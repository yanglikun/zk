package com.example.zk.leader.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class CuratorFrameworkFactoryBean implements FactoryBean, InitializingBean, DisposableBean {
    private CuratorFramework cFramework;
    private String connectString;
    private String namespace;
    private boolean isSingleton = false;

    public void destroy() throws Exception {
        cFramework.close();
    }

    public void afterPropertiesSet() throws Exception {
        if(StringUtils.isEmpty(namespace)|| StringUtils.startsWith(namespace,"/")){
            throw new IllegalArgumentException("非法空间名，不以/开始");
        }
        cFramework = CuratorFrameworkFactory
                .builder()
                .namespace(namespace)
                .retryPolicy(new ExponentialBackoffRetry(1000,10) )
                .connectionTimeoutMs(5000)
                .ensembleProvider(new FixedEnsembleProvider(connectString))
                .build();
        cFramework.start();
    }

    public Object getObject() throws Exception {
        return cFramework;
    }

    public Class getObjectType() {
        return CuratorFramework.class;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}