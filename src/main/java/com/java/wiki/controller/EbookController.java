package com.java.wiki.controller;

import com.java.wiki.domain.Ebook;
import com.java.wiki.domain.Test;
import com.java.wiki.service.impl.EbookServiceImpl;
import com.java.wiki.service.impl.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ebook")
public class EbookController {

    @Autowired
    private EbookServiceImpl ebookServiceImpl;
    /**
     * /hello
     * @return "hello"
     */
    @GetMapping("/hello")
    public String hello()
    {
        return "hello !";
    }

    @GetMapping("/test")
    public List<Ebook> list(){
        return ebookServiceImpl.list() ;
    }

}
