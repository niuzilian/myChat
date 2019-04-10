package com.niu;

import com.niu.tcp.TcpClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.junit.Test;

import java.util.Scanner;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-03-31 17:10
 **/
public class TcpClientTest  extends BaseTest{
    private static EventBus eb;

    @Test
    public void test() {
        Vertx vertx = Vertx.vertx();
        Future future = Future.future();
        vertx.deployVerticle(TcpClientTest.class.getName(),future.completer());
        future.setHandler(handle->{
            vertx.deployVerticle(TcpClient.class.getName(),res->{
                if(res.succeeded()){
                    System.out.println("123");
                }
            });
        });
    }
}
