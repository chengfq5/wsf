package com.wsf.core.util;

import com.wsf.core.constants.LoadBalancePolicy;
import com.wsf.core.model.Refrence;
import com.wsf.core.model.Service;

import java.net.InetAddress;
import java.util.List;
import java.util.Random;

/**
 * 负载均衡
 *
 * @author kangyonggan
 * @since 2019-02-18
 */
public final class LoadBalanceUtil {

    private LoadBalanceUtil() {
    }

    /**
     * 根据负载均衡策略获取服务
     *
     * @param refrence
     * @param loadBalance
     * @return
     * @throws Exception
     */
    public static Service getService(Refrence refrence, String loadBalance) throws Exception {
        List<Service> services = refrence.getServices();
        if (services.isEmpty()) {
            throw new RuntimeException("没有可用的服务");
        }

        long count = refrence.getRefCount();

        if (LoadBalancePolicy.POLL.getName().equals(loadBalance)) {
            // 轮询
            return poll(count, services);
        } else if (LoadBalancePolicy.RANDOM.getName().equals(loadBalance)) {
            // 随机
            return random(services);
        } else if (LoadBalancePolicy.SOURCE_HASH.getName().equals(loadBalance)) {
            // 源地址哈希
            return sourceHash(services);
        } else if (LoadBalancePolicy.WEIGHT_POLL.getName().equals(loadBalance)) {
            // 加权轮询
            throw new RuntimeException("暂不支持加权轮询策略");
        } else if (LoadBalancePolicy.WEIGHT_RANDOM.getName().equals(loadBalance)) {
            // 加权随机
            throw new RuntimeException("暂不支持加权随机策略");
        }
        return null;
    }

    /**
     * 源地址哈希
     *
     * @param services
     * @return
     * @throws Exception
     */
    private static Service sourceHash(List<Service> services) throws Exception {
        String localIp = InetAddress.getLocalHost().getHostAddress();
        return services.get(localIp.hashCode() % services.size());
    }

    /**
     * 随机
     *
     * @param services
     * @return
     */
    private static Service random(List<Service> services) {
        return services.get(new Random().nextInt(services.size()));
    }

    /**
     * 轮询
     *
     * @param refCount
     * @param services
     * @return
     */
    private static Service poll(long refCount, List<Service> services) {
        long index = refCount % services.size();
        return services.get((int) index);
    }
}
