package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateUserEip;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateUserEipMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateUserEip gateUserEip);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateUserEip gateUserEip);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateUserEip> gateUserEipList);

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateUserEip> gateUserEipList);

    /**
     * 更新记录
     **/
    public Long update(GateUserEip gateUserEip);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateUserEip> gateUserEipList);

    /**
     * 删除记录
     **/
    public Long delete(GateUserEip gateUserEip);

    /**
     * 删除记录
     **/
    public Long deleteById(Long id);

    /**
     * 根据ID查询
     **/
    public GateUserEip queryById(Long id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateUserEip> query(Map paramMap);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

}
