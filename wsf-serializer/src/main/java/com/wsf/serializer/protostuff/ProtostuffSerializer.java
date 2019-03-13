package com.wsf.serializer.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wsf.core.serializer.Serializer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Protostuff序列化实现
 */
public class ProtostuffSerializer implements Serializer {

    //构建schema的过程可能会比较耗时，因此希望使用过的类对应的schema能被缓存起来
    private static final LoadingCache<Class<?>, Schema<?>> schemas = CacheBuilder.newBuilder()
            .build(new CacheLoader<Class<?>, Schema<?>>() {
                @Override
                public Schema<?> load(Class<?> cls) throws Exception {
                    return RuntimeSchema.createFrom(cls);
                }
            });

    @Override
    public byte[] encode(Object msg) throws IOException {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema schema = getSchema(msg.getClass());
            byte[] arr = ProtostuffIOUtil.toByteArray(msg, schema, buffer);
            return arr;
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T decode(byte[] buf, Class<T> type) throws IOException {
        Schema<T> schema = getSchema(type);
        T msg = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(buf, msg, schema);
        return (T) msg;
    }

    private static Schema getSchema(Class<?> cls) throws IOException {
        try {
            return schemas.get(cls);
        } catch (ExecutionException e) {
            throw new IOException("create protostuff schema error", e);
        }
    }

}
