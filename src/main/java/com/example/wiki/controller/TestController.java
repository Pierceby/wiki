package com.example.wiki.controller;

import com.example.wiki.domain.Demo;
import com.example.wiki.domain.Test;
import com.example.wiki.service.DemoService;
import com.example.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    @Autowired
    private DemoService demoService;
    @GetMapping("/demo/list")
    public List<Demo> list(){
        return demoService.list();
    }
}
