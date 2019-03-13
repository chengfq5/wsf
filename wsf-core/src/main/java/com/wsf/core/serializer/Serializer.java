package com.wsf.core.serializer;

import java.io.IOException;

/**
 * 编解码接口类
 */
public interface Serializer {

    byte[] encode(Object msg) throws IOException;

    <T> T decode(byte[] buf, Class<T> type) throws IOException;

}
