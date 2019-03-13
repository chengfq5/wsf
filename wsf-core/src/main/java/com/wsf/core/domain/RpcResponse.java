package com.wsf.core.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = 1505168822590402757L;

    /**
     * 请求流水
     */
    private String requestId;

    /**
     * 处理成功标识
     */
    private Boolean isSuccess;

    /**
     * 响应结果
     */
    private Object result;

    /**
     * 异常信息
     */
    private Throwable error;


}
