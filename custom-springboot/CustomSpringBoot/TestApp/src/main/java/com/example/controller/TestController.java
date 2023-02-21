package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试controller
 *
 * @author song2m
 * @since 2023/2/20 22:42
 */
@RestController
public class TestController {

    @GetMapping("test")
    public String test(){
        return "测试服务success";
    }
}
