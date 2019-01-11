package com.intime.soa.anygate.service.impl;

import com.intime.soa.anygate.mapper.GateLogParamMapper;
import com.intime.soa.model.anygate.GateLogParam;
import com.intime.soa.service.anygate.GateLogParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@Service("gateLogParamService")
public class GateLogParamServiceImpl implements GateLogParamService {

    @Autowired
    GateLogParamMapper gateLogParamMapper;

    /**
     * 创建记录
     **/
    public Long insert(GateLogParam gateLogParam) {
        return gateLogParamMapper.insert(gateLogParam);
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateLogParam gateLogParam) {
        return gateLogParamMapper.insertPartly(gateLogParam);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateLogParam> gateLogParamList) {
        return gateLogParamMapper.insertBatch(gateLogParamList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateLogParam> gateLogParamList) {
        return gateLogParamMapper.insertPartlyBatch(gateLogParamList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateLogParam gateLogParam) {
        return gateLogParamMapper.update(gateLogParam);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateLogParam gateLogParam) {
        return gateLogParamMapper.delete(gateLogParam);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateLogParam> gateLogParamList) {
        return gateLogParamMapper.updateBatch(gateLogParamList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.Long id) {
        return gateLogParamMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateLogParam queryById(java.lang.Long id) {
        return gateLogParamMapper.queryById(id);
    }

    /**
     * 根据搜索条件查询
     **/
    public List<GateLogParam> query(Map<String, Object> paramMap) {
        return gateLogParamMapper.query(paramMap);
    }

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateLogParamMapper.queryCount(paramMap);
    }

}
