package com.wsf.core.handler;

import com.google.common.collect.Maps;
import com.wsf.core.channel.ClientChannel;
import com.wsf.core.domain.RpcRequest;
import com.wsf.core.domain.RpcResponse;
import com.wsf.core.util.ClientChannelPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ClientMessageHandlerImpl implements ClientMessageHandler {
    private static Logger logger = LoggerFactory.getLogger(ClientMessageHandlerImpl.class);

    private ClientChannel clientChannel;

    private Map<String, BlockingQueue<RpcResponse>> requestMap;

    /**
     * 服务超时时间，默认30s
     */
    private long timeout = 30 * 1000;

    public ClientMessageHandlerImpl(ClientChannel clientChannel, long timeout) {
        this.clientChannel = clientChannel;
        this.timeout = timeout;
        requestMap = Maps.newConcurrentMap();
    }

    /**
     * 收到数据加入BlockingQueue，通知发送端处理消息
     * @param object
     */
    @Override
    public void receive(Object object) {
        RpcResponse response = (RpcResponse) object;
        if (response != null && !StringUtils.isEmpty(response.getRequestId())) {
            BlockingQueue<RpcResponse> queue = requestMap.remove(response.getRequestId());
            queue.add(response);
        } else {
            logger.error("Receive message error, no data found, reuquestid:{}", response.getRequestId());
        }
    }

    /**
     * 发送数据，利用BlockingQueue阻塞接收消息
     * @param object
     * @return
     * @throws InterruptedException
     */
    @Override
    public RpcResponse send(Object object) throws InterruptedException {
        RpcRequest request = (RpcRequest) object;
        final BlockingQueue<RpcResponse> queue = new LinkedBlockingDeque<>();
        requestMap.put(request.getRequestId(), queue);
        clientChannel.send(request);
        RpcResponse response = queue.poll(timeout, TimeUnit.MILLISECONDS);
        if (response != null) {
            return response;
        } else {
            throw new RuntimeException("Request wait response timeout, spend-millsecs:" + timeout);
        }
    }

    @Override
    public void channelInactive() {
        ClientChannelPool.remove(clientChannel.getChannelKey());
    }
}
