package com.java.wiki.service;

import com.java.wiki.domain.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.java.wiki.req.CategoryQueryReq;
import com.java.wiki.req.CategorySaveReq;
import com.java.wiki.resp.CategoryQueryResp;
import com.java.wiki.resp.PageResp;

import java.util.List;

/**
* @author Zenos
* @description 针对表【category(分类)】的数据库操作Service
* @createDate 2024-07-16 10:17:31
*/
public interface CategoryService extends IService<Category> {

    List<CategoryQueryResp> list(CategoryQueryReq req);

    void save(CategorySaveReq req);
}
