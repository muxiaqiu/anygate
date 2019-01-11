package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateLog;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateLogMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateLog gateLog);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateLog gateLog);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateLog> gateLogList);

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateLog> gateLogList);

    /**
     * 更新记录
     **/
    public Long update(GateLog gateLog);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateLog> gateLogList);

    /**
     * 删除记录
     **/
    public Long delete(GateLog gateLog);

    /**
     * 删除记录
     **/
    public Long deleteById(Long id);

    /**
     * 根据ID查询
     **/
    public GateLog queryById(Long id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateLog> query(Map paramMap);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

}
