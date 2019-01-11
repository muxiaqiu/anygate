package com.intime.soa.anygate.mapper;

import com.intime.soa.model.anygate.GateUser;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
public interface GateUserMapper {

    /**
     * 创建记录
     **/
    public Long insert(GateUser gateUser);

    /**
     * 创建记录
     **/
    public Long insertPartly(GateUser gateUser);

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateUser> gateUserList);

    /**
     * 同步intime中的数据
     **/
    public Long insertBatchPartly(List<GateUser> gateUserList);


    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateUser> gateUserList);

    /**
     * 更新记录
     **/
    public Long update(GateUser gateUser);

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateUser> gateUserList);

    /**
     * 删除记录
     **/
    public Long delete(GateUser gateUser);

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.String id);

    /**
     * 根据ID查询
     **/
    public GateUser queryById(java.lang.String id);

    /**
     * 根据搜索条件查询
     **/
    public List<GateUser> query(Map paramMap);

    /**
     * 根据搜索条件模糊匹配
     **/
    public List<GateUser> queryUserBy(Map<String, Object> paramMap);

    /**
     * 根据搜索条件模糊匹配
     **/
    public Long queryUserByCount(Map<String, Object> paramMap);

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map paramMap);

    /**
     * 根据用户集合查询这些用户是否都未删除
     * @param userList
     * @return
     */
    public Long queryNotDeleteUserByLists(List<String> userList);

}
