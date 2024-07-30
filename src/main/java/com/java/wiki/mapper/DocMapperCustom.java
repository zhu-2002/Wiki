package com.java.wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.wiki.domain.Doc;

/**
* @author Zenos
* @description 针对表【doc(文档)】的数据库操作Mapper
* @createDate 2024-07-17 08:35:09
* @Entity com.java.wiki.domain.Doc
*/
public interface DocMapperCustom extends BaseMapper<Doc> {
    public void updateEbookInfo();
}




