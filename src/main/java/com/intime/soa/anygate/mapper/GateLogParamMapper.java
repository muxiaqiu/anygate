package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateLogParam;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateLogParamMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateLogParam gateLogParam);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateLogParam gateLogParam);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateLogParam> gateLogParamList);

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateLogParam> gateLogParamList);

    /**
     * 更新记录
     **/
    public Long update(GateLogParam gateLogParam);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateLogParam> gateLogParamList);

    /**
     * 删除记录
     **/
    public Long delete(GateLogParam gateLogParam);

    /**
     * 删除记录
     **/
    public Long deleteById(Long id);

    /**
     * 根据ID查询
     **/
    public GateLogParam queryById(Long id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateLogParam> query(Map paramMap);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

}
