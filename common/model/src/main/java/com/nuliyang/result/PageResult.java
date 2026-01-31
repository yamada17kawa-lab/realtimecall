package com.nuliyang.result;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private long total;
    private long pages;

    public PageResult(List<T> list, long total, long pages) {
        this.list = list;
        this.total = total;
        this.pages = pages;
    }
}

