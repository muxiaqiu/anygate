/*
 * Powered By [rapid-framework]
 * Web Site: http://blog.csdn.net/houfeng30920/article/details/52730893
 * Csdn Code: 
 * Since 2015 - 2017
 */
package com.intime.soa.anygate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intime.soa.anygate.constants.SwaggerContants;
import com.intime.soa.framework.exception.ApplicationException;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.id.IdWorker;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.framework.validation.ValidateManager;
import com.intime.soa.framework.validation.validator.NotBlankValidator;
import com.intime.soa.framework.validation.validator.NotEmptyValidator;
import com.intime.soa.model.anygate.GateProject;
import com.intime.soa.model.anygate.GateRole;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.service.anygate.GateProjectService;
import com.intime.soa.service.anygate.GateRoleService;
import com.intime.soa.service.anygate.GateUserRoleService;
import com.intime.soa.service.anygate.GateUserService;
import com.intime.soa.util.AnyGateBaseController;
import com.intime.soa.util.Constants;
import com.intime.soa.util.MapUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/anygate")
public class GateProjectController extends AnyGateBaseController {

    @Autowired
    GateProjectService gateProjectService;

    @Autowired
    GateUserService gateUserService;

    @Autowired
    GateRoleService gateRoleService;

    @Autowired
    GateUserRoleService gateUserRoleService;

//    @NoToken
    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public void getList(@RequestParam String query) {
        Map<String, Object> paramMap = getTokenParamMap(query);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("offset,limit", "offset或limit不能为空，请求参数错误"))
                .checkAndThrow();

        List<GateProject> projectList = gateProjectService.query(paramMap);
        List<Map<String,Object>> list = Lists.newArrayList();
        for(GateProject gateProject : projectList){
            Map<String,Object> map =  gateProject.toMap();
            list.add(map);
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("rows", MapUtil.getPartMap("id,code,name,protocol,domain,remark,createTime,createUserId,createUserName,updateTime,updateUserId,updateUserName,isDisabled",list));
        result.put("total", gateProjectService.queryCount(paramMap));
        new ApiResultWrapper().setObj(JsonHelper.toJson(result)).toResponse();
    }

//    @NoToken
    @RequestMapping(value = "/projectlist", method = RequestMethod.GET)
    public void getresourcelist() {
        Map<String,Object> param = getTokenParamMap();

        //获取该用户可以访问的项目
        Map<String,Object> userMap = (Map<String,Object>)param.get("userMap");
        GateUser gateUser = new GateUser();
        try {
            gateUser = (GateUser) ApiResultWrapper.mapToObject(userMap, GateUser.class);
        } catch (Exception e) {
            throw new ApplicationException("Map转换Bean出错！");
        }

        List<GateProject> projectList = gateProjectService.query(param);
        List<Map<String,Object>> list = Lists.newArrayList();
        if(gateUser.getIsAdminManager() != 1){
            List<GateProject> projectOwnList = Lists.newArrayList();
            //该用户是否是某项目的管理员
            for (GateProject project : projectList) {
                //项目管理员角色的ID
                String managers = project.getManagers();
                if(!CheckUtil.isEmpty(managers)){
                    //根据roleId查询用户
                    List<String> userIds = gateUserRoleService.queryUserIdsByRoleId(managers);

                    for(String user:userIds){
                        if(user.equals(gateUser.getId())){
                            projectOwnList.add(project);
                        }
                    }
                }
            }
            projectList.clear();
            projectList.addAll(projectOwnList);
        }

        for(GateProject gateProject : projectList){
            Map<String,Object> map =  gateProject.toMap();
            list.add(map);
        }
        List<Map<String,Object>> result = MapUtil.getPartMap("id,name",list);
        new ApiResultWrapper().setObj(JsonHelper.toJson(result)).toResponse();
    }

//    @NoToken
    @RequestMapping(value = "/project", method = RequestMethod.DELETE)
    public void remove(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "id不能为空，请求参数错误"))
                .checkAndThrow();

        List<GateProject> gateProjects = gateProjectService.query(paramMap);
        if(!CheckUtil.isEmpty(gateProjects)){
            GateProject gateProject = gateProjects.get(0);
            if(Constants.AnyGate.equals(gateProject.getCode())){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("系统警告：您在启动自毁模式！").toResponse();
                return;
            }
        }

        paramMap.put("isDelete", "1");
        String result = update_project(paramMap);
        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setMessage("操作失败！").toResponse();
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", result);
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

//    @NoToken
    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public void add(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotBlankValidator("code,name,protocol,domain", "code,name,protocol,domain不能为空，请求参数错误"))
                .checkAndThrow();

        GateProject gateProject = new GateProject();
        //判断code是否重复
        Map<String,Object> map = Maps.newHashMap();
        map.put("code",paramMap.get("code"));
        List<GateProject> projects = gateProjectService.query(map);

        if(!is_upperCase(ValueUtil.toString(paramMap.get("code")))){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("code需要为小写字母，请不要加特殊符号！").toResponse();
            return;
        }

        if(!CheckUtil.isEmpty(projects)){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("code重复，请重新提交！").toResponse();
            return;
        }

        //初始化项目管理员角色
        GateRole gateRole = new GateRole();
        gateRole.setId(IdWorker.getUUID());
        gateRole.setCode(ValueUtil.toString(paramMap.get("code")) + Constants.ADMIN);
        gateRole.setName("Admin");
        gateRole.setRemark("<" + ValueUtil.toString(paramMap.get("name")) + ">" + "项目管理员");

        try {
            gateProject = (GateProject) ApiResultWrapper.mapToObject(paramMap, GateProject.class);
        } catch (Exception e) {
            throw new ApplicationException("Map转换Bean出错！");
        }
        gateProject.setManagers(gateRole.getId());
        gateProject.setId(IdWorker.getUUID());
        gateProject.setCreateTime(new Date());
        gateProject.setCreateUserId(ValueUtil.toString(paramMap.get("userId")));
        Map<String,Object> userMap = (Map<String,Object>)paramMap.get("userMap");
        gateProject.setCreateUserName(ValueUtil.toString(userMap.get("name")));

        Long result = gateProjectService.insertPartly(gateProject);
        gateRole.setProjectId(gateProject.getId());
        gateRoleService.insertPartly(gateRole);

        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", gateProject.getId());
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

//    @NoToken
    @RequestMapping(value = "/project", method = RequestMethod.PATCH)
    public void update(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
         new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "id不能为空，请求参数错误!"))
                .checkAndThrow();

        if(!CheckUtil.isEmpty(paramMap.get("code"))){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("code已定，不可修改！").toResponse();
            return;
        }

        if(!ValueUtil.isEmpty(ValueUtil.toString(paramMap.get("isDisabled")))){
            if(!(paramMap.get("isDisabled").equals("0") || paramMap.get("isDisabled").equals("1"))){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("参数传递错误！").toResponse();
                return;
            }

            List<GateProject> gateProjects = gateProjectService.query(paramMap);
            if(!CheckUtil.isEmpty(gateProjects)){
                GateProject gateProject = gateProjects.get(0);
                if(Constants.AnyGate.equals(gateProject.getCode()) && paramMap.get("isDisabled").equals("1")){
                    new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("系统警告：您在启动自毁模式！").toResponse();
                    return;
                }
            }
        }

        String result = update_project(paramMap);
        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", result);
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

//    @NoToken
    @ApiOperation(value = "第一步：获取资源树", notes = "获取资源树", tags = {SwaggerContants.GATE_RESOURCE_ROLE})
    @RequestMapping(value = "/resource/tree", method = RequestMethod.GET)
    public void getresourcetree(@RequestParam String query) {
        Map<String, Object> paramMap = getTokenParamMap(query);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("projectId", "projectId参数不能为空！"))
                .checkAndThrow();

        paramMap.put("id",ValueUtil.toString(paramMap.get("projectId")));
        List<GateProject> projectList = gateProjectService.query(paramMap);
        if(CheckUtil.isEmpty(projectList)){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该项目不存在！").toResponse();
            return;
        }
        GateProject gateProject = projectList.get(0);
        List result = JsonHelper.toList(gateProject.getStorage());
        new ApiResultWrapper().setObj(result).toResponse();
    }

//    @NoToken
    @RequestMapping(value = "/resource/tree", method = RequestMethod.PATCH)
    public void changeresource(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("tree,projectId", "tree,projectId参数不能为空！"))
                .checkAndThrow();

        paramMap.put("storage",JsonHelper.toJson(paramMap.get("tree")));
        paramMap.put("id",ValueUtil.toString(paramMap.get("projectId")));
        String result = update_project(paramMap);
        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", result);
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

    private String update_project(Map<String, Object> param) {
        GateProject gateProject = new GateProject();
        try {
            gateProject = (GateProject) ApiResultWrapper.mapToObject(param, GateProject.class);
        } catch (Exception e) {
            throw new ApplicationException("Map转换Bean出错！");
        }
        gateProject.setUpdateTime(new Date());
        gateProject.setUpdateUserId(ValueUtil.toString(param.get("userId")));
        Map<String,Object> userMap = (Map<String,Object>)param.get("userMap");
        gateProject.setUpdateUserName(ValueUtil.toString(userMap.get("name")));
        Long temp = gateProjectService.update(gateProject);
        if (temp > 0) {
            return gateProject.getId();
        }
        return null;
    }

    private boolean is_upperCase(String str){
        char[] c = str.toCharArray();
        for(int i=0;i<c.length;i++){
            int asiiValue=(int)c[i];
            if(!(asiiValue >=97 && asiiValue<=122)){
                return false;
            }
        }
        return true;
    }
}
