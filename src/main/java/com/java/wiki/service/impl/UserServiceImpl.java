package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.wiki.domain.User;
import com.java.wiki.service.UserService;
import com.java.wiki.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Zenos
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-07-17 20:21:30
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




