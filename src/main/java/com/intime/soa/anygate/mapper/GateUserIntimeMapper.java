package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateUserIntime;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateUserIntimeMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateUserIntime gateUserIntime);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateUserIntime gateUserIntime);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateUserIntime> gateUserIntimeList);

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateUserIntime> gateUserIntimeList);

    /**
     * 更新记录
     **/
    public Long update(GateUserIntime gateUserIntime);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateUserIntime> gateUserIntimeList);

    /**
     * 删除记录
     **/
    public Long delete(GateUserIntime gateUserIntime);

    /**
     * 删除记录
     **/
    public Long deleteById(Long id);

    /**
     * 根据ID查询
     **/
    public GateUserIntime queryById(Long id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateUserIntime> query(Map paramMap);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

    /**
     * 同步gate_intime_user中的数据到gate_user
     * @return
     */
    public List<Map<String,Object>> findMobileInsertUser();

}
