package com.example;

import org.easy.springboot.EasySpringApplication;
import org.easy.springboot.anntations.EasySpringBootApplication;

/**
 * 测试项目启动
 *
 * @author song2m
 * @since 2023/2/20 22:40
 */
@EasySpringBootApplication
public class TestApplication {

    /**
     * 以自定义启动器启动项目
     */
    public static void main(String[] args) {
        EasySpringApplication.run(TestApplication.class);
    }

}
