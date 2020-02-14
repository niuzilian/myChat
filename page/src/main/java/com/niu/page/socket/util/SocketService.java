package com.niu.page.socket.util;

import com.niu.common.constants.CmdEnum;
import com.niu.page.socket.CallBack;
import com.niu.page.socket.SocketBuidler;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketService {


    public void dealMsg(CmdEnum cmdEnum, CallBack callBack) {

        switch (cmdEnum) {
            case SEND:

                break;
            case LOGIN:
                this.dologin();
                break;
            case LOGIN_ACK:

                break;
            case HEARTBEAT:

                break;
            case LOGOUT:

                break;
            default:
                break;
        }


    }




    /**
     * 发起登录
     * @return true 发起登录成功
     *         false 发起登录失败
     */
    public boolean dologin(){
        Socket socket = SocketBuidler.getInstence();
        try {
            PrintWriter fw = new PrintWriter(socket.getOutputStream());
            //cmd=1000  bodyLenth=0
            int cmd=1000;
            int bodyLenth=0;
            ByteBuffer byteBuffer=new ByteBuffer();
            byteBuffer.append(IntUtil.int2ByteArr(cmd)).append(IntUtil.int2ByteArr(bodyLenth));
            fw.println(byteBuffer.toString());
            fw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }







}
