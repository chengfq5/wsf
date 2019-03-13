package com.wsf.transport.netty.server;

import com.wsf.core.channel.ServerChannel;
import com.wsf.core.handler.ServerMessageHandler;
import com.wsf.core.serializer.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * 服务端发送通道管理
 */
public class NettyServerChannel implements ServerChannel {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerChannel.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    @Override
    public void start(int port, Executor executor, ServerMessageHandler messageHandler, Serializer serializer) throws IOException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture channelFuture = serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerChannelInitializer(messageHandler, executor, serializer))
                //.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .bind(port);
        try {
            channelFuture.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            logger.error("启动失败...", ex);
            throw new RuntimeException("Interrupted waiting for bind");
        }
        if (!channelFuture.isSuccess()) {
            logger.error("启动失败...", channelFuture.cause());
            throw new IOException("Failed to bind", channelFuture.cause());
        }
        channel = channelFuture.channel();
    }


    @Override
    public void shutdown() {
        if (channel == null || !channel.isOpen()) {
            return;
        }

        try {
            channel.close();
        } catch (Exception e) {
            logger.error("Close NettyServerChannel failed", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
