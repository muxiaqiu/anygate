/*
 * Powered By [rapid-framework]
 * Web Site: http://blog.csdn.net/houfeng30920/article/details/52730893
 * Csdn Code: 
 * Since 2015 - 2017
 */
package com.intime.soa.anygate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intime.soa.Util.StringUtils;
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
import com.intime.soa.model.anygate.GateResource;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.model.anygate.ResourceRole;
import com.intime.soa.service.anygate.GateResourceService;
import com.intime.soa.service.anygate.GateUserRoleService;
import com.intime.soa.service.anygate.GateUserService;
import com.intime.soa.util.AnyGateBaseController;
import com.intime.soa.util.MapUtil;
import io.swagger.annotations.Api;
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
@Api(tags = {SwaggerContants.GATE_RESOURCE_ROLE})
@RestController
@RequestMapping(value = "/anygate")
public class GateResourceController extends AnyGateBaseController {

    @Autowired
    GateResourceService gateResourceService;

    @Autowired
    GateUserRoleService gateUserRoleService;

    @Autowired
    GateUserService gateUserService;

//    @NoToken
    @ApiOperation(value = "第二步：获取资源信息", notes = "根据项目Id获取项目下资源信息", tags = {SwaggerContants.GATE_RESOURCE_ROLE})
    @RequestMapping(value = "/resources", method = RequestMethod.GET)
    public void getList(@RequestParam String query) {
        Map<String, Object> paramMap = getTokenParamMap(query);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("projectId", "projectId不能为空，请求参数错误"))
                .checkAndThrow();

        List<GateResource> projectList = gateResourceService.query(paramMap);
        List<Map<String,Object>> list = Lists.newArrayList();
        for(GateResource gateResrouce : projectList){
            Map<String,Object> map =  gateResrouce.toMap();
            list.add(map);
        }

        List<Map<String,Object>> result = MapUtil.getPartMap("id,projectId,type,name,icon,method,path,remark,authMode,createTime,createUserId,createUserName,updateTime,updateUserId,updateUserName,isDisabled",list);
        new ApiResultWrapper().setObj(JsonHelper.toJson(result)).toResponse();
    }

//    @NoToken
    @RequestMapping(value = "/resource", method = RequestMethod.DELETE)
    public void remove(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "id不能为空，请求参数错误"))
                .checkAndThrow();

        paramMap.put("isDelete", "1");
        String result = update_resource(paramMap);
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
    @RequestMapping(value = "/resource", method = RequestMethod.POST)
    public void add(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        logger.info(JsonHelper.toJson(paramMap));
        new ValidateManager(paramMap)
                .add(new NotBlankValidator("type", "type不能为空，请求参数错误"))
                .checkAndThrow();

        if ("module".equals(ValueUtil.toString(paramMap.get("type")))) {
             new ValidateManager(paramMap)
                    .add(new NotBlankValidator("name,projectId,authMode", "name,projectId,authMode不能为空，请求参数错误"))
                    .checkAndThrow();
        } else if ("page".equals(ValueUtil.toString(paramMap.get("type")))) {
            new ValidateManager(paramMap)
                    .add(new NotBlankValidator("name,path,projectId,authMode", "name,path,projectId,authMode不能为空，请求参数错误"))
                    .checkAndThrow();
        } else if("action".equals(ValueUtil.toString(paramMap.get("type")))){
            new ValidateManager(paramMap)
                    .add(new NotBlankValidator("name,path,method,projectId,authMode", "name,path,method,projectId,authMode不能为空，请求参数错误"))
                    .checkAndThrow();

            //如果该资源的method和path已存在，不可以重复添加
            Map<String,Object> map = Maps.newHashMap();
            map.put("method",paramMap.get("method"));
            map.put("path",paramMap.get("path"));
            List<GateResource> resources = gateResourceService.query(map);
            logger.info(JsonHelper.toJson(resources));
            if(!CheckUtil.isEmpty(resources)){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该资源已存在，请重新提交！").toResponse();
                return;
            }
        }else{
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("type不存在，请重新提交").toResponse();
            return;
        }

        if(!(paramMap.get("authMode").equals("0") || paramMap.get("authMode").equals("1")|| paramMap.get("authMode").equals("2"))){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("authMode类型不正确，请重新提交！").toResponse();
            return;
        }

        //添加
        GateResource gateResource = new GateResource();
        try {
            gateResource = (GateResource) ApiResultWrapper.mapToObject(paramMap, GateResource.class);
        } catch (Exception e) {
            throw new ApplicationException("Map转换Bean出错！");
        }
        gateResource.setId(IdWorker.getUUID());
        gateResource.setCreateTime(new Date());
        gateResource.setCreateUserId(ValueUtil.toString(paramMap.get("userId")));
        Map<String,Object> userMap = (Map<String,Object>)paramMap.get("userMap");
        gateResource.setCreateUserName(ValueUtil.toString(userMap.get("name")));
        Long result = gateResourceService.insertPartly(gateResource);
        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", gateResource.getId());
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

//    @NoToken
    @RequestMapping(value = "/resource", method = RequestMethod.PATCH)
    public void update(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "id不能为空，请求参数错误"))
                .checkAndThrow();

        Map<String,Object> mapTemp = Maps.newHashMap();
        mapTemp.put("id",paramMap.get("id"));
        List<GateResource> resources = gateResourceService.query(mapTemp);
        if(CheckUtil.isEmpty(resources)){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该资源不存在，请重新提交！").toResponse();
            return;
        }

        if(!ValueUtil.isEmpty(ValueUtil.toString(paramMap.get("isDisabled")))){
            if(!(paramMap.get("isDisabled").equals("0") || paramMap.get("isDisabled").equals("1"))){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("参数传递错误！").toResponse();
                return;
            }
        }

        GateResource resource = resources.get(0);
        List<GateResource> resourceTemps = Lists.newArrayList();

        //如果该资源的method和path已存在，不可以重复添加
        Map<String,Object> map = Maps.newHashMap();
        if(!CheckUtil.isEmpty(paramMap.get("path")) && !CheckUtil.isEmpty(paramMap.get("method"))){
            map.put("method",paramMap.get("method"));
            map.put("path",paramMap.get("path"));
            resourceTemps = gateResourceService.query(map);
            if(!CheckUtil.isEmpty(resourceTemps)){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该资源已存在，请重新提交！").toResponse();
                return;
            }
        }

        if(!CheckUtil.isEmpty(paramMap.get("path")) && CheckUtil.isEmpty(paramMap.get("method"))){
            map.put("method",resource.getMethod());
            map.put("path",paramMap.get("path"));
            resourceTemps = gateResourceService.query(map);
            if(!CheckUtil.isEmpty(resourceTemps)){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该资源已存在，请重新提交！").toResponse();
                return;
            }
        }

        if(CheckUtil.isEmpty(paramMap.get("path")) && !CheckUtil.isEmpty(paramMap.get("method"))){
            map.put("method",paramMap.get("method"));
            map.put("path",resource.getPath());
            resourceTemps = gateResourceService.query(map);
            if(!CheckUtil.isEmpty(resourceTemps)){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该资源已存在，请重新提交！").toResponse();
                return;
            }
        }

        String result = update_resource(paramMap);
        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", result);
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

    private String update_resource(Map<String, Object> param) {
        GateResource gateResource = new GateResource();
        try {
            gateResource = (GateResource) ApiResultWrapper.mapToObject(param, GateResource.class);
        } catch (Exception e) {
            throw new ApplicationException("Map转换Bean出错！");
        }
        gateResource.setUpdateTime(new Date());
        gateResource.setUpdateUserId(ValueUtil.toString(param.get("userId")));
        Map<String,Object> userMap = (Map<String,Object>)param.get("userMap");
        gateResource.setUpdateUserName(ValueUtil.toString(userMap.get("name")));
        Long temp = gateResourceService.update(gateResource);
        if (temp > 0) {
            return gateResource.getId();
        }
        return null;
    }

    @ApiOperation(value = "第三步：资源返回", notes = "根据用户的手机号和项目ID  req：{'mobild':'18730685824','projectId':'981adf122d7643aa86324142471410e2'}", tags = {SwaggerContants.GATE_RESOURCE_ROLE})
    @RequestMapping(value = "/resourcesuser", method = RequestMethod.GET)
    public void resourcesuser(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        if(StringUtils.isEmpty(StringUtils.safeToString(paramMap.get("mobile"))) || StringUtils.isEmpty(StringUtils.safeToString(paramMap.get("projectId")))){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("用户手机号或项目名称不能为空！").toResponse();
            return;
        }

        Map<String,Object> result = Maps.newHashMap();
        Map<String,Object> userParam = Maps.newConcurrentMap();
        userParam.put("mobile",StringUtils.safeToString(paramMap.get("mobile")));
        List<GateUser> gateuser = gateUserService.query(userParam);
        if(gateuser != null && gateuser.size() >0){
            List<ResourceRole> resources = gateUserRoleService.getResourceRoleByUserId(gateuser.get(0).getId(),StringUtils.safeToString(paramMap.get("projectId")));
            result.put("resources",resources);
            result.put("userName",gateuser.get(0).getName());
            new ApiResultWrapper().setStatus(HttpStatus.OK).setObj(result).toResponse();
            return;
        }
        new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("没有该用户，请重试！").toResponse();
        return;
    }

}
