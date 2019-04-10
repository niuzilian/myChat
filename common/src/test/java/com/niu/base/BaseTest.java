package com.niu.base;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.*;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-04-04 20:56
 **/
@RunWith(VertxUnitRunner.class)
public class BaseTest {
    private Vertx vertx;
    private EventBus eb;
    @Before
    public void setUp(TestContext context) throws Exception{
        vertx=Vertx.vertx();
        eb=vertx.eventBus();
    }
    @After
    public void tearDown(TestContext context){

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
