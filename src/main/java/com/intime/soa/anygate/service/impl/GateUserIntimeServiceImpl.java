package com.intime.soa.anygate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intime.soa.Util.StringUtils;
import com.intime.soa.anygate.mapper.GateUserIntimeMapper;
import com.intime.soa.anygate.mapper.GateUserMapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.id.IdWorker;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.model.anygate.GateUserIntime;
import com.intime.soa.service.anygate.GateUserIntimeService;
import com.intime.soa.service.anygate.GateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@Service("gateUserIntimeService")
public class GateUserIntimeServiceImpl implements GateUserIntimeService {

    @Autowired
    GateUserIntimeMapper gateUserIntimeMapper;

    @Autowired
    GateUserMapper gateUserMapper;

    /**
     * 创建记录
     **/
    public Long insert(GateUserIntime gateUserIntime) {
        return gateUserIntimeMapper.insert(gateUserIntime);
    }

    /**
     * 创建记录
     **/
    public Long insertPartly(GateUserIntime gateUserIntime) {
        return gateUserIntimeMapper.insertPartly(gateUserIntime);
    }

    /**
     * 创建记录
     **/
    public Long insertBatch(List<GateUserIntime> gateUserIntimeList) {
        return gateUserIntimeMapper.insertBatch(gateUserIntimeList);
    }

    /**
     * 创建记录
     **/
    public Long insertPartlyBatch(List<GateUserIntime> gateUserIntimeList) {
        return gateUserIntimeMapper.insertPartlyBatch(gateUserIntimeList);
    }

    /**
     * 更新记录
     **/
    public Long update(GateUserIntime gateUserIntime) {
        return gateUserIntimeMapper.update(gateUserIntime);
    }

    /**
     * 删除记录
     **/
    public Long delete(GateUserIntime gateUserIntime) {
        return gateUserIntimeMapper.delete(gateUserIntime);
    }

    /**
     * 更新记录
     **/
    public Long updateBatch(List<GateUserIntime> gateUserIntimeList) {
        return gateUserIntimeMapper.updateBatch(gateUserIntimeList);
    }

    /**
     * 删除记录
     **/
    public Long deleteById(java.lang.Long id) {
        return gateUserIntimeMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     **/
    public GateUserIntime queryById(java.lang.Long id) {
        return gateUserIntimeMapper.queryById(id);
    }

    /**
     * 根据搜索条件查询
     **/
    public List<GateUserIntime> query(Map<String, Object> paramMap) {
        return gateUserIntimeMapper.query(paramMap);
    }

    /**
     * 根据搜索条件查询记录数
     **/
    public Long queryCount(Map<String, Object> paramMap) {
        return gateUserIntimeMapper.queryCount(paramMap);
    }

    /**
     * 同步gate_user_intime中的数据与gate_user中的数据一致
     * @return
     */
    public Long findMobileInsertUser(){
        List<Map<String,Object>> userList = gateUserIntimeMapper.findMobileInsertUser();
        List<GateUser> list = Lists.newArrayList();
        List<GateUser> users = gateUserMapper.query(Maps.newHashMap());

        Set<String> sets = Sets.newHashSet();

        for(GateUser gateUser:users){
            sets.add(gateUser.getMobile());
        }

        for (Map<String,Object> user:userList){
            int before = sets.size();
            sets.add(StringUtils.safeToString(user.get("mobile")));
            int after = sets.size();
            if(after == before+1){
                GateUser gateUser = new GateUser();
                gateUser.setId(IdWorker.getUUID());
                gateUser.setName(StringUtils.safeToString(user.get("name")));
                gateUser.setMobile(StringUtils.safeToString(user.get("mobile")));
                gateUser.setCreateUserName("邱慕夏");
                gateUser.setCreateTime(new Date());
                list.add(gateUser);
            }
        }

        if(!CheckUtil.isEmpty(list)){
            return gateUserMapper.insertBatchPartly(list);
        }
        return 0L;
    }

}
