package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.java.wiki.domain.Category;
import com.java.wiki.mapper.CategoryMapper;
import com.java.wiki.req.CategoryQueryReq;
import com.java.wiki.req.CategorySaveReq;
import com.java.wiki.resp.CategoryQueryResp;
import com.java.wiki.resp.PageResp;
import com.java.wiki.service.CategoryService;
import com.java.wiki.util.CopyUtil;
import com.java.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Zenos
* @description 针对表【category(分类)】的数据库操作Service实现
* @createDate 2024-07-16 10:17:31
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Autowired
    private CategoryMapper categoryMapper ;

    @Autowired
    private SnowFlake snowFlake ;

    @Override
    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        PageHelper.startPage(req.getPage(),req.getSize());
        QueryWrapper<Category> wrapper = null ;
        if (!ObjectUtils.isEmpty(req.getName())) {
            wrapper = new QueryWrapper<>();
            wrapper.like("name","%" + req.getName() + "%");
        }
        List<Category> categoryList = categoryMapper.selectList(wrapper);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);

        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp ;
    }

    @Override
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (req.getId() == null){
            //新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else {
            //更新
            categoryMapper.updateById(category);
        }
    }
}




