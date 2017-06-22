package com.example.zk.leader.util.selector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created by yanglikun on 2017/6/21.
 */
public class Client {

    private static Log logger = LogFactory.getLog(Client.class);

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-zk" +
                ".xml");
        CuratorFramework curatorClient = ctx.getBean("curatorClient", CuratorFramework.class);
        String leaderPath = "/leaderPath";
        LeaderSelector leaderSelector = new LeaderSelector(curatorClient, leaderPath, new
                LeaderSelectorListenerAdapter() {
                    @Override
                    public void takeLeadership(CuratorFramework client) throws Exception {
                        logger.debug(currentThread() + "获取leader");
                        for (int i = 0; i < 10; i++) {
                            logger.debug(currentThread() + "业务逻辑处理中......");
                            sleep(1);
                        }
                        logger.debug(currentThread() + "释放leader");
                    }

                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                        logger.debug(currentThread() + "状态改变:" + newState);
                        super.stateChanged(client, newState);
                    }
                });
        leaderSelector.autoRequeue();
        leaderSelector.start();
        for (; ; ) {
            logger.debug(currentThread() + "休眠3秒");
            sleep(3);
        }
    }

    public static void sleep(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String currentThread() {
        return "[线程:" + Thread.currentThread() + "]";
    }
}
