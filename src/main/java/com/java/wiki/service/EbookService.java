package com.java.wiki.service;

import com.java.wiki.domain.Ebook;
import com.baomidou.mybatisplus.extension.service.IService;
import com.java.wiki.req.EbookQueryReq;
import com.java.wiki.resp.EbookQueryResp;
import com.java.wiki.resp.PageResp;

import java.util.List;

/**
* @author Zenos
* @description 针对表【ebook(电子书)】的数据库操作Service
* @createDate 2024-07-11 10:02:43
*/
public interface EbookService extends IService<Ebook> {

    PageResp<EbookQueryResp> list(EbookQueryReq req);
}
