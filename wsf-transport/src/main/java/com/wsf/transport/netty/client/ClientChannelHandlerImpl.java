package com.wsf.transport.netty.client;

import com.wsf.core.domain.RpcResponse;
import com.wsf.core.handler.ClientMessageHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端通道处理句柄
 */
public class ClientChannelHandlerImpl extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientChannelHandlerImpl.class);

    private Channel outboundChannel;

    private ClientMessageHandler messageHandler;

    public ClientChannelHandlerImpl(ClientMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * 链路激活，连接成功
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        outboundChannel = ctx.channel();
        ctx.read();
    }

    /**
     * 有数据读状态
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        messageHandler.receive((RpcResponse) msg);
        ReferenceCountUtil.release(msg);
    }

    /**
     * 链路异常，强制将发送缓冲区flush到通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("MessageHandler异常", cause);
        if (outboundChannel.isActive()){
            outboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 链路断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        messageHandler.channelInactive();
        super.channelInactive(ctx);
    }
}
