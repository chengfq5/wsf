
package com.wsf.core.model;

import com.wsf.core.constants.Constant;
import com.wsf.core.constants.RegisterTypeEnum;
import com.wsf.core.constants.RpcModelEnum;
import com.wsf.core.exceptions.WsfRpcException;
import com.wsf.core.util.SpringUtil;
import com.wsf.core.util.ZookeeperUtil;
import lombok.Data;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.net.InetAddress;


@Data
public class Service implements ApplicationContextAware, InitializingBean, Serializable {

    private static final long serialVersionUID = -5139604441535216353L;

    private transient Logger logger = Logger.getLogger(Service.class);

    protected String id;

    protected String interfaceName;

    protected String impl;

    protected String ref;

    protected String ip;

    protected int port;

    protected String version;

    private transient ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!applicationContext.containsBean(RpcModelEnum.application.name())) {
            logger.info("没有配置application，不发布到注册中心");
            return;
        }
        if (!applicationContext.containsBean(RpcModelEnum.server.name())) {
            logger.info("没有配置server，不发布到注册中心");
            return;
        }
        if (!applicationContext.containsBean(RpcModelEnum.register.name())) {
            logger.info("没有配置register，不发布到注册中心");
            return;
        }

        registerService();
    }

    /**
     * 发布服务
     *
     * @throws Exception
     */
    public void registerService() throws Exception {
        Server server = SpringUtil.getBean(RpcModelEnum.server.name());
        if (server == null) {
            throw new WsfRpcException("服务提供方未配置server相关信息");
        }

        this.setIp(InetAddress.getLocalHost().getHostAddress());
        this.setPort(server.getPort());


        Register register = SpringUtil.getBean(RpcModelEnum.register.name());
        if (register == null) {
            return;
        }

        //注册服务
        String path = null;
        if (RegisterTypeEnum.zookeeper.name().equals(register.getType())) {
            ZookeeperUtil client = ZookeeperUtil.getInstance(register.getAddress());
            String basepath = Constant.ZK_BASE_PATH + "/" + interfaceName + "/" + Constant.ZK_PROVICER_NODE;
            path = basepath + "/" + ip + "_" + port;
            client.createPath(basepath);
            client.saveNode(path, this);
        }

        logger.info("服务发布成功:[" + path + "]");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
