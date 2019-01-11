package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateUserRoleMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateUserRole gateUserRole);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateUserRole gateUserRole);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateUserRole> gateUserRoleList);

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateUserRole> gateUserRoleList);

    /**
     * 更新记录
     **/
    public Long update(GateUserRole gateUserRole);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateUserRole> gateUserRoleList);

    /**
     * 删除记录
     **/
    public Long delete(GateUserRole gateUserRole);

    /**
     * 删除记录
     **/
    public Long deleteById(String id);

    /**
     * 根据ID查询
     **/
    public GateUserRole queryById(String id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateUserRole> query(Map paramMap);

    /**
     * 根据用户ID查询该用户所有的角色code
     * @param userId
     * @return
     */
    public List<String> getRoleCodeByUserId(String userId);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

    /**
     * 根据角色ID查询该角色下的用户IDs
     * @param roleId
     * @return
     */
    public List<String> queryUserIdsByRoleId(String roleId);

    /**
     * 根据角色id 查询该角色下的用户前20条信息
     *
     * @param
     * @return
     */
    public List<Map<String,Object>> queryUserByRole(Map<String, Object> paraMap);

    /**
     * 根据角色id 查询该角色下用户所有条数
     *
     * @param paraMap
     * @return
     */
    public Long queryUserByRoleCount(Map<String, Object> paraMap);

    /**
     * 批量删除用户与角色之间的关系
     *
     * @param
     */
    public void deleteBatch(List<GateUserRole> userRoleList);

    /**
     * 查询用户所有项目的tree
     *
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getProjectByUserId(String userId);

    /**
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getResourceListByUserId(String userId);

    /**
     * 根据anygate admin 查看该下所有的资源ID
     * @param code
     * @return
     */
    public List<String> getResourceIdsByRoleCode(String code);

    /**
     * 查询所有项目的tree
     *
     * @param
     * @return
     */
    public List<Map<String, Object>> getProject();

    /**
     * 根据code，查询AnyGate项目
     *
     * @param
     * @return
     */
    public List<Map<String, Object>> getAnyGateByCode(String code);

    /**
     * 查询所有的资源list
     *
     * @param
     * @return
     */
    public List<Map<String, Object>> getResourceList();

    /**
     * 根据项目Id查询该项目下所有资源
     *
     * @param projectId
     * @return
     */
    public List<Map<String, Object>> getResourceListByProjectId(String projectId);

    /**
     * 根据role的code为项目管理员，该角色下的资源（AnyGate下的角色管理和资源管理）
     *
     * @param
     * @return
     */
    public List<Map<String, Object>> getResourceListByRoleCode(String code);

    /**
     * 查询所有项目（包含managers）
     * @return
     */
    public List<Map<String, Object>> getProjectManagers();

    /**
     * 如果用户有某项目的权限，将该项目中AuthMode（0 || 1）的资源给用户
     * @param projectIds
     * @return
     */
    public List<Map<String,Object>> getResourceAuthModePartlyByProjectIds(List<String> projectIds);

    /**
     * 获取该使用是否是该项目管理员
     * @param projectId
     * @param userId
     * @return
     */
    public List<String> getUserIsManager(@Param("projectId") String projectId, @Param("userId")String userId);

    /**
     * 获取该用户在该项目中所有role（排除Admin权限）
     * @param projectId
     * @param userId
     * @return
     */
    public List<String> getRolesByUserIdAndProjectIdWithoutAdmin(@Param("projectId") String projectId, @Param("userId")String userId);

}
