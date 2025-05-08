package com.springboot.microservicemodules3.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/2")
public class TestController {

    @GetMapping("/hello")
    public String helloTest() {
        return "hello2";
    }
}
