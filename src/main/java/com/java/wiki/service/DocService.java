package com.java.wiki.service;

import com.java.wiki.domain.Doc;
import com.baomidou.mybatisplus.extension.service.IService;
import com.java.wiki.req.DocQueryReq;
import com.java.wiki.req.DocSaveReq;
import com.java.wiki.resp.DocQueryResp;

import java.util.List;

/**
* @author Zenos
* @description 针对表【doc(文档)】的数据库操作Service
* @createDate 2024-07-17 08:35:09
*/
public interface DocService extends IService<Doc> {


    List<DocQueryResp> list(DocQueryReq req);

    List<DocQueryResp> listByEbookId(Long ebookId);

    void save(DocSaveReq req);

    String findContent(Long id);
}
