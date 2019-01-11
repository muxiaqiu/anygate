package com.intime.soa.util;

/**
 * Created by qmx on 2018/4/18.
 */
public enum OrderByEnum {

    CREATE_TIME("createTime","create_time"),
    ID("id","id"),
    UPDATE_TIME("updateTime","update_time");


    OrderByEnum(String index,String name){
        this.index = index;
        this.name = name;
    }

    private String index;
    private  String name;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName(String index) {
        if(null == index){
            return null;
        }
        for(OrderByEnum _enum : OrderByEnum.values()){
            if (index.equals(_enum.getIndex()))
                return _enum.name;
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }



}
