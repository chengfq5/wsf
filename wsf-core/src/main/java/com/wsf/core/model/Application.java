
package com.wsf.core.model;

import com.wsf.core.constants.Constant;
import com.wsf.core.constants.RegisterTypeEnum;
import com.wsf.core.constants.RpcModelEnum;
import com.wsf.core.domain.RpcContext;
import com.wsf.core.util.SpringUtil;
import com.wsf.core.util.ZookeeperUtil;
import lombok.Data;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.util.Map;


/**
 * 客户端应用
 */
@Data
public class Application implements ApplicationContextAware, InitializingBean {
    private transient Logger logger = Logger.getLogger(Application.class);

    /**
     * 应用标识
     */
    protected String id;

    /**
     * 应用名称
     */
    protected String name;

    private transient ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        //优雅停机
        addShutdownHook();

        //设置上下文环境
        RpcContext.setApplicationName(this.name);
        RpcContext.setLocalIp(InetAddress.getLocalHost().getHostAddress());
    }

    /**
     * 停机钩子
     */
    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (SpringUtil.containsBean(RpcModelEnum.server.name())) {
                    //从注册中心注销本服务包含的service
                    unregisterServer();
                }

                if (SpringUtil.containsBean(RpcModelEnum.client.name())) {
                    //从注册中心注销客户端包含的refrence
                    unregisterClient();
                }
            }
        });
    }

    /**
     * 服务停止前，从注册中心注销应用下相关服务
     */
    public void unregisterServer() {
        Register register = SpringUtil.getBean(RpcModelEnum.register.name());
        if (register == null) {
            return;
        }

        Map<String, Service> serviceMap = SpringUtil.getBeanOfType(Service.class);
        for (String key : serviceMap.keySet()) {
            if (RegisterTypeEnum.zookeeper.name().equals(register.getType())) {
                ZookeeperUtil client = ZookeeperUtil.getInstance(register.getAddress());
                Service service = serviceMap.get(key);
                String path = Constant.ZK_BASE_PATH + "/" + service.getInterfaceName() + "/" + Constant.ZK_PROVICER_NODE + "/" + service.getIp() + "_" + service.getPort();
                client.deletePath(path);
                logger.info("注销服务：" + path);
            }
        }

        logger.info("注销应用" + this.getName() + "下相关服务成功");
    }

    /**
     * 服务停止前，从注册中心注销应用下相关引用
     */
    public void unregisterClient() {
        Register register = SpringUtil.getBean(RpcModelEnum.register.name());
        if (register == null) {
            return;
        }

        Map<String, Refrence> refrenceMap = SpringUtil.getBeanOfType(Refrence.class);
        for (String key : refrenceMap.keySet()) {
            if (RegisterTypeEnum.zookeeper.name().equals(register.getType())) {
                ZookeeperUtil client = ZookeeperUtil.getInstance(register.getAddress());
                Refrence refrence = refrenceMap.get(key);
                String path = Constant.ZK_BASE_PATH + "/" + refrence.getInterfaceName() + "/" + Constant.ZK_CONSUMER_NODE + "/" + refrence.getIp();
                client.deletePath(path);
                logger.info("注销客户端引用：" + path);
            }
        }

        logger.info("注销应用" + this.getName() + "下相关引用成功");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SpringUtil.setApplicationContext(applicationContext);
    }
}
