package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.java.wiki.domain.User;
import com.java.wiki.domain.User;
import com.java.wiki.exception.BusinessException;
import com.java.wiki.exception.BusinessExceptionCode;
import com.java.wiki.mapper.UserMapper;
import com.java.wiki.req.UserQueryReq;
import com.java.wiki.req.UserQueryReq;
import com.java.wiki.req.UserSaveReq;
import com.java.wiki.resp.UserQueryResp;
import com.java.wiki.resp.PageResp;
import com.java.wiki.resp.UserQueryResp;
import com.java.wiki.service.UserService;
import com.java.wiki.util.CopyUtil;
import com.java.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author Zenos
 * @description 针对表【user(分类)】的数据库操作Service实现
 * @createDate 2024-07-16 10:17:31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{

    @Autowired
    private UserMapper userMapper ;

    @Autowired
    private SnowFlake snowFlake ;

    @Override
    public PageResp<UserQueryResp> list(UserQueryReq req) {
        PageHelper.startPage(req.getPage(),req.getSize());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (!org.springframework.util.ObjectUtils.isEmpty(req.getLoginName())) {
            wrapper.like("login_name","%" + req.getLoginName() + "%");
        }
        List<User> userList = userMapper.selectList(wrapper);
        PageInfo<User> pageInfo = new PageInfo<>(userList);

        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp ;
    }

    @Override
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("login_name",req.getLoginName());
        if (req.getId() == null){
            if ( userMapper.selectOne(wrapper) != null ){
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
            else {
                //新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }
        }else {
            //更新
            user.setLoginName(null);
            userMapper.updateById(user);
        }
    }
}




