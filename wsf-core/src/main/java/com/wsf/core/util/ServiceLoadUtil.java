package com.wsf.core.util;

import com.wsf.core.exceptions.WsfRpcException;

import java.util.ServiceLoader;

public class ServiceLoadUtil {

    public static <T> T getProvider(Class<T> type) {
        T result = null;
        for (T service : ServiceLoader.load(type)) {
            if (result != null) {
                throw new WsfRpcException(type.getSimpleName() + " is not allow multiple!");
            }
            result = service;
        }

        if (result != null) {
            return result;
        }
        throw new WsfRpcException(type.getSimpleName() + " cantnot be found");
    }
}
