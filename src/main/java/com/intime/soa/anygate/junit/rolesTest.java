package com.intime.soa.anygate.junit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intime.soa.anygate.mapper.*;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.id.IdWorker;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.model.anygate.GateLog;
import com.intime.soa.model.anygate.GateProject;
import com.intime.soa.model.anygate.GateRole;
import com.intime.soa.model.anygate.GateUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by qmx on 2017/12/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-datasource.xml"})
public class rolesTest {


    @Autowired
    GateRoleMapper gateRoleMapper;

    @Autowired
    GateLogMapper gateLogMapper;

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

        //初始化用户管理数据
//        List<GateUser> userList = Lists.newArrayList();
//        GateUser gateUser = new GateUser();
//        gateUser.setId(IdWorker.getUUID());
//        gateUser.setName("刘安琪");
//        gateUser.setIsAdminManager(0);
//        gateUser.setMobile("13436933054");
//        gateUser.setDataTime(new Date());
//        userList.add(gateUser);
//        gateUserMapper.insertBatch(userList);

//        List<String> userList = Lists.newArrayList();
//        userList.add("3837883de8064caf94476405ad7c64bf");
//
//        Long role = gateUserMapper.queryNotDeleteUserByLists(userList);
//        System.out.println(role);


//        //初始化项目
//        List<GateProject> projectsList = Lists.newArrayList();
//        GateProject gateProject = new GateProject();
//        gateProject.setId(IdWorker.getUUID());
//        gateProject.setCode("bi");
//        gateProject.setName("大数据采集平台");
//        gateProject.setProtocol("https");
//        gateProject.setDomain("datareport.simuyun.com");
//        gateProject.setDataTime(new Date());

//
//for (int i=0;i<6;i++){
//            System.out.println(IdWorker.getUUID());
//        }

//        Map<String, Object> params = new HashMap();
//        params.put("mobile", "13436933054");
//        params.put("phone", "13436933054");
//        List<GateUser> listUser = gateUserMapper.query(params);
//        System.out.println(listUser);


//        GateUser gateUser = new GateUser();
//        gateUser.setId(IdWorker.getUUID());
//        gateUser.setName("刘安琪");
//        gateUser.setIsAdminManager(0);
//        gateUser.setMobile("13436933054");
//        gateUser.setDataTime(new Date());
//        Long result = gateUserMapper.insertPartly(gateUser);
//        System.out.println(result);

    }
}
