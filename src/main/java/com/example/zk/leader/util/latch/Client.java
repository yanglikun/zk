package com.example.zk.leader.util.latch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
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

        LeaderLatch leaderLatch = new LeaderLatch(curatorClient, leaderPath);
        leaderLatch.start();
        for (; ; ) {
            sleep();
            logger.debug(currentThread() + " leader?=" + leaderLatch.hasLeadership());
        }
        //leaderLatch调用leader的close方法可以放弃leader，但是放弃后就不能获取了
    }

    public static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String currentThread() {
        return "[线程:" + Thread.currentThread() + "]";
    }


}
