package com.wsf.transport.netty.client;

import com.wsf.core.handler.ClientMessageHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ClientReadTimeoutHandler extends ReadTimeoutHandler {

    private ClientMessageHandler handler;

    private long timeout;

    public ClientReadTimeoutHandler(long timeout, TimeUnit unit) {
        super(timeout, unit);
    }

    public ClientReadTimeoutHandler(ClientMessageHandler handler, long timeout, TimeUnit unit) {
        super(timeout, unit);
        this.handler = handler;
        this.timeout = timeout;
    }

    @Override
    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        handler.channelInactive();
        super.readTimedOut(ctx);
    }
}
