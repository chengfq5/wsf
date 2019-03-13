package com.wsf.core.listener;

import com.wsf.core.util.RefrenceUtil;
import org.I0Itec.zkclient.IZkChildListener;
import org.apache.log4j.Logger;

import java.util.List;

public class ServiceChangeListener implements IZkChildListener {
    private static Logger logger = Logger.getLogger(ServiceChangeListener.class);
    /**
     * 接口类名称
     */
    private String interfaceName;

    public ServiceChangeListener(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @Override
    public void handleChildChange(String s, List<String> list) throws Exception {
        logger.info("监听到服务变化，refrenceName=" + interfaceName);
        RefrenceUtil.get(interfaceName).getRefrence();
    }
}
