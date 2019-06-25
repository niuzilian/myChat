package com.niu;


import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-04-23 20:11
 **/
public class MyLauncher extends Launcher {
    Logger logger = LoggerFactory.getLogger(MyLauncher.class.getName());


    public static void main(String[] args) {

       new MyLauncher().dispatch(args);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        logger.debug("-------------------start chat server---------------------------------------");
        super.beforeStartingVertx(options);
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        super.beforeDeployingVerticle(deploymentOptions);
        if (deploymentOptions == null) {
            deploymentOptions.setConfig(new JsonObject());
        }
        String config = System.getProperty("config", "");
        JsonObject resourceConf= getConfig(config + File.separator + "config.json");
        deploymentOptions.getConfig().mergeIn(resourceConf);
    }

    private JsonObject getConfig(String path){
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream resourceStream = classLoader.getResourceAsStream(path);
            //转换成文本流
            BufferedReader br=new BufferedReader(new InputStreamReader(resourceStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line=br.readLine())!=null) {
                sb.append(line);
            }
            JsonObject config=new JsonObject(sb.toString());
            logger.info("get config is ="+config);
            return config;
        } catch (IOException e) {
            logger.error("get config resource error"+e);
            return new JsonObject();
        }
    }
}
