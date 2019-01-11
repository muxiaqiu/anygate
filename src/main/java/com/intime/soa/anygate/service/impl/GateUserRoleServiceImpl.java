package com.intime.soa.anygate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intime.soa.anygate.mapper.*;
import com.intime.soa.framework.exception.ApplicationException;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.string.StringUtils;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.model.anygate.GateResource;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.model.anygate.GateUserRole;
import com.intime.soa.model.anygate.ResourceRole;
import com.intime.soa.service.anygate.GateUserRoleService;
import com.intime.soa.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@Service("gateUserRoleService")
public class GateUserRoleServiceImpl implements GateUserRoleService {

    @Autowired
    GateUserRoleMapper gateUserRoleMapper;

    @Autowired
    GateUserMapper gateUserMapper;

    @Autowired
    GateProjectMapper gateProjectMapper;

    @Autowired
    GateRoleResourceMapper gateRoleResourceMapper;

    @Autowired
    GateResourceMapper gateResourceMapper;

    /**
     * 创建记录
     **/
    public Long insert(GateUserRole gateUserRole) {
        return gateUserRoleMapper.insert(gateUserRole);
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateUserRole gateUserRole) {
        return gateUserRoleMapper.insertPartly(gateUserRole);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateUserRole> gateUserRoleList) {
        return gateUserRoleMapper.insertBatch(gateUserRoleList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateUserRole> gateUserRoleList) {
        return gateUserRoleMapper.insertPartlyBatch(gateUserRoleList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateUserRole gateUserRole) {
        return gateUserRoleMapper.update(gateUserRole);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateUserRole gateUserRole) {
        return gateUserRoleMapper.delete(gateUserRole);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateUserRole> gateUserRoleList) {
        return gateUserRoleMapper.updateBatch(gateUserRoleList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.String id) {
        return gateUserRoleMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateUserRole queryById(java.lang.String id) {
        return gateUserRoleMapper.queryById(id);
    }

    /**
     * 根据搜索条件查询
     **/
    public List<GateUserRole> query(Map<String, Object> paramMap) {
        return gateUserRoleMapper.query(paramMap);
    }

    /**
     * 根据用户ID查询该用户所有的角色code
     * @param userId
     * @return
     */
    public List<String> getRoleCodeByUserId(String userId){return gateUserRoleMapper.getRoleCodeByUserId(userId);}


    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateUserRoleMapper.queryCount(paramMap);
    }

    /**
     * 根据角色id 查询该角色下用户的前20条信息
     *
     * @param
     * @return
     */
    public List<Map<String,Object>> queryUserByRole(Map<String, Object> paraMap) {
        return gateUserRoleMapper.queryUserByRole(paraMap);
    }

    /**
     * 查询角色下的所有的用户，用户总数
     *
     * @param paraMap
     * @return
     */
    public Long queryUserByRoleCount(Map<String, Object> paraMap) {
        return gateUserRoleMapper.queryUserByRoleCount(paraMap);
    }

    /**
     * 批量删除用户与角色之间的关系
     *
     * @param
     */
    public void deleteBatch(List<GateUserRole> userRoleList) {
        gateUserRoleMapper.deleteBatch(userRoleList);
    }

    public List<Map<String, Object>> resourceListByUserId(String userId){
        return gateUserRoleMapper.getResourceListByUserId(userId);
    }

    /**
     * 根据角色ID查询该角色下的用户IDs
     * @param roleId
     * @return
     */
    public List<String> queryUserIdsByRoleId(String roleId){
        return gateUserRoleMapper.queryUserIdsByRoleId(roleId);
    }

    /**
     * 根据anygate admin 查看该下所有的资源ID
     * @param code
     * @return
     */
    public List<String> getResourceIdsByRoleCode(String code){return gateUserRoleMapper.getResourceIdsByRoleCode(code);}

    /**
     * 查询用户所有的资源列表
     *
     * @param userId
     * @return
     */
    public Map<String, Object> getMenu(String userId) {
        //获取用户的信息，查询该用户是否是某项目的超级管理员
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", userId);
        param.put("isDisabled","0");
        param.put("isDelete","0");
        List<GateUser> gateUsers = gateUserMapper.query(param);
        if (CheckUtil.isEmpty(gateUsers)) {
            throw new ApplicationException("该用户不存在！");
        }

        GateUser gateUser = gateUsers.get(0);
        if (1 == ValueUtil.toInt(gateUser.getIsAdminManager())) {

            //获取所有项目的资源树
            List<Map<String, Object>> treeList = gateUserRoleMapper.getProject();
            //获取所有项目的资源
            List<Map<String, Object>> resourceList = gateUserRoleMapper.getResourceList();

            Map<String, Object> result = Maps.newHashMap();
            result.put("resources", resourceList);
            result.put("projects", treeList);
            Map<String, Object> params = Maps.newHashMap();
            params.put("userName", gateUser.getName());
            params.put("userId", gateUser.getId());
            result.put("profile", params);
            return result;
        }

        //获取该用户所有项目及项目资源树
        List<Map<String, Object>> treeList = gateUserRoleMapper.getProjectByUserId(userId);
        //获取该用户所有项目的资源
        List<Map<String, Object>> resourceList = gateUserRoleMapper.getResourceListByUserId(userId);
        //该用户是否是某项目的管理员
        List<Map<String, Object>> projectList = gateUserRoleMapper.getProjectManagers();
        List<Map<String, Object>> projectOwnList = Lists.newArrayList();
        for (Map<String, Object> project : projectList) {
            //项目管理员角色的ID
            String managers = ValueUtil.toString(project.get("managers"));
            if(!CheckUtil.isEmpty(managers)){
                //根据roleId查询用户
                List<String> userIds = gateUserRoleMapper.queryUserIdsByRoleId(managers);
                for(String user:userIds){
                    if(user.equals(userId)){
                        projectOwnList.add(project);
                    }
                }
            }
        }

        //添加AnyGate中的资源
        if(!CheckUtil.isEmpty(projectOwnList)){
            //获取所有anygate的资源
            List<Map<String,Object>> gate = gateUserRoleMapper.getAnyGateByCode(Constants.AnyGate);
            if(!CheckUtil.isEmpty(gate)){
                Map<String,Object> tree = gate.get(0);
                treeList.add(tree);
            }
            //获取项目管理员的AnyGate中的资源树
            List<Map<String,Object>> anyGateResourceList = gateUserRoleMapper.getResourceListByRoleCode(Constants.AnyGate_Admin);
            resourceList.addAll(anyGateResourceList);
        }

        //项目去重
        treeList.addAll(projectOwnList);
        List<Map<String,Object>> tmpList = Lists.newArrayList();
        Set<String> keysSet = new HashSet<String>();
        for(Map<String, Object> map : treeList){
            String keys = ValueUtil.toString(map.get("id"));
            int beforeSize = keysSet.size();
            keysSet.add(keys);
            int afterSize = keysSet.size();
            if(afterSize == beforeSize + 1){
                tmpList.add(map);
            }
        }
        treeList.clear();
        treeList.addAll(tmpList);

        //如果用户有某项目的权限，将该项目中AuthMode（0 || 1）的资源给用户
        List<Map<String,Object>> resourceAuthModePartly = Lists.newArrayList();
        List<String> projectIds = new ArrayList<>(keysSet);
        if(!CheckUtil.isEmpty(projectIds)){
            resourceAuthModePartly = gateUserRoleMapper.getResourceAuthModePartlyByProjectIds(projectIds);
            resourceList.addAll(resourceAuthModePartly);
        }

        //资源去重
        List<Map<String, Object>> resourceByProjectId = Lists.newArrayList();
        //该用户是projectOwnList项目的管理员
        for (Map<String, Object> map : projectOwnList) {
            String projectId = ValueUtil.toString(map.get("id"));
            //根据项目ID，获取该项目所有资源列表
            List<Map<String, Object>> resourcelist = gateUserRoleMapper.getResourceListByProjectId(projectId);
            resourceByProjectId.addAll(resourcelist);
        }

        //资源去重
        resourceList.addAll(resourceByProjectId);
        List<Map<String,Object>> tmpResourceList = Lists.newArrayList();
        Set<String> keysResourceSet = new HashSet<String>();
        for(Map<String, Object> map : resourceList){
            String keys = ValueUtil.toString(map.get("resourceId"));
            int beforeSize = keysResourceSet.size();
            keysResourceSet.add(keys);
            int afterSize = keysResourceSet.size();
            if(afterSize == beforeSize + 1){
                tmpResourceList.add(map);
            }
        }
        resourceList.clear();
        resourceList.addAll(tmpResourceList);

        Map<String, Object> result = Maps.newHashMap();
        result.put("resources", resourceList);
        result.put("projects", treeList);
        Map<String, Object> params = Maps.newHashMap();
        params.put("userName", gateUser.getName());
        params.put("userId", gateUser.getId());
        result.put("profile", params);
        return result;
    }


    /**
     * 查询用户所有的资源列表
     *
     * @param userId
     * @return
     */
    public List<ResourceRole> getResourceRoleByUserId(String userId, String projectId) {
        //获取用户的信息，查询该用户是否是某项目的超级管理员
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", userId);
        param.put("isDisabled","0");
        param.put("isDelete","0");

        int supperAdmin = 0;
        int admin=0;
        Map<String,List<String>> resourceKeys = Maps.newConcurrentMap();


        List<GateUser> gateUsers = gateUserMapper.query(param);
        if (CheckUtil.isEmpty(gateUsers)) {
            throw new ApplicationException("该用户不存在！");
        }

        GateUser gateUser = gateUsers.get(0);
        if (1 == ValueUtil.toInt(gateUser.getIsAdminManager())) {
            supperAdmin = 1;
        }

        //获取用户是否是该项目的管理员
        List<String> list = gateUserRoleMapper.getUserIsManager(projectId,userId);
        if(list != null && list.size()>0){
            admin = 1;
        }

        if(supperAdmin !=0 || admin !=0){
            List<String> adminList = Lists.newArrayList();
            String adminStr = "Admin";
            String supperAdminStr = "SupperAdmin";
            if (supperAdmin == 1) {
                adminList.add(supperAdminStr);
            }
            if (admin == 1) {
                adminList.add(adminStr);
            }

            Map<String,Object> paramResource = Maps.newConcurrentMap();
            paramResource.put("projectId",projectId);
            List<GateResource> resourcesALL = gateResourceMapper.query(paramResource);
            for(GateResource gateResource:resourcesALL){
                List<String> listAdmin = Lists.newArrayList();
                listAdmin.addAll(adminList);
                resourceKeys.put(gateResource.getId(),listAdmin);
            }
        }

        //获取该用户在该项目中所有的roleId
        List<String> listUser = gateUserRoleMapper.getRolesByUserIdAndProjectIdWithoutAdmin(projectId,userId);
        if(listUser == null){
            return changeResource(resourceKeys);
        }

        List<Map<String,Object>> reourcesRoles = Lists.newArrayList();
        if(listUser.size()>0){
            reourcesRoles = gateRoleResourceMapper.getRoleResourceByRoleId(listUser);
        }
        if(reourcesRoles.size() == 0){
            return changeResource(resourceKeys);
        }

        for(Map<String,Object> map :reourcesRoles){
            String resourceId = StringUtils.safeToString(map.get("resourceId"));
            if(resourceKeys.containsKey(resourceId)){
                List<String> roles = resourceKeys.get(resourceId);
                roles.add(StringUtils.safeToString(map.get("roleName")));
            }else{
                List<String> roles = Lists.newArrayList();
                roles.add(StringUtils.safeToString(map.get("roleName")));
                resourceKeys.put(resourceId,roles);
            }
        }
        return changeResource(resourceKeys);
    }

    private List<ResourceRole> changeResource (Map<String,List<String>> resourceKeys){
        List<ResourceRole> result = Lists.newArrayList();
        if(resourceKeys !=null && resourceKeys.size() >0){
            for(Map.Entry<String, List<String>> entry : resourceKeys.entrySet()){
                String key = entry.getKey();
                List<String> roles = entry.getValue();
                ResourceRole resourceRole = new ResourceRole();
                resourceRole.setResourceId(key);
                resourceRole.setRoles(roles);
                result.add(resourceRole);
            }
        }
        return result;
    }
}
