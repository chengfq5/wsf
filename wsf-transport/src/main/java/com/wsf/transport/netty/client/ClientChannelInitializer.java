package com.wsf.transport.netty.client;

import com.wsf.core.domain.RpcRequest;
import com.wsf.core.domain.RpcResponse;
import com.wsf.core.handler.ClientMessageHandler;
import com.wsf.core.serializer.Serializer;
import com.wsf.transport.netty.codec.NettyMessageDecoder;
import com.wsf.transport.netty.codec.NettyMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(ClientChannelInitializer.class);

    private ClientMessageHandler clientMessageHandler;

    private Serializer serializer;

    private long readTimeOut;

    public ClientChannelInitializer(ClientMessageHandler clientMessageHandler, Serializer serializer, long readTimeOut) {
        this.clientMessageHandler = clientMessageHandler;
        this.serializer = serializer;
        this.readTimeOut = readTimeOut;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        logger.debug("ClientChannelInitializer initChannel");
        socketChannel.pipeline().addLast("log", new LoggingHandler(LogLevel.DEBUG));
        socketChannel.pipeline().addLast("decode", new NettyMessageDecoder(RpcResponse.class, serializer, 1 << 20, 0, 4));
        socketChannel.pipeline().addLast("encode", new NettyMessageEncoder(RpcRequest.class, serializer));
        //读超时处理器，长时间未有数据读，关闭通道
        //socketChannel.pipeline().addLast("timeoutHandler", new ClientReadTimeoutHandler(clientMessageHandler, readTimeOut, TimeUnit.MILLISECONDS));
        socketChannel.pipeline().addLast("messageHandler", new ClientChannelHandlerImpl(clientMessageHandler));
    }
}
