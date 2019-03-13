package com.wsf.transport.netty.codec;

import com.wsf.core.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * netty解码实现类
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private Logger logger = LoggerFactory.getLogger(getClass());
    //判断传送客户端传送过来的数据是否按照协议传输，头部信息的大小应该是 4
    private static final int HEADER_SIZE = 4;

    private Serializer serializer;
    private Class clazz;

    public NettyMessageDecoder(Class clazz, Serializer serializer, int maxFrameLength, int lengthFieldOffset,
                               int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        //可读字节小样头部长度4，下次读
        if (in.readableBytes() < HEADER_SIZE) {
            return null;
        }

        //标记读位置
        in.markReaderIndex();
        //注意在读的过程中，readIndex的指针也在移动
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            //小于数据包长度，复位读位置，下次读
            in.resetReaderIndex();
            return null;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        try {
            return serializer.decode(data, clazz);
        } catch (Exception e) {
            throw new RuntimeException("serializer decode error");
        }
    }
}

