package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.wiki.domain.Content;
import com.java.wiki.service.ContentService;
import com.java.wiki.mapper.ContentMapper;
import org.springframework.stereotype.Service;

/**
* @author Zenos
* @description 针对表【content(文档内容)】的数据库操作Service实现
* @createDate 2024-07-17 12:24:10
*/
@Service
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content>
    implements ContentService{

}




