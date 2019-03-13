package com.wsf.core.domain;

import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.FutureTask;

public class RpcContext {

    private static String applicationName;

    private static String localIp;

    /**
     * 线程唯一标识
     */
    @Getter
    private static ThreadLocal<String> uuid = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    /**
     * 异步任务
     */
    @Getter
    private static ThreadLocal<FutureTask<Object>> futureTask = new ThreadLocal<>();

    public static void setApplicationName(String applicationName) {
        RpcContext.applicationName = applicationName;
    }

    public static String getApplicationName() {
        return RpcContext.applicationName;
    }

    public static void setLocalIp(String localIp) {
        RpcContext.localIp = localIp;
    }

    public static String getLocalIp() {
        return RpcContext.localIp;
    }
}
