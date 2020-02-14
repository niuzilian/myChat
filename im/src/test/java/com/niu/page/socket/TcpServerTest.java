package com.niu.page.socket;

import com.niu.im.tcp.TcpServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.io.*;

import static io.vertx.core.Vertx.vertx;

/**
 * Unit test for simple App.
 */
public class TcpServerTest extends AbstractVerticle{

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(getConfig());
        vertx.deployVerticle(TcpServer.class.getName(),options);
    }

    public  static JsonObject getConfig(){
        try {
            ClassLoader ctxClsLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = ctxClsLoader.getResourceAsStream("dev/config.json");
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
                    String line ;
            StringBuilder sb = new StringBuilder();
            while ((line=br.readLine())!=null) {
                sb.append(line);
            }
            JsonObject config=new JsonObject(sb.toString());
            return config;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
