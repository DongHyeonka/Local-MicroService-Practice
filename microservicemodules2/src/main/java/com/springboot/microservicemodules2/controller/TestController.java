package com.springboot.microservicemodules2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/1")
public class TestController {

    @GetMapping("/hello")
    public String helloTest() {
        return "hello1";
    }
}
