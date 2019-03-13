package com.wsf.transport.netty.client;

import com.wsf.core.channel.ClientChannel;
import com.wsf.core.exceptions.WsfRpcException;
import com.wsf.core.handler.ClientMessageHandler;
import com.wsf.core.serializer.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * 客户端发送通道管理
 */
public class NettyClientChannel implements ClientChannel {

    private static Logger logger = LoggerFactory.getLogger(NettyClientChannel.class);

    private EventLoopGroup workGroup;

    private ClientMessageHandler messageHandler;

    private Channel channel;

    private String channelKey;

    @Override
    public void start(String channelKey, ClientMessageHandler clientMessageHandler, Serializer serializer, SocketAddress socketAddress, long readTimeOut) {
        this.messageHandler = clientMessageHandler;
        this.channelKey = channelKey;
        workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ClientChannelInitializer(clientMessageHandler, serializer, readTimeOut))
                    .connect(socketAddress)
                    .sync().await();
        } catch (InterruptedException e) {
            logger.error("Connect server:[{}] failed", socketAddress);
            throw new WsfRpcException("Connect server failed, serveraddr:" + socketAddress, e);
        }
        channel = channelFuture.channel();
    }

    @Override
    public void send(Object object) {
        channel.writeAndFlush(object).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    channel.read();
                } else {
                    logger.error("Message send failed, channel:{}, msg:{}", channel, object, channelFuture.cause());
                }
            }
        });
    }

    @Override
    public String getChannelKey() {
        return channelKey;
    }

    @Override
    public ClientMessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    @Override
    public void shutdown() {
        if (channel == null || !channel.isOpen()) {
            return;
        }

        try {
            channel.close();
        } catch (Exception e) {
            logger.error("Close NettyClientChannel failed", e);
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.shutdown();
        super.finalize();
    }
}
