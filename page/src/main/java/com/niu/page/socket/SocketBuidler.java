package com.niu.page.socket;

import java.io.IOException;
import java.net.Socket;

public class SocketBuidler {
    private static Socket socket;

    private final static String remote_ip="192.168.100.33";

    private final static int remote_port=8082;

    private SocketBuidler (){}

    public static Socket getInstence(){
        if(socket==null){
            synchronized (SocketBuidler.class){
                if(socket==null){
                    try {
                        socket=new Socket(remote_ip,remote_port);
                    } catch (IOException e) {
                        e.printStackTrace();
                        //链接远程socekt服务失败，直接停止程序运行
                        System.exit(1);
                    }
                }
            }
        }
        return socket;
    }

}
