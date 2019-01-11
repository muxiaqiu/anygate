package com.intime.soa.anygate.service.impl;

import com.intime.soa.anygate.mapper.GateLogMapper;
import com.intime.soa.model.anygate.GateLog;
import com.intime.soa.service.anygate.GateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@Service("gateLogService")
public class GateLogServiceImpl implements GateLogService {

    @Autowired
    GateLogMapper gateLogMapper;

    /**
     * 创建记录
     **/
    public Long insert(GateLog gateLog) {
        return gateLogMapper.insert(gateLog);
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateLog gateLog) {
        return gateLogMapper.insertPartly(gateLog);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateLog> gateLogList) {
        return gateLogMapper.insertBatch(gateLogList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateLog> gateLogList) {
        return gateLogMapper.insertPartlyBatch(gateLogList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateLog gateLog) {
        return gateLogMapper.update(gateLog);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateLog gateLog) {
        return gateLogMapper.delete(gateLog);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateLog> gateLogList) {
        return gateLogMapper.updateBatch(gateLogList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.Long id) {
        return gateLogMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateLog queryById(java.lang.Long id) {
        return gateLogMapper.queryById(id);
    }

    /**
     * 根据搜索条件查询
     **/
    public List<GateLog> query(Map<String, Object> paramMap) {
        return gateLogMapper.query(paramMap);
    }

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateLogMapper.queryCount(paramMap);
    }

}
