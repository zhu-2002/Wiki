package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.wiki.domain.Demo;
import com.java.wiki.service.DemoService;
import com.java.wiki.mapper.DemoMapper;
import org.springframework.stereotype.Service;

/**
* @author Zenos
* @description 针对表【demo】的数据库操作Service实现
* @createDate 2024-07-09 21:20:20
*/
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo>
    implements DemoService{

}




