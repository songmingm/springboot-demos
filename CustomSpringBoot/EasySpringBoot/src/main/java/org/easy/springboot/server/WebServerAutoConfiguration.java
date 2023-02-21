package org.easy.springboot.server;

import org.easy.springboot.server.jetty.JettyCondition;
import org.easy.springboot.server.jetty.JettyWebServer;
import org.easy.springboot.server.tomcat.TomcatCondition;
import org.easy.springboot.server.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * webServer自动配置
 *
 * @author song2m
 * @since 2023/2/20 23:28
 */
@Configuration
public class WebServerAutoConfiguration {

    /**
     * 当TomcatCondition返回true注入Bean
     */
    @Bean
    @Conditional(TomcatCondition.class)
    public TomcatWebServer tomcatWebServer() {
        return new TomcatWebServer();
    }

    /**
     * 当JettyCondition返回true注入Bean
     */
    @Bean
    @Conditional(JettyCondition.class)
    public JettyWebServer jettyWebServer() {
        return new JettyWebServer();
    }
}
