package com.niu.im.tcp;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @ClassName: AppClientMain
 * @Description: TODO
 * @author: niuzilian
 * @date: 2020/2/14  8:14 PM
 */
public class AppClientMain {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(TcpClient.class.getName(),new DeploymentOptions());
    }
}
