package com.example.wiki.controller;

import com.example.wiki.domain.Test;
import com.example.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    @Value("${test.hello:TEST}")
    private String testHello;
    @GetMapping("/hello")
    public String hello(){
        return "hello world"+testHello;
    }

    @Autowired
    private TestService testService;
    @GetMapping("/test/list")
    public List<Test> list(){
        return testService.list();
    }
}
