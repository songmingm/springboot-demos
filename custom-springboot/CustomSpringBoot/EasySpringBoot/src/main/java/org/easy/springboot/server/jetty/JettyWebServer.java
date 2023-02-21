package org.easy.springboot.server.jetty;

import org.easy.springboot.server.WebServer;
import org.springframework.web.context.WebApplicationContext;

/**
 * Jetty服务启动
 *
 * @author song2m
 * @since 2023/2/20 23:17
 */
public class JettyWebServer implements WebServer {

    @Override
    public void start(WebApplicationContext applicationContext) {
        System.out.println("Jetty服务器启动...");
    }
}
