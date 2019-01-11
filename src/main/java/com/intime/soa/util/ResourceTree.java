package com.intime.soa.util;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qmx on 2017/12/6.
 */
public class ResourceTree implements Serializable {

    private static final long serialVersionUID = 4659460380232015333L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private Integer sort;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private List<ResourceTree> branch;
}
