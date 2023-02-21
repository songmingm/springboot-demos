package org.easy.springboot;

import org.easy.springboot.server.WebServer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.Map;

/**
 * 自定义SpringBoot的启动器
 *
 * @author song2m
 * @since 2023/2/20 22:34
 */
public class EasySpringApplication {

    public static <T> void run(Class<T> clazz) {
        // 创建一个Spring容器，因为要识别扫描哪些包下注解的Bean，所以解析配置类（即SpringBoot启动类）
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(clazz);
        applicationContext.refresh();
        // 根据Pom文件依赖情况，选择启动合适的服务器
        WebServer webServer = getWebServer(applicationContext);
        webServer.start(applicationContext);
    }

    /**
     * 获取webServer
     */
    private static WebServer getWebServer(WebApplicationContext applicationContext) {
        Map<String, WebServer> webServers = applicationContext.getBeansOfType(WebServer.class);
        // webServer只能获取一个，当无或者大于一个时，启动报错
        if (webServers.isEmpty()){
            throw new NullPointerException();
        }
        if (webServers.size()>1){
            throw new IllegalStateException();
        }
        // 返回唯一的一个WebServer
        return webServers.values().stream().findFirst().get();
    }

}
