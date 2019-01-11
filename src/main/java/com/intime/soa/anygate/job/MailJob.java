package com.intime.soa.anygate.job;

import com.intime.soa.anygate.mapper.GateUserIntimeMapper;
import com.intime.soa.framework.job.IBaseJob;
import com.intime.soa.framework.job.JobHander;
import com.intime.soa.model.anygate.GateResourceTree;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.service.anygate.GateProjectService;
import com.intime.soa.service.anygate.GateResourceService;
import com.intime.soa.service.anygate.GateUserIntimeService;
import com.intime.soa.service.anygate.GateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Quartz定时任务. <br>
 * 使用配置文件方式.
 */

@JobHander("mailJob")
public class MailJob implements IBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(MailJob.class);

    String content = "这是一封测试邮件，请忽略它的存在！";


    @Autowired
    GateUserService gateUserService;

    @Autowired
    GateProjectService gateProjectService;

    @Autowired
    GateResourceService gateResourceService;

    @Autowired
    GateUserIntimeService gateUserIntimeService;

//    @Scheduled(fixedRate = 60 * 10000 * 1000)
    public void task() {

        Long result = gateUserIntimeService.findMobileInsertUser();
        System.out.println(result);

//        String userId = "811723dc8f8b48db9cc1be9c4fb534d7";
//        GateUser gateUser = gateUserService.queryById(userId);
//        System.out.println(gateUser);

//        //查询前20个
//        Map<String,Object> paramMap = Maps.newHashMap();
//        //{   orderBy: "createTime",   desc: "1",   offset: "0",   limit: "20",   like:"", }
//        paramMap.put("orderBy","create_time");
//        paramMap.put("desc","1");
//        paramMap.put("offset",0);
//        paramMap.put("limit",20);
//        paramMap.put("desc",'1');
//        List<GateProject> list = gateProjectService.query(paramMap);
//        System.out.println(list.toString());
//
//        //参数限制
//        Map<String,Object> paramMap1 = Maps.newHashMap();
//        //{   orderBy: "createTime",   desc: "1",   offset: "0",   limit: "20",   like:"", }
//        paramMap.put("orderBy","create_time");
//        paramMap.put("desc","1");
//        paramMap1.put("offset",0);
//        paramMap1.put("limit",20);
//        paramMap.put("desc",'0');
//        List<GateProject> list1 = gateProjectService.query(paramMap1);
//        System.out.println(list1.toString());
//
//        //参数限制
//        Map<String,Object> paramMap4 = Maps.newHashMap();
//        //{   orderBy: "createTime",   desc: "1",   offset: "0",   limit: "20",   like:"", }
//        paramMap4.put("offset",0);
//        paramMap4.put("limit",20);
//        List<GateProject> list4 = gateProjectService.query(paramMap4);
//        System.out.println(list4.toString());

        //系统添加项目
//        Map<String,Object> paramMap2 = Maps.newHashMap();
//        paramMap2.put("code","天网1");
//        paramMap2.put("name","天网系统1");
//        paramMap2.put("protocol","https");
//        paramMap2.put("domain","intime.simuyun.com");
//               //限制多少个手机号
//        paramMap2.put("managers","18730685824,,18545612345");
//        paramMap2.put("remark","天网系统天网系统天网系统天网系统天网系统天网系统");
//        GateProject gateProject = new GateProject();
//        try {
//            gateProject = (GateProject) ApiResultWrapper.mapToObject(paramMap2,GateProject.class);
//        } catch (Exception e) {
//            throw new ApplicationException("Map转换Bean出错！");
//        }
//        gateProject.setCreateTime(new Date());
//        gateProject.setCreateUserId(ValueUtil.toString(paramMap2.get("userId")));
//        Long result = gateProjectService.insertPartly(gateProject);
//        Long id = gateProject.getId();
//        System.out.println(id);

//        //系统添加项目
//        Map<String,Object> paramMap3 = Maps.newHashMap();
////        paramMap3.put("id",2L);
//        paramMap3.put("code","天网1");
//        paramMap3.put("name","天网系统1");
//        paramMap3.put("protocol","https");
//        paramMap3.put("domain","intime.simuyun.com");
//        paramMap3.put("userId","123456789");
//        //限制多少个手机号
//        paramMap3.put("managers","18730685824,18545612345");
//        paramMap3.put("remark","天网系统天网系统天网系统天网系统天网系统天网系统");
//        GateProject gateProject1 = new GateProject();
//        try {
//            gateProject1 = (GateProject) ApiResultWrapper.mapToObject(paramMap3,GateProject.class);
//        } catch (Exception e) {
//            throw new ApplicationException("Map转换Bean出错！");
//        }
//        gateProject1.setUpdateTime(new Date());
//        gateProject1.setUpdateUserId(ValueUtil.toString(paramMap3.get("userId")));
//        Long temp = gateProjectService.update(gateProject1);
//        System.out.println(temp);

        //某项目下的资源树
//        String projectId = "1";
//        List<GateResourceTree> result = gateResourceService.readAllGateResource(projectId);
//        System.out.println(result);

    }
}
