package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.wiki.domain.Content;
import com.java.wiki.domain.Doc;
import com.java.wiki.domain.Ip;
import com.java.wiki.exception.BusinessException;
import com.java.wiki.exception.BusinessExceptionCode;
import com.java.wiki.mapper.ContentMapper;
import com.java.wiki.mapper.DocMapper;
import com.java.wiki.mapper.DocMapperCustom;
import com.java.wiki.mapper.IpMapper;
import com.java.wiki.req.DocQueryReq;
import com.java.wiki.req.DocSaveReq;
import com.java.wiki.resp.DocQueryResp;
import com.java.wiki.service.DocService;
import com.java.wiki.util.CopyUtil;
import com.java.wiki.util.RequestContext;
import com.java.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author Zenos
* @description 针对表【doc(文档)】的数据库操作Service实现
* @createDate 2024-07-17 08:35:09
*/
@Service
public class DocServiceImpl extends ServiceImpl<DocMapper, Doc>
    implements DocService{
    @Autowired
    private DocMapper docMapper ;

    @Autowired
    private SnowFlake snowFlake ;

    @Autowired
    private ContentMapper contentMapper ;

    @Autowired
    private IpMapper ipMapper ;

    @Autowired
    private DocMapperCustom myDocMapper ;

    @Override
    public List<DocQueryResp> list(DocQueryReq req) {
        QueryWrapper<Doc> wrapper = new QueryWrapper<>() ;
        wrapper.orderByAsc("sort");
        if (!ObjectUtils.isEmpty(req.getEbookId())) {
            wrapper.eq("ebook_id",req.getEbookId());
        }
        if (!ObjectUtils.isEmpty(req.getName())) {
            wrapper.like("name","%" + req.getName() + "%");
        }
        List<Doc> docList = docMapper.selectList(wrapper);
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list ;
    }

    @Override
    public List<DocQueryResp> listByEbookId(Long ebookId) {
        QueryWrapper<Doc> wrapper = new QueryWrapper<>() ;
        wrapper.orderByAsc("sort");
        if (!ObjectUtils.isEmpty(ebookId)) {
            wrapper.eq("ebook_id",ebookId);
        }
        List<Doc> docList = docMapper.selectList(wrapper);
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list ;
    }

    @Transactional
    @Override
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (req.getId() == null){
            //新增

            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
            //新增
            content.setId(doc.getId());
            contentMapper.insert(content);
        } else if ( contentMapper.selectById(doc.getId()) == null ) {
            //更新
            docMapper.updateById(doc);
            //新增
            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            //更新
            docMapper.updateById(doc);
            //更新
            contentMapper.updateById(content);
        }
    }

    @Override
    public String findContent(Long id) {
        Content content = contentMapper.selectById(id);
        Doc doc = docMapper.selectById(id);
        if ( content == null ){
            return "";
        }
        else {
            doc.setViewCount(doc.getViewCount() + 1);
            docMapper.updateById(doc);
        }
        return content.getContent();
    }

    @Override
    public void vote(Long id) {
        Doc doc = docMapper.selectById(id);
        String ipStr = "DOC_VOTE_" + id + "_" + RequestContext.getRemoteAddr();
        if ( ipMapper.selectById(ipStr) == null ){
            doc.setVoteCount(doc.getVoteCount() + 1);
            docMapper.updateById(doc);

            Ip ip = new Ip();
            ip.setId(ipStr);
            ipMapper.insert(ip);
        }
        else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
    }

    @Override
    public void updateEbookInfo() {
        myDocMapper.updateEbookInfo();
    }
}




