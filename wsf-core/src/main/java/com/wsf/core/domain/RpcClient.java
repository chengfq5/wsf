package com.wsf.core.domain;


import com.wsf.core.channel.ClientChannel;
import com.wsf.core.constants.RpcModelEnum;
import com.wsf.core.exceptions.WsfRpcException;
import com.wsf.core.handler.ClientMessageHandler;
import com.wsf.core.handler.ClientMessageHandlerImpl;
import com.wsf.core.model.Client;
import com.wsf.core.model.Refrence;
import com.wsf.core.model.Service;
import com.wsf.core.serializer.Serializer;
import com.wsf.core.util.ClientChannelPool;
import com.wsf.core.util.LoadBalanceUtil;
import com.wsf.core.util.ServiceLoadUtil;
import com.wsf.core.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;

public class RpcClient {

    private static Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private Refrence refrence;

    private ClientChannel clientChannel;

    public RpcClient(Refrence refrence) {
        this.refrence = refrence;
        init();
    }

    /**
     * 初始化，连接远程端口
     */
    public void init() {
        String remoteIp = null;
        Integer remotePort = 0;
        try {
            // 如果是点对点服务，不走负载均衡
            if (!StringUtils.isEmpty(refrence.getDirectServerIp())) {
                remoteIp = refrence.getDirectServerIp();
                remotePort = refrence.getDirectServerPort();
            } else {
                // 获取负载均衡策略
                Client client = SpringUtil.getBean(RpcModelEnum.client.name());
                logger.debug("客户端负载均衡策略:" + client.getLoadBalance());
                Service service = LoadBalanceUtil.getService(refrence, client.getLoadBalance());
                remoteIp = service.getIp();
                remotePort = service.getPort();
            }

            String channelKey = refrence.getInterfaceName() + "_" + remoteIp + "_" + remotePort;
            clientChannel = ClientChannelPool.get(channelKey);
            if (clientChannel == null) {
                synchronized (ClientChannelPool.getLocks()) {
                    clientChannel = ClientChannelPool.get(channelKey);
                    if (clientChannel == null) {
                        clientChannel = ServiceLoadUtil.getProvider(ClientChannel.class);
                        ClientMessageHandler clientMessageHandler = new ClientMessageHandlerImpl(clientChannel, refrence.getTimeout());
                        Serializer serializer = ServiceLoadUtil.getProvider(Serializer.class);
                        clientChannel.start(channelKey, clientMessageHandler, serializer, InetSocketAddress.createUnresolved(remoteIp, remotePort), refrence.getTimeout());
                        ClientChannelPool.put(channelKey, clientChannel);
                        logger.info("连接远程服务[{}]成功", channelKey);
                    }
                }
            }

        } catch (Exception e) {
            throw new WsfRpcException("ClientChannel init failed", e);
        }
    }

    public RpcResponse remoteCall(RpcRequest request) throws InterruptedException {
        return clientChannel.getMessageHandler().send(request);
    }
}
