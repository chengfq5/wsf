package com.wsf.core.util;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

public class ZookeeperUtil {

    private static Logger logger = Logger.getLogger(ZookeeperUtil.class);

    private static volatile ZookeeperUtil instance;

    private ZkClient zkClient;

    /**
     * 通过zk集群(ip1:port1,ip2:port2...)创建zk客户端
     *
     * @param address
     */
    private ZookeeperUtil(String address) {
        logger.info("连接zookeeper[" + address + "]开始");
        zkClient = new ZkClient(address);
        logger.info("连接zookeeper[" + address + "]成功");
    }

    public static synchronized ZookeeperUtil getInstance(String address) {
        if (instance == null) {
            instance = new ZookeeperUtil(address);
        }
        return instance;
    }

    /**
     * 判断zk中路径是否存在
     *
     * @param path
     * @return
     */
    public boolean exists(String path) {
        return zkClient.exists(path);
    }

    /**
     * 创建永久路径
     *
     * @param path 路径
     */
    public void createPath(String path) {
        if (!exists(path)) {
            String[] paths = path.substring(1).split("/");
            String temp = "";
            for (String dir : paths) {
                temp += "/" + dir;
                if (!exists(temp)) {
                    zkClient.create(temp, null, CreateMode.PERSISTENT);
                }
            }
        }
    }

    /**
     * 删除路径
     *
     * @param path
     */
    public void deletePath(String path) {
        if (exists(path)) {
            zkClient.delete(path);
        }
    }

    /**
     * 保存节点数据，不永久保存，当与zookeeper断开连接自动删除
     *
     * @param path 路径
     * @param data 数据
     */
    public void saveNode(String path, Object data) {
        if (!exists(path)) {
            String[] paths = path.substring(1).split("/");
            String temp = "";
            for (String dir : paths) {
                temp += "/" + dir;
                if (!exists(temp)) {
                    zkClient.create(temp, null, CreateMode.EPHEMERAL);
                }
            }
        }
        zkClient.writeData(path, data);
    }

    /**
     * 返回指定路径的所有子节点
     *
     * @param path
     * @return
     */
    public List<String> getChildNodes(String path) {
        if (!exists(path)) {
            return new ArrayList<>();
        }

        return zkClient.getChildren(path);
    }

    /**
     * 返回指定节点数据
     *
     * @param path
     * @return
     */
    public Object getNode(String path) {
        if (!exists(path)) {
            return null;
        }

        return zkClient.readData(path, new Stat());
    }

    /**
     * 订阅节点变化事件
     *
     * @param path
     * @param zkChildListener
     */
    public void subscribeChildChange(String path, IZkChildListener zkChildListener) {
        zkClient.subscribeChildChanges(path, zkChildListener);
    }
}
