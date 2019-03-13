package com.wsf.transport.netty.codec;

import com.wsf.core.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * netty编码实现类
 */
public final class NettyMessageEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    private Class clazz;

    public NettyMessageEncoder(Class clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try {
            byte[] data = serializer.encode(msg);
            //4字节头部长度
            out.writeInt(data.length);
            out.writeBytes(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

