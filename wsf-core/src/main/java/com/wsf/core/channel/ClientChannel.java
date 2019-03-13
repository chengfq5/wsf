package com.wsf.core.channel;

import com.wsf.core.handler.ClientMessageHandler;
import com.wsf.core.serializer.Serializer;

import java.net.SocketAddress;

public interface ClientChannel extends AbstractChannel {

    /**
     * 开启通道，连接服务端
     *
     * @param messageHandler
     * @param socketAddress
     */
    void start(String channelKey, ClientMessageHandler messageHandler, Serializer serializer, SocketAddress socketAddress, long readTimeOut) throws InterruptedException;

    /**
     * 使用通道发送信息
     *
     * @param object
     */
    void send(Object object);


    /**
     * 获取标识此次连接的通道标识 组成：interfaceName+ip+port
     *
     * @return
     */
    String getChannelKey();


    /**
     * 获取消息处理句柄
     *
     * @return
     */
    ClientMessageHandler getMessageHandler();
}
