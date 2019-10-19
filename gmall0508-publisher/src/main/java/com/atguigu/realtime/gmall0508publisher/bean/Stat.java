package com.atguigu.realtime.gmall0508publisher.bean;

import java.util.List;

public class Stat {
    private List<Option> options;
    private String title;

    public Stat() {
    }

    public Stat(List<Option> options, String title) {
        this.options = options;
        this.title = title;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
