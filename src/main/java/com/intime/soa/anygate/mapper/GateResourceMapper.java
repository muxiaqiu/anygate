package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateResource;
import com.intime.soa.model.anygate.GateResourceTree;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateResourceMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateResource gateResource);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateResource gateResource);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateResource> gateResourceList);

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateResource> gateResourceList);

    /**
     * 更新记录
     **/
    public Long update(GateResource gateResource);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateResource> gateResourceList);

    /**
     * 删除记录
     **/
    public Long delete(GateResource gateResource);

    /**
     * 删除记录
     **/
    public Long deleteById(String id);

    /**
     * 根据ID查询
     **/
    public GateResource queryById(String id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateResource> query(Map paramMap);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

    /**
     * 加载该项目的资源树
     *
     * @param projectId
     * @return
     */
    public List<GateResourceTree> readAllResource(String projectId);

    /**
     * 查询所有Resource
     *
     * @return
     */
    public List<GateResource> selectGateResource();

    /**
     * 根据查询某资源的子资源
     *
     * @param id
     * @return
     */
    public List<GateResource> selectSubGateResource(String id);

}
