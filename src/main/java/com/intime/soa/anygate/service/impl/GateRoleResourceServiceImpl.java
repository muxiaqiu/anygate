package com.intime.soa.anygate.service.impl;

import com.intime.soa.anygate.mapper.GateRoleResourceMapper;
import com.intime.soa.model.anygate.GateRoleResource;
import com.intime.soa.service.anygate.GateResourceService;
import com.intime.soa.service.anygate.GateRoleResourceService;
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
@Service("gateRoleResourceService")
public class GateRoleResourceServiceImpl implements GateRoleResourceService {

    @Autowired
    GateRoleResourceMapper gateRoleResourceMapper;

    @Autowired
    GateResourceService gateResourceService;

    @Autowired
    GateRoleService gateRoleService;

    /**
     * 创建记录
     **/
    public Long insert(GateRoleResource gateRoleResource) {
        return gateRoleResourceMapper.insert(gateRoleResource);
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateRoleResource gateRoleResource) {
        return gateRoleResourceMapper.insertPartly(gateRoleResource);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateRoleResource> gateRoleResourceList) {
        return gateRoleResourceMapper.insertBatch(gateRoleResourceList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateRoleResource> gateRoleResourceList) {
        return gateRoleResourceMapper.insertPartlyBatch(gateRoleResourceList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateRoleResource gateRoleResource) {
        return gateRoleResourceMapper.update(gateRoleResource);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateRoleResource gateRoleResource) {
        return gateRoleResourceMapper.delete(gateRoleResource);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateRoleResource> gateRoleResourceList) {
        return gateRoleResourceMapper.updateBatch(gateRoleResourceList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.String id) {
        return gateRoleResourceMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateRoleResource queryById(java.lang.String id) {
        return gateRoleResourceMapper.queryById(id);
    }

    /**
     * 根据搜索条件查询
     **/
    public List<GateRoleResource> query(Map<String, Object> paramMap) {
        return gateRoleResourceMapper.query(paramMap);
    }

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateRoleResourceMapper.queryCount(paramMap);
    }

    /**
     * 加载该角色下的资源
     */
    public List<String> queryResourceByRole(String roleId) {
        //加载该角色下所有的资源
        return gateRoleResourceMapper.queryResourceByRole(roleId);
    }

    /**
     * 批量删除某角色下的资源
     *
     * @param userResourceList
     */
    public void deleteBatch(List<GateRoleResource> userResourceList) {

    }

//    public void getResourceListByUserId(String userId){
//        List<GateRole> roleList = Lists.newArrayList();
//        Map<String,Object> param = Maps.newHashMap();
//        param.put("userId",userId);
//        roleList = gateRoleService.query(param);
//
//        List<Map<Long,List<Long>>> result = Lists.newArrayList();
//
//        //如果role不能空，加载用户
//        if(CheckUtil.isEmpty(roleList)){
//            //将角色列表按照项目分组
//            for(GateRole gateRole:roleList){
//                Long roleId = gateRole.getId();
//                Long  projectId = gateRole.getProjectId();
//
//                for(Map<Long,List<Long>> map : result){
//                    if(map.containsKey(projectId)){
//                        List<Long> rolesList = map.get(projectId);
//                        rolesList.add(roleId);
//                    }else{
//                        List<Long> rolesList = Lists.newArrayList();
//                        rolesList.add(roleId);
//                        Map<Long,List<Long>> insertList = Maps.newHashMap();
//                        insertList.put(projectId,rolesList);
//                        result.add(insertList);
//                    }
//                }
//            }
//
//        for(Map<Long,List<Long>> map : result){
//            for(Map.Entry<Long, List<Long>> vo : map.entrySet()){
//                Long projectId = vo.getKey();
//                List<Long> roles= vo.getValue();
//                //将该项目的资源加载成为资源树
//                List<GateResourceTree> resourceTree = gateResourceService.readAllGateResource(projectId);
//                //根据角色的资源Id，删除不是该角色的资源
//                List<Long> resourceOwn = gateRoleResourceMapper.queryResourceByRoles(roles);
//                for(GateResourceTree tree : resourceTree){
//                        if(!resourceOwn.contains(tree.getId())){
//                            resourceTree.remove()
//                        }
//                    }
//                }
//            }
//
//        }
//
//        }
//    }
}
