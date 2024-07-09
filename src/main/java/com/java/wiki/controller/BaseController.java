package com.java.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @Value("${author.name}")
    private String name ;
    /**
     * /hello
     * @return "hello"
     */
    @GetMapping("/hello")
    public String hello()
    {
        return "hello ! "+name;
    }
}
