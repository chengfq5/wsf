
package com.wsf.core.model;

import com.wsf.core.domain.RpcServer;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;


@Data
public class Server implements InitializingBean {

    /**
     * 服务方标识
     */
    protected String id;

    /**
     * 服务启动(RPC)端口
     */
    protected Integer port;


    private transient RpcServer rpcServer;


    @Override
    public void afterPropertiesSet() throws Exception {
        rpcServer = new RpcServer(port);
        rpcServer.start();
    }

    @Override
    protected void finalize() throws Throwable {
        rpcServer.shutdown();
        super.finalize();
    }
}
