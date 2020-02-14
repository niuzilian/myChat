package com.niu.page.socket;

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
    public Vertx vertx;
    public EventBus eb;
    @Before
    public void setUp(TestContext context) throws Exception{
        vertx=Vertx.vertx();
        eb=vertx.eventBus();
    }
    @After
    public void tearDown(TestContext context){

    }

}
