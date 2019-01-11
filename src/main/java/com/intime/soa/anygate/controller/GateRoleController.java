/*
 * Powered By [rapid-framework]
 * Web Site: http://blog.csdn.net/houfeng30920/article/details/52730893
 * Csdn Code: 
 * Since 2015 - 2017
 */
package com.intime.soa.anygate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intime.soa.framework.exception.ApplicationException;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.id.IdWorker;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.framework.validation.ValidateManager;
import com.intime.soa.framework.validation.validator.NotBlankValidator;
import com.intime.soa.framework.validation.validator.NotEmptyValidator;
import com.intime.soa.model.anygate.GateRole;
import com.intime.soa.model.anygate.GateUserRole;
import com.intime.soa.service.anygate.GateRoleService;
import com.intime.soa.service.anygate.GateUserRoleService;
import com.intime.soa.service.anygate.GateUserService;
import com.intime.soa.util.AnyGateBaseController;
import com.intime.soa.util.Constants;
import com.intime.soa.util.MapUtil;
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
public class GateRoleController extends AnyGateBaseController {

    @Autowired
    GateRoleService gateRoleService;

    @Autowired
    GateUserRoleService gateUserRoleService;

    @Autowired
    GateUserService gateUserService;

//    @NoToken
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public void getList(@RequestParam String query) {
        Map<String, Object> paramMap = getTokenParamMap(query);
        //{orderBy: "createTime",desc: "1",offset: "0",limit: "20",like:""}
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("offset,limit,projectId", "offset或limit或projectId不能为空，请求参数错误"))
                .checkAndThrow();

        List<GateRole> gateRoles = gateRoleService.query(paramMap);
        List<Map<String,Object>> list = Lists.newArrayList();
        for(GateRole gateRole : gateRoles){
            Map<String,Object> map =  gateRole.toMap();
            list.add(map);
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("rows", MapUtil.getPartMap("id,projectId,code,name,remark,isDisabled,createTime,createUserId,createUserName,updateTime,updateUserId,updateUserName,isDisabled",list));
        result.put("total", gateRoleService.queryCount(paramMap));
        new ApiResultWrapper().setObj(JsonHelper.toJson(result)).toResponse();
    }

//    @NoToken
    @RequestMapping(value = "/role", method = RequestMethod.DELETE)
    public void remove(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "id不能为空，请求参数错误"))
                .checkAndThrow();

        GateRole gateRole = gateRoleService.queryById(ValueUtil.toString(paramMap.get("id")));
        if(CheckUtil.isEmpty(gateRole)){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("id不存在！").toResponse();
            return;
        }
        if(gateRole.getCode() != null){
            if(gateRole.getCode().contains(Constants.ADMIN) || gateRole.getCode().contains(Constants.AnyGate_Admin)){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("系统警告：您在启动自毁模式！").toResponse();
                return;
            }
        }

        //判断该角色下是否存在用户
        Map<String, Object> paramUserRoleMap = Maps.newHashMap();
        paramUserRoleMap.put("roleId", paramMap.get("id"));
        List<GateUserRole> gateUserRoles = gateUserRoleService.query(paramUserRoleMap);

        List<String> users = Lists.newArrayList();
        for(GateUserRole gateUserRole : gateUserRoles){
            users.add(gateUserRole.getUserId());
        }

        if(!CheckUtil.isEmpty(users)){
            Long count = gateUserService.queryNotDeleteUserByLists(users);
            if (count != 0) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该角色存在用户,不可以删除！").toResponse();
                return;
            }
        }

        paramMap.put("isDelete", "1");
        String result = update_role(paramMap);
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
    @RequestMapping(value = "/role", method = RequestMethod.POST)
    public void add(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotBlankValidator("projectId,name", "projectId,name不能为空，请求参数错误！"))
                .checkAndThrow();

        if(!CheckUtil.isEmpty(paramMap.get("name"))){
            String temp = ValueUtil.toString(paramMap.get("name"));
            if(temp.toLowerCase().contains("admin")){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("不能包含保留字!").toResponse();
                return;
            }
        }

        GateRole gateRole = new GateRole();
        try {
            gateRole = JsonHelper.toObject(JsonHelper.toJson(paramMap), GateRole.class);
        } catch (Exception ex) {
            throw new ApplicationException("Map转Bean出错！");
        }
        gateRole.setCode(IdWorker.getUUID());
        gateRole.setId(IdWorker.getUUID());
        gateRole.setCreateTime(new Date());
        gateRole.setCreateUserId(ValueUtil.toString(paramMap.get("userId")));
        Map<String,Object> userMap = (Map<String,Object>)paramMap.get("userMap");
        gateRole.setCreateUserName(ValueUtil.toString(userMap.get("name")));
        Long result = gateRoleService.insertPartly(gateRole);
        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", gateRole.getId());
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

//    @NoToken
    @RequestMapping(value = "/role", method = RequestMethod.PATCH)
    public void update(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "id不能为空，请求参数错误"))
                .checkAndThrow();

        if(!CheckUtil.isEmpty(paramMap.get("name"))){
            String temp = ValueUtil.toString(paramMap.get("name"));
            if(temp.toLowerCase().contains("admin")){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("不能包含保留字！").toResponse();
                return;
            }
        }

        GateRole gateRole = gateRoleService.queryById(ValueUtil.toString(paramMap.get("id")));
        if (CheckUtil.isEmpty(gateRole)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("id不存在！").toResponse();
            return;
        }

        Boolean flag = false;
        if(gateRole.getCode() != null) {
            if (gateRole.getCode().contains(Constants.ADMIN) || gateRole.getCode().contains(Constants.AnyGate_Admin)) {
                flag = true;
            }
        }

        if(!CheckUtil.isEmpty(paramMap.get("code"))){
           if(flag){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("系统警告：该角色代码中不可包含关键字！").toResponse();
                return;
           }
        }

        if(!ValueUtil.isEmpty(ValueUtil.toString(paramMap.get("isDisabled")))) {
            if (!(paramMap.get("isDisabled").equals("0") || paramMap.get("isDisabled").equals("1"))) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("参数传递错误！").toResponse();
                return;
            }

            if (flag) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("系统警告：您在启动自毁模式！").toResponse();
                return;
            }
        }

        String result = update_role(paramMap);
        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", result);
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

    private String update_role(Map<String, Object> param) {
        GateRole gateRole = new GateRole();
        try {
            gateRole = (GateRole) ApiResultWrapper.mapToObject(param, GateRole.class);
        } catch (Exception e) {
            throw new ApplicationException("Map转换Bean出错！");
        }
        gateRole.setUpdateTime(new Date());
        gateRole.setUpdateUserId(ValueUtil.toString(param.get("userId")));
        Map<String,Object> userMap = (Map<String,Object>)param.get("userMap");
        gateRole.setUpdateUserName(ValueUtil.toString(userMap.get("name")));
        Long temp = gateRoleService.update(gateRole);
        if (temp > 0) {
            return gateRole.getId();
        }
        return null;
    }
}
