package com.niu.im;

import com.niu.im.tcp.TcpClient;
import io.vertx.core.Vertx;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-03-31 17:10
 **/
public class TcpClientTest {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(TcpClient.class.getName(), clientRes -> {
            System.out.println("client 测试类部署完成");
        });
    }
}
