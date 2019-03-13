package com.wsf.core.handler;

import com.wsf.core.domain.RpcResponse;

public interface ClientMessageHandler {
    void receive(Object object);

    RpcResponse send(Object object) throws InterruptedException;

    void channelInactive();
}
