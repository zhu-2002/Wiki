package com.java.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.wiki.domain.Doc;
import com.java.wiki.mapper.DocMapper;
import com.java.wiki.req.DocQueryReq;
import com.java.wiki.req.DocSaveReq;
import com.java.wiki.resp.DocQueryResp;
import com.java.wiki.service.DocService;
import com.java.wiki.util.CopyUtil;
import com.java.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<DocQueryResp> list(DocQueryReq req) {
        QueryWrapper<Doc> wrapper = new QueryWrapper<>() ;
        wrapper.orderByAsc("sort");
        if (!ObjectUtils.isEmpty(req.getName())) {
            wrapper.like("name","%" + req.getName() + "%");
        }
        List<Doc> docList = docMapper.selectList(wrapper);
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list ;
    }

    @Override
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (req.getId() == null){
            //新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        }else {
            //更新
            docMapper.updateById(doc);
        }
    }
}




