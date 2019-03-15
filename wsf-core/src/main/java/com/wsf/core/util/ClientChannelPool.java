package com.wsf.core.util;

import com.google.common.collect.Maps;
import com.wsf.core.channel.ClientChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ClientChannelPool {
    private static Logger logger = LoggerFactory.getLogger(ClientChannelPool.class);

    private static Map<String, ClientChannel> channelMap = Maps.newConcurrentMap();

    public static Map<String, ClientChannel> getLocks(){
        return ClientChannelPool.channelMap;
    }

    public static void put(String key, ClientChannel channel) {
        channelMap.put(key, channel);
    }

    public static ClientChannel get(String key) {
        return channelMap.get(key);
    }

    public static ClientChannel remove(String key) {
        ClientChannel channel = channelMap.remove(key);
        logger.info("ClientChannel[{}] is removed", key);
        return channel;
    }
}
