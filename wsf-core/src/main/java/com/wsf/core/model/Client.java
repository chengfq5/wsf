package com.wsf.core.model;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;


@Data
public class Client implements InitializingBean {

    private String id;

    /**
     * 负载均衡
     */
    private String loadBalance;

    /**
     * 在spring实例化全部的bean之后执行
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 从注册中心获取降级服务
        //ServiceDegradeUtil.getDegradeServices();

        // 订阅降级服务
        //ServiceDegradeUtil.listenerDegradeServices();
    }
}
