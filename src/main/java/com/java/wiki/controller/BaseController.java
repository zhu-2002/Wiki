package com.java.wiki.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
public class BaseController {
    /**
     * /hello
     * @return "hello"
     */
    @GetMapping("/hello")
    public String hello()
    {
        return "hello ! " ;
    }


}
