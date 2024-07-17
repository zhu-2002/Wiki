package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.wiki.domain.Doc;
import com.java.wiki.service.DocService;
import com.java.wiki.mapper.DocMapper;
import org.springframework.stereotype.Service;

/**
* @author Zenos
* @description 针对表【doc(文档)】的数据库操作Service实现
* @createDate 2024-07-17 08:35:09
*/
@Service
public class DocServiceImpl extends ServiceImpl<DocMapper, Doc>
    implements DocService{

}




