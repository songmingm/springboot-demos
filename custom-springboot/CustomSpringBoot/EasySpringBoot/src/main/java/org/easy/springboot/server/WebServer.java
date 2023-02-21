package org.easy.springboot.server;

import org.springframework.web.context.WebApplicationContext;

public interface WebServer {

    void start(WebApplicationContext applicationContext);
}
