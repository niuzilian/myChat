package com.niu.im.tcp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-03-31 16:40
 **/
public class TcpClient extends AbstractVerticle {


    @Override
    public void start() throws Exception {
        super.start();
        NetClientOptions options = new NetClientOptions();

        options.setConnectTimeout(1000);

         NetClient netClient = vertx.createNetClient();

        netClient.connect(8082, "localhost", socket -> {
            if (socket.succeeded()) {
                System.out.println("tcp 客户端链接成功");
                NetSocket netSocket = socket.result();
                netSocket.handler(buff-> System.out.println(buff));
                netSocket.write("第一次发送消息");
                netClient.close();
            } else {
                System.out.println("链接失败 " + socket.cause());

            }
        });
    }
}
