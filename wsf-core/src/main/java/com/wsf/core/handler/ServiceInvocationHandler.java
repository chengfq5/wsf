package com.wsf.core.handler;


import com.wsf.core.constants.FaultPolicy;
import com.wsf.core.domain.RpcClient;
import com.wsf.core.domain.RpcContext;
import com.wsf.core.domain.RpcRequest;
import com.wsf.core.domain.RpcResponse;
import com.wsf.core.model.Refrence;
import com.wsf.core.util.AsynTaskUtil;
import com.wsf.core.util.TypeParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 服务远程代理Handler
 */
public class ServiceInvocationHandler implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(ClientMessageHandlerImpl.class);

    private Refrence refrence;

    public ServiceInvocationHandler(Refrence refrence) {
        this.refrence = refrence;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoke(method.getName(), method.getParameterTypes(), args, method.getReturnType());
    }

    public Object invoke(String methodName, Class[] argTypes, Object[] args, Class<?> returnType) throws Throwable {
        RpcRequest request = new RpcRequest();
        //公用参数
        request.setRequestId(RpcContext.getUuid().get());
        request.setClientApplicationName(RpcContext.getApplicationName());
        request.setClientIp(RpcContext.getLocalIp());

        //业务参数
        request.setClassName(refrence.getInterfaceName());
        request.setVersion(refrence.getVersion());
        request.setMethodName(methodName);
        request.setParamterTypes(getTypes(argTypes));
        request.setParameters(args);

        Object result = null;
        if (refrence.isAsync()) {
            FutureTask<Object> futureTask = AsynTaskUtil.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    try {
                        return doService(refrence, request, returnType);
                    } catch (Throwable e) {
                        logger.error("异步调用发生异常", e);
                        throw new RuntimeException(e);
                    }
                }
            });

            RpcContext.getFutureTask().set(futureTask);
            if (TypeParseUtil.isBasicType(returnType)) {
                result = TypeParseUtil.getBasicTypeDefaultValue(returnType);
            }
            return result;

        }
        return doService(refrence, request, returnType);
    }

    public Object doService(Refrence refrence, RpcRequest request, Class<?> returnType) throws Throwable {
        Date beginDate = new Date();
        try {
            RpcClient rpcClient = new RpcClient(refrence);
            RpcResponse response = rpcClient.remoteCall(request);
            if (response != null) {
                if (response.getError() != null) {
                    throw new RuntimeException(response.getError());
                } else {
                    return response.getResult();
                }
            }
            return null;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            if (refrence.getFault().equals(FaultPolicy.FAIL_FAST.getName())) {
                // 快速失败
                logger.info("远程调用失败，使用快速失败策略");
                throw e;
            } else if (refrence.getFault().equals(FaultPolicy.FAIL_RETRY.getName())) {
                // 失败重试
                logger.info("远程调用失败，使用失败重试策略");
                return new RpcClient(refrence).remoteCall(request);
            } else if (refrence.getFault().equals(FaultPolicy.FAIL_SAFE.getName())) {
                // 失败安全
                logger.info("远程调用失败，使用失败安全策略");
                // 判断基础类型，返回默认值，否则自动转换会报空指针
                if (TypeParseUtil.isBasicType(returnType)) {
                    return TypeParseUtil.getBasicTypeDefaultValue(returnType);
                }
                return null;
            }
            logger.error("远程调用失败，暂不支持此容错策略");
            throw e;
        } finally {

        }
    }

    /**
     * 获取参数类型名称列表
     *
     * @param parameterTypes
     * @return
     */
    public String[] getTypes(Class<?>[] parameterTypes) {
        String[] types = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            types[i] = parameterTypes[i].getName();
        }
        return types;
    }
}
