package com.java.wiki.req;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PageReq {
    @NotNull(message = "【页码】不能为空")
    private int page;

    @NotNull(message = "【每页条数】不能为空")
    @Max(value = 1000, message = "【每页条数】不能超过1000")
    private int size;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageReq{");
        sb.append("page=").append(page);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
