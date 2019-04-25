package com.niu.start;

import com.niu.im.status.MessageVerticle;
import com.niu.im.status.SocketSession;
import com.niu.im.tcp.TcpServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

/**
 * @program: myChat
 * @description: 统一部署所有的verticle
 * @author: niuzilian
 * @create: 2019-04-10 13:38
 **/
public class StartVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        vertx.deployVerticle(TcpServer.class.getName(),getAvailableOpts().setConfig(config()));
        vertx.deployVerticle(SocketSession.class.getName(),getSingleOpts().setConfig(config()));
        vertx.deployVerticle(MessageVerticle.class.getName(),getAvailableOpts().setConfig(config()));
    }


    private DeploymentOptions getAvailableOpts(){
        DeploymentOptions options = new DeploymentOptions();
        //获取jvm可用的处理器核心的数量
        int num = Runtime.getRuntime().availableProcessors();
        options.setInstances(num);
        return options;
    }

    private DeploymentOptions getSingleOpts(){
        DeploymentOptions options = new DeploymentOptions();
        options.setInstances(1);
        return options;
    }
}
