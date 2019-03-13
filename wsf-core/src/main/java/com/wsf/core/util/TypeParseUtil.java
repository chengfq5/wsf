package com.wsf.core.util;

import java.util.HashMap;
import java.util.Map;

public class TypeParseUtil {

    private static char DEFAULT_CHAR;

    private static Map<String, Object> basicTypeDefValMap = new HashMap<String, Object>() {
        {
            put("byte", 0);
            put("short", 0);
            put("int", 0);
            put("long", 0);
            put("float", 0.0);
            put("double", 0.0);
            put("boolean", false);
            put("char", DEFAULT_CHAR);
        }
    };

    private static Map<String, Class> basicTypeDefClassMap = new HashMap<String, Class>() {
        {
            put("byte", byte.class);
            put("short", short.class);
            put("int", int.class);
            put("long", long.class);
            put("float", float.class);
            put("double", double.class);
            put("boolean", boolean.class);
            put("char", char.class);
        }
    };

    private TypeParseUtil() {

    }

    /**
     * 转类型字符串到类型对象
     *
     * @param types
     * @return
     * @throws Throwable
     */
    public static Map<String, Object> parseTypeString2Class(String[] types, Object[] args) throws Throwable {
        Map<String, Object> result = new HashMap<>(types.length);
        Class<?>[] classTypes = new Class<?>[types.length];
        for (int i = 0; i < types.length; i++) {
            if (basicTypeDefValMap.containsKey(types[i])) {
                classTypes[i] = basicTypeDefClassMap.get(types[i]);
                if (null == args[i]) {
                    args[i] = getBasicTypeDefaultValue(types[i]);
                }
            } else {
                classTypes[i] = Class.forName(types[i]);
            }
        }

        result.put("classTypes", classTypes);
        result.put("args", args);
        return result;
    }

    /**
     * 判断类型是否基本类型
     *
     * @param type
     * @return
     */
    public static boolean isBasicType(Class<?> type) {
        return basicTypeDefValMap.containsKey(type.getName());
    }

    public static Object getBasicTypeDefaultValue(Class<?> type) {
        return basicTypeDefValMap.get(type.getName());
    }

    public static Object getBasicTypeDefaultValue(String type) {
        return basicTypeDefValMap.get(type);
    }
}
