package com.java.wiki.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocQueryReq extends PageReq {
    private Long id;

    private String name;
    @Override
    public String toString() {
        return "DocQueryReq{} " + super.toString();
    }
}
