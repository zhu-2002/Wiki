package com.java.wiki.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.java.wiki.domain.Ebook;
import com.java.wiki.req.EbookQueryReq;
import com.java.wiki.req.EbookSaveReq;
import com.java.wiki.resp.CommonResp;
import com.java.wiki.resp.EbookQueryResp;
import com.java.wiki.resp.PageResp;
import com.java.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Wrapper;
import java.util.List;

@RestController
@RequestMapping("ebook")
public class EbookController extends BaseController{

    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/del/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        ebookService.removeById(id);
        return resp;
    }
}
