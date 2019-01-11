package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateRole;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateRoleMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateRole gateRole);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateRole gateRole);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateRole> gateRoleList);

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateRole> gateRoleList);

    /**
     * 更新记录
     **/
    public Long update(GateRole gateRole);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateRole> gateRoleList);

    /**
     * 删除记录
     **/
    public Long delete(GateRole gateRole);

    /**
     * 删除记录
     **/
    public Long deleteById(String id);

    /**
     * 根据ID查询
     **/
    public GateRole queryById(String id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateRole> query(Map paramMap);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

    /**
     * 根据用户和角色关联ID，查询该角色信息
     * @param relationRoleId
     * @return
     */
    public List<GateRole> queryByRelationRoleId(String relationRoleId);

}
