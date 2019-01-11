package com.intime.soa.anygate.service.impl;

import com.google.common.collect.Maps;
import com.intime.soa.anygate.mapper.GateResourceMapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.model.anygate.GateResource;
import com.intime.soa.model.anygate.GateResourceTree;
import com.intime.soa.service.anygate.GateResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@Service("gateResourceService")
public class GateResourceServiceImpl implements GateResourceService {


    @Autowired
    GateResourceMapper gateResourceMapper;

    /**
     * 创建记录
     **/
    public Long insert(GateResource gateResource) {
        return gateResourceMapper.insert(gateResource);
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateResource gateResource) {
        return gateResourceMapper.insertPartly(gateResource);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateResource> gateResourceList) {
        return gateResourceMapper.insertBatch(gateResourceList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateResource> gateResourceList) {
        return gateResourceMapper.insertPartlyBatch(gateResourceList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateResource gateResource) {
        return gateResourceMapper.update(gateResource);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateResource gateResource) {
        return gateResourceMapper.delete(gateResource);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateResource> gateResourceList) {
        return gateResourceMapper.updateBatch(gateResourceList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.String id) {
        return gateResourceMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateResource queryById(java.lang.String id) {
        return gateResourceMapper.queryById(id);
    }

    /**
     * 根据搜索条件查询
     **/
    public List<GateResource> query(Map<String, Object> paramMap) {
        return gateResourceMapper.query(paramMap);
    }

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateResourceMapper.queryCount(paramMap);
    }

    /**
     * 加载该项目的资源树
     *
     * @param projectId
     * @return
     */
    public List<GateResourceTree> readAllGateResource(String projectId) {
//        List<GateResourceTree> result = gateResourceMapper.readAllGateResource(projectId);
//        return result;
        GateResource result = gateResourceMapper.queryById("1");
        System.out.println(result);
        return null;
    }

    /**
     * 禁用某资源
     *
     * @param paramMap
     * @return
     */
    public Map<String, Object> forbidden(Map<String, Object> paramMap) {
        //TODO：查询资源下是否存在资源
        //更新
        GateResource gateResource = new GateResource();
        gateResource.setId(ValueUtil.toString(paramMap.get("id")));
        gateResource.setIsDelete("1");
        gateResource.setUpdateTime(new Date());
        gateResource.setUpdateUserId(ValueUtil.toString(paramMap.get("userId")));
        Long temp = gateResourceMapper.update(gateResource);
        Map<String, Object> result = Maps.newHashMap();
        if (temp > 0) {
            result.put("id", gateResource.getId());
        } else {
            result.put("code", "1");
            result.put("message", "该项目下存在资源，不可禁用！");
        }
        return result;
    }

}
