package com.java.wiki.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DocSaveReq {
    private Long id;

    @NotNull(message = "【电子书】不能为空")
    private Long ebookId;

    @NotNull(message = "【父文档】不能为空")
    private Long parent;

    @NotNull(message = "【名称】不能为空")
    private String name;

    @NotNull(message = "【顺序】不能为空")
    private Integer sort;

    @NotNull(message = "【内容】不能为空")
    private String content;

    private Integer viewCount;

    private Integer voteCount;

}
