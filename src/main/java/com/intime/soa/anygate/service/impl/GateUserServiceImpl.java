package com.intime.soa.anygate.service.impl;

import com.google.common.collect.Maps;
import com.intime.soa.anygate.mapper.GateUserMapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.service.anygate.GateUserService;
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
@Service("gateUserService")
public class GateUserServiceImpl implements GateUserService {

    @Autowired
    GateUserMapper gateUserMapper;

    /**
     * 创建记录
     **/
    public Long insert(GateUser gateUser) {
        //检查该手机号是否存在
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("mobile", gateUser.getMobile());
        List<GateUser> gateUserList = gateUserMapper.query(paramMap);
        if (CheckUtil.isEmpty(gateUserList)) {
            return gateUserMapper.insert(gateUser);
        }
        return null;
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateUser gateUser) {
        return gateUserMapper.insertPartly(gateUser);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateUser> gateUserList) {
        return gateUserMapper.insertBatch(gateUserList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateUser> gateUserList) {
        return gateUserMapper.insertPartlyBatch(gateUserList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateUser gateUser) {
        return gateUserMapper.update(gateUser);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateUser gateUser) {
        return gateUserMapper.delete(gateUser);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateUser> gateUserList) {
        return gateUserMapper.updateBatch(gateUserList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.String id) {
        return gateUserMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateUser queryById(java.lang.String id) {
        return gateUserMapper.queryById(id);
    }


    /**
     * 根据搜索条件查询
     **/
    public List<GateUser> query(Map<String, Object> paramMap) {
        return gateUserMapper.query(paramMap);
    }

    /**
     * 根据搜索条件模糊匹配
     **/
    public List<GateUser> queryUserBy(Map<String, Object> paramMap) {
        return gateUserMapper.queryUserBy(paramMap);
    }

    /**
     * 根据搜索条件模糊匹配
     **/
    public Long queryUserByCount(Map<String, Object> paramMap) {
        return gateUserMapper.queryUserByCount(paramMap);
    }

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateUserMapper.queryCount(paramMap);
    }


    @Override
    public String forbidden(Map<String, Object> paramMap) {
        //更新
        GateUser gateUser = new GateUser();
        gateUser.setId(ValueUtil.toString(paramMap.get("id")));
        gateUser.setIsDisabled("1");
        gateUser.setUpdateTime(new Date());
        gateUser.setUpdateUserId(ValueUtil.toString(paramMap.get("userId")));
        Long temp = gateUserMapper.update(gateUser);

        if (temp > 0) {
            return gateUser.getId();
        } else {
            return null;
        }
    }

    @Override
    public Long queryNotDeleteUserByLists(List<String> userList) {
        return gateUserMapper.queryNotDeleteUserByLists(userList);
    }
}
