package com.wsf.core.xsd;

import com.wsf.core.constants.RpcModelEnum;
import com.wsf.core.model.Client;
import com.wsf.core.model.Application;
import com.wsf.core.model.Refrence;
import com.wsf.core.model.Register;
import com.wsf.core.model.Server;
import com.wsf.core.model.Service;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RpcNameSpaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser(RpcModelEnum.register.name(), new RpcBeanDefinitionParser(Register.class));
        registerBeanDefinitionParser(RpcModelEnum.application.name(), new RpcBeanDefinitionParser(Application.class));
        registerBeanDefinitionParser(RpcModelEnum.server.name(), new RpcBeanDefinitionParser(Server.class));
        registerBeanDefinitionParser(RpcModelEnum.service.name(), new RpcBeanDefinitionParser(Service.class));
        registerBeanDefinitionParser(RpcModelEnum.client.name(), new RpcBeanDefinitionParser(Client.class));
        registerBeanDefinitionParser(RpcModelEnum.refrence.name(), new RpcBeanDefinitionParser(Refrence.class));
    }
}
