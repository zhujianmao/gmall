package com.atguigu.realtime.gmall0508publisher.bean;

import jdk.nashorn.internal.objects.annotations.Getter;

import java.io.Serializable;

/*
"id":"dau","name":"新增日活","value":1200
 */
public class DauBean implements Serializable {

    private String id;
    private String name;
    private Object value;

    public DauBean() {
    }

    public DauBean(String id, String name, Object value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "DauBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
