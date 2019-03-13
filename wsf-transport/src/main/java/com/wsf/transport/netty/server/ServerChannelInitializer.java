package com.wsf.transport.netty.server;

import com.wsf.core.handler.ServerMessageHandler;
import com.wsf.core.domain.RpcRequest;
import com.wsf.core.domain.RpcResponse;
import com.wsf.core.serializer.Serializer;
import com.wsf.transport.netty.codec.NettyMessageDecoder;
import com.wsf.transport.netty.codec.NettyMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(ServerChannelInitializer.class);

    private ServerMessageHandler messageHandler;

    private Executor executor;

    private Serializer serializer;

    public ServerChannelInitializer(ServerMessageHandler messageHandler, Executor executor, Serializer serializer) {
        this.messageHandler = messageHandler;
        this.executor = executor;
        this.serializer = serializer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        logger.debug("ServerChannelInitializer initChannel");
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("log", new LoggingHandler(LogLevel.DEBUG));
        pipeline.addLast("decode", new NettyMessageDecoder(RpcRequest.class, serializer, 1 << 20, 0, 4));
        pipeline.addLast("encode", new NettyMessageEncoder(RpcResponse.class, serializer));
        pipeline.addLast("messageHandler", new ServerChannelHandlerImpl(messageHandler, executor));

    }
}
