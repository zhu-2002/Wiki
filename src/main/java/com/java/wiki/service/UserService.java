package com.java.wiki.service;

import com.java.wiki.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.java.wiki.req.UserLoginReq;
import com.java.wiki.req.UserQueryReq;
import com.java.wiki.req.UserResetPasswordReq;
import com.java.wiki.req.UserSaveReq;
import com.java.wiki.resp.PageResp;
import com.java.wiki.resp.UserLoginResp;
import com.java.wiki.resp.UserQueryResp;

import java.util.List;

/**
* @author Zenos
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-07-17 20:21:30
*/
public interface UserService extends IService<User> {

    PageResp<UserQueryResp> list(UserQueryReq req);

    void save(UserSaveReq req);

    void resetPassword(UserResetPasswordReq req);

    UserLoginResp login(UserLoginReq req);
}
