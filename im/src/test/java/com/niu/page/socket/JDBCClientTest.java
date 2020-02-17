package com.niu.page.socket;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;

/**
 * @ClassName: JDBCClientTest
 * @Description: TODO
 * @author: niuzilian
 * @date: 2020/2/17  9:13 PM
 */
public class JDBCClientTest extends AbstractVerticle {

    private static JDBCClient jdbcClient;

    @Override
    public void start() throws Exception {
        super.start();

        JsonObject config = new JsonObject();
        config.put("url", "jdbc:mysql://172.22.0.24:3306/gj_asset");
        config.put("user", "root");
        config.put("password", "Sqzl1234");
        config.put("driver_class", "com.mysql.cj.jdbc.Driver");
        jdbcClient = JDBCClient.createShared(vertx, config, "gj");

    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(JDBCClientTest.class.getName(), res -> {
            if (res.succeeded()) {
                jdbcClient.getConnection(conRes -> {
                    if (conRes.succeeded()) {
                        conRes.result().query("select * from ichat_user", que -> {
                            List<JsonObject> rows = que.result().getRows();
                            System.out.println(rows.toString());
                        });
                    }
                });
            } else {

            }


        });
    }
}
