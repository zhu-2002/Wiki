package com.java.wiki.mapper;

import com.java.wiki.domain.Test;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestMapper {
    public List<Test> list();
}
