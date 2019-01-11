package com.intime.soa.anygate.junit;

import com.intime.soa.anygate.mapper.GateProjectMapper;
import com.intime.soa.anygate.mapper.GateResourceMapper;
import com.intime.soa.anygate.mapper.GateRoleResourceMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by qmx on 2017/12/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-datasource.xml"})
public class resourceTest {


    @Autowired
    GateProjectMapper gateProjectMapper;

    @Autowired
    GateResourceMapper gateResourceMapper;

    @Autowired
    GateRoleResourceMapper gateRoleResourceMapper;

    @Test
    public void testAdd() throws Exception {
//        Map<String, Object> param = Maps.newHashMap();
//        param.put("orderBy", "create_time");
//        param.put("limit", 20);
//        param.put("offset", 0);
//
//        Map<String, Object> result = Maps.newHashMap();
//        result.put("rows", JsonHelper.toJson(gateProjectMapper.query(param)));
//        result.put("toal", gateProjectMapper.queryCount(param));
//        System.out.println(result);



//        GateResource gateResource = new GateResource();
//        gateResource.setId("2dbff54ea7bf4dcd910cbb60d27d103d");
//        gateResource.setRemark("");
//        gateResourceMapper.update(gateResource);

//        GateProject gateProject = new GateProject();
//        gateProject.setId(IdWorker.getUUID());
//        gateProject.setCode("BigData");
//        gateProject.setName("用户数据采集系统");
//        gateProject.setProtocol("https");
//        gateProject.setManagers("18730685824,15425456789");
//        gateProject.setDomain("intime-simuyun.com");
//        gateProject.setCreateTime(new Date());
//        gateProject.setCreateUserId("123456");
//        Long result = gateProjectMapper.insert(gateProject);
//        System.out.println(gateProject.getId());
//
//
//        GateProject gateProject1 = new GateProject();
//        gateProject1.setId("11");
//        gateProject1.setCode("BigData");
//        gateProject1.setName("用户数据采集系统");
//        gateProject1.setProtocol("https");
//        gateProject1.setManagers("18730685824,15425456789");
//        gateProject1.setDomain("intime-simuyun.com");
//        gateProject1.setUpdateTime(new Date());
//        gateProject1.setUpdateUserId("123456789");
//        gateProjectMapper.update(gateProject1);
//        System.out.println(gateProject1.getId());

//        Map<String, Object> paramMap = Maps.newHashMap();
//        paramMap.put("projectId","f433b67ffefb4395973ecd20ecdefd86");
//        paramMap.put("id", ValueUtil.toString(paramMap.get("projectId")));
//        List<GateProject> projectList = gateProjectMapper.query(paramMap);
//        System.out.println(projectList);


//        Map<String, Object> param1 = Maps.newHashMap();
//        param1.put("method", "GET");
//        param1.put("path", "/anygate/userinfo");
//
//        List<GateResource> resourceList = gateResourceMapper.query(param1);
//        System.out.println(resourceList);

//        GateRoleResource gateRoleResourceDel = new GateRoleResource();
//        gateRoleResourceDel.setRoleId("2");
//        System.out.println(gateRoleResourceMapper.delete(gateRoleResourceDel));
    }
}
