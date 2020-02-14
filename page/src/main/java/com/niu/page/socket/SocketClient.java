package com.niu.page.socket;

import java.io.*;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class SocketClient
{
    public static void main( String[] args ) {
        try {
            Socket socket = new Socket("192.168.100.33",8082);

            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));




            printWriter.println("msg");

            printWriter.flush();

           StringBuilder content_sb=new StringBuilder();

            String len="";
            while ((len=bufferedReader.readLine())!=null){
                content_sb.append(len);
            }

            System.out.println(content_sb.toString());





        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
