package com.wsf.core.channel;

import com.wsf.core.handler.ServerMessageHandler;
import com.wsf.core.serializer.Serializer;

import java.io.IOException;
import java.util.concurrent.Executor;

public interface ServerChannel extends AbstractChannel {

    void start(int port, Executor executor, ServerMessageHandler messageHandler, Serializer serializer) throws IOException;
}
