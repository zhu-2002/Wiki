package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.java.wiki.domain.Ebook;
import com.java.wiki.req.EbookQueryReq;
import com.java.wiki.req.EbookSaveReq;
import com.java.wiki.resp.EbookQueryResp;
import com.java.wiki.resp.PageResp;
import com.java.wiki.service.EbookService;
import com.java.wiki.mapper.EbookMapper;
import com.java.wiki.util.CopyUtil;
import com.java.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.logging.Logger;

/**
* @author Zenos
* @description 针对表【ebook(电子书)】的数据库操作Service实现
* @createDate 2024-07-11 10:02:43
*/
@Service
public class EbookServiceImpl extends ServiceImpl<EbookMapper, Ebook>
    implements EbookService{

    @Autowired
    private EbookMapper ebookMapper ;

    @Autowired
    private SnowFlake snowFlake ;

    @Override
    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
        PageHelper.startPage(req.getPage(),req.getSize());
        QueryWrapper<Ebook> wrapper = new QueryWrapper<>();
        if (!ObjectUtils.isEmpty(req.getName())) {
            wrapper.like("name","%" + req.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(req.getCategoryId2())) {
            wrapper.eq("category2_id",req.getCategoryId2());
        }
        List<Ebook> ebookList = ebookMapper.selectList(wrapper);
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);

        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp ;
    }

    @Transactional
    @Override
    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (req.getId() == null){
            //新增
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        }else {
            //更新
            ebookMapper.updateById(ebook);
        }
    }
}




