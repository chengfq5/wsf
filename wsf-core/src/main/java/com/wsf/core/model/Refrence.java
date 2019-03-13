
package com.wsf.core.model;

import com.wsf.core.constants.Constant;
import com.wsf.core.constants.RegisterTypeEnum;
import com.wsf.core.constants.RpcModelEnum;
import com.wsf.core.handler.ServiceInvocationHandler;
import com.wsf.core.listener.ServiceChangeListener;
import com.wsf.core.util.RefrenceUtil;
import com.wsf.core.util.SpringUtil;
import com.wsf.core.util.ZookeeperUtil;
import lombok.Data;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@Data
public class Refrence implements ApplicationContextAware, InitializingBean, FactoryBean, Serializable {

    private transient Logger logger = Logger.getLogger(Refrence.class);

    private transient ApplicationContext applicationContext;
    /**
     * 引用标识
     */
    protected String id;

    /**
     * 引用接口名称
     */
    protected String interfaceName;

    /**
     * 指定调用服务版本号
     */
    protected String version;

    /**
     * 服务调用超时，单位(ms)
     */
    protected long timeout;

    /**
     * 直连ip
     */
    protected String directServerIp;

    /**
     * 直连端口
     */
    protected int directServerPort;

    /**
     * 是否使用缓存
     */
    protected String useCache;

    /**
     * 缓存有效期
     */
    protected long cacheTime;

    /**
     * 是否启动异步接收
     */
    protected boolean async;

    /**
     * 容错策略
     */
    protected String fault;

    /**
     * 拦截器
     */
    protected String interceptor;

    /**
     * 调用方本地ip
     */
    protected String ip;

    private transient List<Service> services;

    private AtomicLong refCount = new AtomicLong(0);

    public long getRefCount() {
        return refCount.getAndIncrement();
    }


    @Override
    public Object getObject() throws Exception {
        Class clazz = getObjectType();
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ServiceInvocationHandler(this));
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return Class.forName(interfaceName);
        } catch (ClassNotFoundException ex) {
            logger.error("未找到对应服务：" + interfaceName, ex);
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!applicationContext.containsBean(RpcModelEnum.application.name())) {
            logger.info("没有配置application信息，无法获取服务引用");
            return;
        }

        if (!applicationContext.containsBean(RpcModelEnum.client.name())) {
            logger.info("没有配置client信息，无法获取服务引用");
            return;
        }

        // 点对点服务，不需要配置注册中心
        if (!StringUtils.isEmpty(directServerIp)) {
            logger.info("点对点服务，" + directServerIp + ":" + directServerPort);
            return;
        }

        if (!applicationContext.containsBean(RpcModelEnum.register.name())) {
            logger.info("没有配置注册中心信息，无法获取服务引用");
            return;
        }

        init();
    }

    public void init() throws Exception {
        //注册
        registerRefrence();
        //获取服务列表
        getRefrence();
        //保存，方便别处获取
        RefrenceUtil.put(this);
        //注册服务变化处理事件
        subscribeServiceChange();
    }

    /**
     * 注册引用
     *
     * @throws Exception
     */
    public void registerRefrence() throws Exception {
        this.setIp(InetAddress.getLocalHost().getHostAddress());

        Register register = SpringUtil.getBean(RpcModelEnum.register.name());
        if (register == null) {
            return;
        }

        String path = null;
        if (RegisterTypeEnum.zookeeper.name().equals(register.getType())) {
            ZookeeperUtil client = ZookeeperUtil.getInstance(register.getAddress());
            String basepath = Constant.ZK_BASE_PATH + "/" + interfaceName + "/" + Constant.ZK_CONSUMER_NODE;
            path = basepath + "/" + this.ip;
            // 应用（路径）永久保存
            client.createPath(basepath);

            // 服务(数据)不永久保存，当与zookeeper断开连接20s左右自动删除
            client.saveNode(path, this);
        }

        logger.info("客户端引用发布成功:[" + path + "]");
    }

    /**
     * 初始化可用服务列表
     */
    public void getRefrence() {
        String path = getRegisterServiceBasePath();
        logger.info("正在获取引用服务:[" + path + "]");
        Register register = SpringUtil.getBean(RpcModelEnum.register.name());
        services = new ArrayList<>();
        if (RegisterTypeEnum.zookeeper.name().equals(register.getType())) {
            ZookeeperUtil zookeeperUtil = ZookeeperUtil.getInstance(register.getAddress());
            List<String> childNodes = zookeeperUtil.getChildNodes(path);
            for (String node : childNodes) {
                Service service = (Service) zookeeperUtil.getNode(path + "/" + node);
                if (!StringUtils.isEmpty(version) && !version.equals(service.getVersion())) {
                    continue;
                }
                services.add(service);
            }
        }
    }


    /**
     * 注册服务接口变化事件
     */
    public void subscribeServiceChange() {
        Register register = SpringUtil.getBean(RpcModelEnum.register.name());
        if (register == null) {
            return;
        }

        String path = getRegisterServiceBasePath();
        logger.info("订阅服务变化:[" + path + "]");
        if (RegisterTypeEnum.zookeeper.name().equals(register.getType())) {
            ZookeeperUtil.getInstance(register.getAddress()).subscribeChildChange(path, new ServiceChangeListener(interfaceName));
        }
    }

    /**
     * 接口服务对应的配置中心路径
     *
     * @return
     */
    public String getRegisterServiceBasePath() {
        return Constant.ZK_BASE_PATH + "/" + this.interfaceName + "/" + Constant.ZK_PROVICER_NODE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
