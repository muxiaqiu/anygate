package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateRoleResource;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateRoleResourceMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateRoleResource gateRoleResource);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateRoleResource gateRoleResource);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateRoleResource> gateRoleResourceList);

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateRoleResource> gateRoleResourceList);

    /**
     * 更新记录
     **/
    public Long update(GateRoleResource gateRoleResource);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateRoleResource> gateRoleResourceList);

    /**
     * 删除记录
     **/
    public Long delete(GateRoleResource gateRoleResource);

    /**
     * 删除记录
     **/
    public Long deleteById(String id);

    /**
     * 根据ID查询
     **/
    public GateRoleResource queryById(String id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateRoleResource> query(Map paramMap);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

    /**
     * 加载该角色下的资源
     */
    public List<String> queryResourceByRole(String roleId);

    /**
     * 加载多个角色下的资源
     *
     * @param rolesId
     * @return
     */
    public List<String> queryResourceByRoles(List<String> rolesId);

    /**
     * 获取角色列表对应的资源列表（可重复资源）
     * @param roles
     * @return
     */
    public List<Map<String,Object>> getRoleResourceByRoleId(List<String> roles);
}
