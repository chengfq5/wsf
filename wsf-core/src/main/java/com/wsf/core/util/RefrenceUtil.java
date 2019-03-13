package com.wsf.core.util;


import com.wsf.core.model.Refrence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户端引用操作类
 */
public class RefrenceUtil {

    private static Map<String, Refrence> refrenceMap = new HashMap<>();

    public static void put(Refrence refrence) {
        refrenceMap.put(refrence.getInterfaceName(), refrence);
    }

    public static Refrence get(String interfaceName) throws Exception {
        Refrence refrence = refrenceMap.get(interfaceName);
        if (refrence == null) {
            synchronized (refrenceMap) {
                refrence = new Refrence();
                refrence.setInterfaceName(interfaceName);
                refrence.init();
                refrenceMap.put(interfaceName, refrence);
            }
        }
        return refrence;
    }

    public static boolean exists(String interfaceName) {
        return refrenceMap.containsKey(interfaceName);
    }

    public static List<Refrence> getAll() {
        return new ArrayList<>(refrenceMap.values());
    }
}
