package com.wsf.transport.netty.server;

import com.wsf.core.handler.SendMessage;
import com.wsf.core.handler.ServerMessageHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class ServerChannelHandlerImpl extends ChannelInboundHandlerAdapter implements SendMessage {
    private static final Logger logger = LoggerFactory.getLogger(ServerChannelHandlerImpl.class);

    private ServerMessageHandler messageHandler;

    private Channel outboundChannel;

    private Executor executor;

    public ServerChannelHandlerImpl(ServerMessageHandler messageHandler, Executor executor) {
        this.messageHandler = messageHandler;
        this.executor = executor;
    }


    @Override
    public void send(Object object) {
        outboundChannel.writeAndFlush(object).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    //数据发送成功，标记通道读状态
                    outboundChannel.read();
                } else {
                    logger.error("outboundChannel={}, sendMsg={}", outboundChannel, object, channelFuture.cause());
                }
            }
        });
    }

    /**
     * 链路激活
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        outboundChannel = ctx.channel();
        logger.info("ServerMessageHandlerImpl  init ");
        ctx.read();
    }

    /**
     * 收到数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        executor.execute(() -> messageHandler.receive(msg, this));
    }

    /**
     * 链路异常，强制将发送缓冲区flush到通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("MessageHandler异常: {}", cause.getMessage());
        if (outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

    }
}
