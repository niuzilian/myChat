package com.niu.im.tcp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-03-31 16:40
 **/
public class TcpClient extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);


    @Override
    public void start() throws Exception {
        super.start();
        NetClientOptions options = new NetClientOptions();

        options.setConnectTimeout(1000);

        NetClient netClient = vertx.createNetClient();

        netClient.connect(8082, "localhost", socket -> {
            if (socket.succeeded()) {
                logger.info("tcp 客户端链接成功");
                NetSocket netSocket = socket.result();
             //   netSocket.handler(buff -> logger.info(buff));
                netSocket.write("第一次发送消息");
                netClient.close();
            } else {
                logger.info("链接失败 " + socket.cause());

            }
        });
    }
}
