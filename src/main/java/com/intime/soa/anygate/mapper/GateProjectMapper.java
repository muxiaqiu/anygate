package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateProject;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateProjectMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateProject gateProject);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateProject gateProject);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateProject> gateProjectList);

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateProject> gateProjectList);

    /**
     * 更新记录
     **/
    public Long update(GateProject gateProject);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateProject> gateProjectList);

    /**
     * 删除记录
     **/
    public Long delete(GateProject gateProject);

    /**
     * 删除记录
     **/
    public Long deleteById(String id);

    /**
     * 根据ID查询
     **/
    public GateProject queryById(String id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateProject> query(Map paramMap);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

}
