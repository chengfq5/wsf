package com.wsf.core.handler;

import com.wsf.core.domain.RpcRequest;
import com.wsf.core.domain.RpcResponse;
import com.wsf.core.model.Service;
import com.wsf.core.util.SpringUtil;
import com.wsf.core.util.TypeParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServerMessageHandlerImpl implements ServerMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerMessageHandlerImpl.class);

    @Override
    public void receive(Object object, SendMessage sendMessage) {
        RpcResponse rpcResponse = new RpcResponse();
        try {
            RpcRequest rpcRequest = (RpcRequest) object;
            rpcResponse.setRequestId(rpcRequest.getRequestId());
            logger.debug("RPC服务端收到消息:{}", rpcRequest);

            // 获取本地服务
            Map<String, Service> serviceMap = SpringUtil.getBeanOfType(Service.class);
            Service service = null;
            for (String key : serviceMap.keySet()) {
                if (serviceMap.get(key).getInterfaceName().equals(rpcRequest.getClassName())) {
                    service = serviceMap.get(key);
                    break;
                }
            }
            if (service == null) {
                throw new RuntimeException("没有找到服务:" + rpcRequest.getClassName());
            }

            // 获取服务的实现类
            Object serviceImpl = SpringUtil.getBean(service.getRef());
            if (serviceImpl == null) {
                throw new RuntimeException("没有找到服务:" + rpcRequest.getClassName());
            }

            // 转换参数和参数类型
            Map<String, Object> map = TypeParseUtil.parseTypeString2Class(rpcRequest.getParamterTypes(), rpcRequest.getParameters());
            Class<?>[] classTypes = (Class<?>[]) map.get("classTypes");
            Object[] args = (Object[]) map.get("args");

            // 反射获取返回值
            Object result = serviceImpl.getClass().getMethod(rpcRequest.getMethodName(), classTypes).invoke(serviceImpl, args);
            rpcResponse.setResult(result);
            rpcResponse.setIsSuccess(true);
        } catch (Throwable e) {
            logger.error("服务端接收消息发送异常", e);
            rpcResponse.setIsSuccess(false);
            rpcResponse.setError(e);
        }
        sendMessage.send(rpcResponse);
    }
}
