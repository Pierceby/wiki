package com.example.wiki.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class PageReq {
    //每页数量
    @Max(value = 1000,message = "每页条数不能超过一千")
    @NotNull(message = "每页条数不能为空")
    private int size;
    @NotNull(message = "页码不能为空")
    //页码
    private int page;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "PageReq{" +
                "size=" + size +
                ", page=" + page +
                '}';
    }
}
