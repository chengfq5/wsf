package com.wsf.core.serializer;

public class SerializerFactory {

    public static <T> Serializer getSerializer(Class<T> t) {
        Serializer serializer = null;
        try {
            serializer = (Serializer) t.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return serializer;
    }

}
