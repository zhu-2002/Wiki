package com.java.wiki.req;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryQueryReq extends PageReq {
    private Long id;

    private String name;
    @Override
    public String toString() {
        return "CategoryQueryReq{} " + super.toString();
    }
}
