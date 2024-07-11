package com.java.wiki.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java.wiki.domain.Ebook;
import com.java.wiki.resp.CommonResp;
import com.java.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Wrapper;
import java.util.List;

@RestController
@RequestMapping("ebook")
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @GetMapping("/test")
    public CommonResp list(){
        CommonResp<List<Ebook>> resp = new CommonResp<>();
        QueryWrapper<Ebook> wrapper = new QueryWrapper<>() ;
        wrapper.like("name","e");
        List<Ebook> ebookList = ebookService.list(wrapper) ;
        resp.setContent(ebookList);
        return resp;
    }

}
