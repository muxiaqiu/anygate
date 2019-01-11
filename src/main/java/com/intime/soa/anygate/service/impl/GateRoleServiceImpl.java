package com.intime.soa.anygate.service.impl;

import com.intime.soa.anygate.mapper.GateRoleMapper;
import com.intime.soa.model.anygate.GateRole;
import com.intime.soa.service.anygate.GateRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@Service("gateRoleService")
public class GateRoleServiceImpl implements GateRoleService {

    @Autowired
    GateRoleMapper gateRoleMapper;

    /**
     * 创建记录
     **/
    public Long insert(GateRole gateRole) {
        return gateRoleMapper.insert(gateRole);
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateRole gateRole) {
        return gateRoleMapper.insertPartly(gateRole);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateRole> gateRoleList) {
        return gateRoleMapper.insertBatch(gateRoleList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateRole> gateRoleList) {
        return gateRoleMapper.insertPartlyBatch(gateRoleList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateRole gateRole) {
        return gateRoleMapper.update(gateRole);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateRole gateRole) {
        return gateRoleMapper.delete(gateRole);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateRole> gateRoleList) {
        return gateRoleMapper.updateBatch(gateRoleList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.String id) {
        return gateRoleMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateRole queryById(java.lang.String id) {
        return gateRoleMapper.queryById(id);
    }

    /**
     * 根据搜索条件查询
     **/
    public List<GateRole> query(Map<String, Object> paramMap) {
        return gateRoleMapper.query(paramMap);
    }

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateRoleMapper.queryCount(paramMap);
    }

    /**
     * 根据用户和角色关联ID，查询该角色信息
     * @param relationRoleId
     * @return
     */
    public List<GateRole> queryByRelationRoleId(String relationRoleId){return gateRoleMapper.queryByRelationRoleId(relationRoleId);}

}
