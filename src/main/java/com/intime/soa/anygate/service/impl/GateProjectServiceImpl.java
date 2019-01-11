package com.intime.soa.anygate.service.impl;

import com.google.common.collect.Maps;
import com.intime.soa.anygate.mapper.GateProjectMapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.model.anygate.GateProject;
import com.intime.soa.service.anygate.GateProjectService;
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
@Service("gateProjectService")
public class GateProjectServiceImpl implements GateProjectService {

    @Autowired
    GateProjectMapper gateProjectMapper;

    /**
     * 创建记录
     **/
    public Long insert(GateProject gateProject) {
        return gateProjectMapper.insert(gateProject);
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateProject gateProject) {
        return gateProjectMapper.insertPartly(gateProject);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateProject> gateProjectList) {
        return gateProjectMapper.insertBatch(gateProjectList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateProject> gateProjectList) {
        return gateProjectMapper.insertPartlyBatch(gateProjectList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateProject gateProject) {
        return gateProjectMapper.update(gateProject);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateProject gateProject) {
        return gateProjectMapper.delete(gateProject);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateProject> gateProjectList) {
        return gateProjectMapper.updateBatch(gateProjectList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.String id) {
        return gateProjectMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateProject queryById(java.lang.String id) {
        return gateProjectMapper.queryById(id);
    }

    /**
     * 根据搜索条件查询
     **/
    public List<GateProject> query(Map<String, Object> paramMap) {
        return gateProjectMapper.query(paramMap);
    }

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateProjectMapper.queryCount(paramMap);
    }

    public Map<String, Object> forbidden(Map<String, Object> paramMap) {
        //更新
        GateProject gateProject = new GateProject();
        gateProject.setId(ValueUtil.toString(paramMap.get("id")));
        gateProject.setIsDisabled("0");
        gateProject.setUpdateTime(new Date());
        gateProject.setUpdateUserId(ValueUtil.toString(paramMap.get("userId")));
        Long temp = gateProjectMapper.update(gateProject);
        Map<String, Object> result = Maps.newHashMap();
        if (temp > 0) {
            result.put("id", gateProject.getId());
        } else {
            //TODO:查看该项目下是否存在其他资源
            result.put("code", "1");
            result.put("message", "该项目下存在资源，不可禁用！");
        }
        return result;
    }

}
