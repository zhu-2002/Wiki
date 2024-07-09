package com.java.wiki.service.impl;

import com.java.wiki.domain.Test;
import com.java.wiki.mapper.TestMapper;
import com.java.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper ;
    @Override
    public List<Test> list(){
        return testMapper.list();
    }
}
