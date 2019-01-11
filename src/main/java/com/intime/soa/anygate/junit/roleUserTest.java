package com.intime.soa.anygate.junit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intime.soa.anygate.mapper.*;
import com.intime.soa.framework.LogCollectManager;
import com.intime.soa.framework.exception.ApplicationException;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.id.IdWorker;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.model.anygate.*;
import com.intime.soa.util.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by qmx on 2017/12/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-datasource.xml"})
public class roleUserTest {


    @Autowired
    GateUserRoleMapper gateUserRoleMapper;

    @Autowired
    GateUserMapper gateUserMapper;

    @Autowired
    GateProjectMapper gateProjectMapper;

    @Autowired
    GateRoleResourceMapper gateRoleResourceMapper;

    @Autowired
    GateResourceMapper gateReourceMapper;

    @Test
    public void testAdd() throws Exception {

        String userId = "578ecb6f0ccf48f9b75a92c16a0caec3";

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
//            return result;
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
        resourceAuthModePartly = gateUserRoleMapper.getResourceAuthModePartlyByProjectIds(projectIds);
        resourceList.addAll(resourceAuthModePartly);

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
//        return result;
    }
}
