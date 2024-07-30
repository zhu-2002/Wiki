package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.wiki.domain.Ip;
import com.java.wiki.service.IpService;
import com.java.wiki.mapper.IpMapper;
import org.springframework.stereotype.Service;

/**
* @author Zenos
* @description 针对表【ip】的数据库操作Service实现
* @createDate 2024-07-30 16:18:28
*/
@Service
public class IpServiceImpl extends ServiceImpl<IpMapper, Ip>
    implements IpService{

}




