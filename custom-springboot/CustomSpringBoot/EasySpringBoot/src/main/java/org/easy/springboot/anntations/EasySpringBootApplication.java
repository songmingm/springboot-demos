package org.easy.springboot.anntations;

import org.easy.springboot.server.WebServerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义启动类的注解
 *
 * @author song2m
 * @since 2023/2/20 23:07
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(WebServerAutoConfiguration.class)
@ComponentScan
public @interface EasySpringBootApplication {
}
