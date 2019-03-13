package com.wsf.core.domain;


import com.wsf.core.channel.ServerChannel;
import com.wsf.core.exceptions.WsfRpcException;
import com.wsf.core.handler.ServerMessageHandler;
import com.wsf.core.handler.ServerMessageHandlerImpl;
import com.wsf.core.serializer.Serializer;
import com.wsf.core.util.ExecutorBuilder;
import com.wsf.core.util.ServiceLoadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;

public class RpcServer extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private Integer port;

    private ServerChannel serverChannel;

    public RpcServer(Integer port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerMessageHandler messageHandler = new ServerMessageHandlerImpl();
        Serializer serializer = ServiceLoadUtil.getProvider(Serializer.class);
        Executor executor = ExecutorBuilder.executorBuild("wsfrpc-business-executor-%d", true);
        serverChannel = ServiceLoadUtil.getProvider(ServerChannel.class);
        try {
            serverChannel.start(port, executor, messageHandler, serializer);
        } catch (IOException e) {
            logger.error("RpcServer serverChannel init failed", e);
            throw new WsfRpcException("RpcServer serverChannel init failed", e);
        }
    }

    public void shutdown() {
        serverChannel.shutdown();
    }
}
