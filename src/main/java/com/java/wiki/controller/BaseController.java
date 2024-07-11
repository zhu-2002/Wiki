package com.java.wiki.controller;

import com.java.wiki.domain.Test;
import com.java.wiki.service.TestService;
import com.java.wiki.service.impl.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BaseController {

    @Value("${author.name}")
    private String name ;

    @Autowired
    private TestService testService;
    /**
     * /hello
     * @return "hello"
     */
    @GetMapping("/hello")
    public String hello()
    {
        return "hello ! "+name;
    }

    @GetMapping("/test")
    public List<Test> list(){
        return testService.list() ;
    }

}
