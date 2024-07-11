package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.wiki.domain.Ebook;
import com.java.wiki.service.EbookService;
import com.java.wiki.mapper.EbookMapper;
import org.springframework.stereotype.Service;

/**
* @author Zenos
* @description 针对表【ebook(电子书)】的数据库操作Service实现
* @createDate 2024-07-11 10:02:43
*/
@Service
public class EbookServiceImpl extends ServiceImpl<EbookMapper, Ebook>
    implements EbookService{

}




