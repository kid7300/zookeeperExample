/*
 * 文 件 名:  LeaderSelectorExample.java
 * 版    权:  Copyright © 2011-2014 深圳市房多多科技有限公司 All Rights Reserved
 * 编写人:  dengkun
 * 编 写 时 间:  2016年5月14日
 */
package com.dengkun.zookeeper.leader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import com.google.common.collect.Lists;

/**
 * @author dengkun
 * @since 2016年5月14日
 */
public class LeaderSelectorExample {

    private static final int CLIENT_QTY = 10;
    private static final String PATH = "/examples/leader";

    public static void main(String[] args) throws Exception {
        List<CuratorFramework> clients = Lists.newArrayList();
        List<LeaderClient> examples = Lists.newArrayList();
        TestingServer server = new TestingServer();
        try {
            for (int i = 0; i < CLIENT_QTY; ++i) {
                CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(),
                        new ExponentialBackoffRetry(1000, 3));
                clients.add(client);
                LeaderClient example = new LeaderClient(client, PATH, "Client #" + i);
                examples.add(example);
                client.start();
                example.start();
            }

            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } finally {
            System.out.println("Shutting down...");
            for (LeaderClient exampleClient : examples) {
                CloseableUtils.closeQuietly(exampleClient);
            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
            CloseableUtils.closeQuietly(server);
        }
    }
}