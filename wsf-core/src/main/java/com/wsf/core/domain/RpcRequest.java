package com.wsf.core.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 5020614182295106516L;

    /**
     * 请求流水
     */
    private String requestId;

    /**
     * 对应接口类
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法版本
     */
    private String version;

    /**
     * 方法参数类型列表
     */
    private String[] paramterTypes;

    /**
     * 方法参数值列表
     */
    private Object[] parameters;

    /**
     * 客户端应用名称
     */
    private String clientApplicationName;

    /**
     * 客户端ip
     */
    private String clientIp;
}
