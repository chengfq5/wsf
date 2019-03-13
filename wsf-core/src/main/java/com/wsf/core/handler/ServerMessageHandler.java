package com.wsf.core.handler;

public interface ServerMessageHandler {

    void receive(Object object, SendMessage sendMessage);
}
