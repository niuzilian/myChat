package com.niu.tcp;

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

    private EventBus eb;

    @Override
    public void start() throws Exception {
        super.start();

        eb = vertx.eventBus();
        NetClientOptions options = new NetClientOptions();

        options.setConnectTimeout(1000);

         NetClient netClient = vertx.createNetClient();

        netClient.connect(8082, "localhost", socket -> {
            if (socket.succeeded()) {
                System.out.println("链接成功");
                NetSocket netSocket = socket.result();
                netSocket.handler(buffer -> {
                    System.out.println("我是客户端：" + buffer.toString());
                });
                for (int i = 0; i < 10; i++) {
                    netSocket.write("" + i);
                    netSocket.write("\n\n");
                    netSocket.write("");
                }
            } else {
                System.out.println("链接失败 " + socket.cause());
            }
        });
    }
}
