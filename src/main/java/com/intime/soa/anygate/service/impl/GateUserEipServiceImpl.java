package com.intime.soa.anygate.service.impl;

import com.intime.soa.anygate.mapper.GateUserEipMapper;
import com.intime.soa.model.anygate.GateUserEip;
import com.intime.soa.service.anygate.GateUserEipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@Service("gateUserEipService")
public class GateUserEipServiceImpl implements GateUserEipService {

    @Autowired
    GateUserEipMapper gateUserEipMapper;

    /**
     * 创建记录
     **/
    public Long insert(GateUserEip gateUserEip) {
        return gateUserEipMapper.insert(gateUserEip);
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateUserEip gateUserEip) {
        return gateUserEipMapper.insertPartly(gateUserEip);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateUserEip> gateUserEipList) {
        return gateUserEipMapper.insertBatch(gateUserEipList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateUserEip> gateUserEipList) {
        return gateUserEipMapper.insertPartlyBatch(gateUserEipList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateUserEip gateUserEip) {
        return gateUserEipMapper.update(gateUserEip);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateUserEip gateUserEip) {
        return gateUserEipMapper.delete(gateUserEip);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateUserEip> gateUserEipList) {
        return gateUserEipMapper.updateBatch(gateUserEipList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.Long id) {
        return gateUserEipMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateUserEip queryById(java.lang.Long id) {
        return gateUserEipMapper.queryById(id);
    }

    /**
     * 根据搜索条件查询
     **/
    public List<GateUserEip> query(Map<String, Object> paramMap) {
        return gateUserEipMapper.query(paramMap);
    }

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateUserEipMapper.queryCount(paramMap);
    }

}
