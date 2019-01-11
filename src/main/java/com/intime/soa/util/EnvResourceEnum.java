package com.intime.soa.util;

/**
 * Created by qmx on 2018/4/18.
 */
public enum EnvResourceEnum {

    TESN("tn","galaxy");

    EnvResourceEnum(String index, String projectCode){
        this.index = index;
        this.projectCode = projectCode;
    }

    private String index;
    private  String projectCode;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }



}
