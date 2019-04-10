package com.niu.tcp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.parsetools.RecordParser;

/**
 * @program: myChat
 * @description:tcp服务
 * @author: niuzilian
 * @create: 2019-03-31 15:01
 **/
public class TcpServer extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        super.start();
        Integer port = config().getInteger("tcp.port", 8082);
        NetServerOptions options = new NetServerOptions();
        options.setPort(port);
        NetServer server = vertx.createNetServer(options);
        System.out.println("=====================");
        server.connectHandler(socket->{
            RecordParser parser = RecordParser.newDelimited("\n\n", null);
            parser.setOutput(buffer->{
                System.out.println("我是服务端：" + buffer);
                socket.write("服务端收到了你的消息，消息内容是：" + buffer.toString());
            });
        });
        server.listen(res -> {
            System.out.println("监听成功了 端口：" + server.actualPort());
        });
    }

}
