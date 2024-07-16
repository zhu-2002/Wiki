package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.wiki.domain.Category;
import com.java.wiki.service.CategoryService;
import com.java.wiki.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author Zenos
* @description 针对表【category(分类)】的数据库操作Service实现
* @createDate 2024-07-16 10:17:31
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




