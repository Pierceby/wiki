package com.example.wiki.req;

public class PageReq {
    //每页数量
    private int size;
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
