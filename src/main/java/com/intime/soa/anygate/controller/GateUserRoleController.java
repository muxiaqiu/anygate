/*
 * Powered By [rapid-framework]
 * Web Site: http://blog.csdn.net/houfeng30920/article/details/52730893
 * Csdn Code: 
 * Since 2015 - 2017
 */
package com.intime.soa.anygate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.id.IdWorker;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.framework.validation.ValidateManager;
import com.intime.soa.framework.validation.validator.NotEmptyValidator;
import com.intime.soa.model.anygate.GateRole;
import com.intime.soa.model.anygate.GateUser;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/anygate")
public class GateUserRoleController extends AnyGateBaseController {

    @Autowired
    GateUserRoleService gateUserRoleService;

    @Autowired
    GateRoleService gateRoleService;

    @Autowired
    GateUserService gateUserService;


//    @NoToken
    @RequestMapping(value = "/usersrole", method = RequestMethod.GET)
    public void getList(@RequestParam String query) {
        Map<String, Object> paramMap = getTokenParamMap(query);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("offset,limit,roleId", "offset或limit或roleId不能为空，请求参数错误"))
                .checkAndThrow();

        List<Map<String,Object>> list = gateUserRoleService.queryUserByRole(paramMap);

        Map<String, Object> result = Maps.newHashMap();
        result.put("rows", MapUtil.getPartMap("id,rid,name,mobile,createTime,createUserName,updateTime,updateUserName",list));
        result.put("total", gateUserRoleService.queryUserByRoleCount(paramMap));
        new ApiResultWrapper().setObj(JsonHelper.toJson(result)).toResponse();
    }

//    @NoToken
    @RequestMapping(value = "/userrole", method = RequestMethod.DELETE)
    public void remove(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "id参数不能为空，请求参数错误"))
                .checkAndThrow();

        String id = ValueUtil.toString(paramMap.get("id"));

        //如果是超级管理员，需要将回收用户的超级管理员权限
        List<GateRole> roleLists = gateRoleService.queryByRelationRoleId(id);
        if(CheckUtil.isEmpty(roleLists)){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("roleId不存在！").toResponse();
        }
        if(roleLists.get(0).getCode().equals(Constants.AnyGate_SuperAdmin)){
            GateUserRole userRole = gateUserRoleService.queryById(id);
            if(!CheckUtil.isEmpty(userRole)){
                GateUser gateUser = new GateUser();
                gateUser.setId(userRole.getUserId());
                gateUser.setIsAdminManager(0);
                gateUserService.update(gateUser);
            }
        }

        //更新userRole表
        Long result = gateUserRoleService.deleteById(id);

        if (result <= 0) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", id);
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

//    @NoToken
    @RequestMapping(value = "/usersrole", method = RequestMethod.POST)
    public void add(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("roleId,userList", "roleId,userList参数不能为空，请求参数错误"))
                .checkAndThrow();

        String roleId = ValueUtil.toString(paramMap.get("roleId"));
        List<String> userList = (ArrayList<String>)(paramMap.get("userList"));

        //对userList去重
        Set<String> userResult = Sets.newHashSet();
        List<String> temp = Lists.newArrayList();
        for(String strTemp:userList){
            int beforeSize = userResult.size();
            userResult.add(strTemp);
            int afterSize = userResult.size();
            if(beforeSize + 1 == afterSize){
                temp.add(strTemp);
            }
        }
        userList.clear();
        userList.addAll(temp);

        //TODO:判断role是否为AnyGate_SuperAdmin，如果是则更新user表中的is_admin_manager = '1'
        GateRole role =gateRoleService.queryById(ValueUtil.toString(paramMap.get("roleId")));
        if(CheckUtil.isEmpty(role)){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("roleId不存在！").toResponse();
        }
        if(role.getCode().equals(Constants.AnyGate_SuperAdmin)){
            List<GateUser> listUsers = Lists.newArrayList();
            for(String userStr:userList){
                GateUser gateUser = new GateUser();
                gateUser.setId(userStr);
                gateUser.setIsAdminManager(1);
                listUsers.add(gateUser);
            }
            gateUserService.updateBatch(listUsers);
        }

        //userList在数据库中是否已存在
        List<GateUserRole> userRoleList = Lists.newArrayList();
        Map<String,Object> map = Maps.newHashMap();
        temp.clear();
        map.put("roleId",roleId);
        List<GateUserRole> userRolesList = gateUserRoleService.query(map);
        for(GateUserRole userrole: userRolesList){
            for(String userTemp:userList){
                if(userrole.getUserId().equals(userTemp)){
                    temp.add(userTemp);
                }
            }
        }
        userList.removeAll(temp);

        for (String str : userList) {
            GateUserRole gateUserRole = new GateUserRole();
            gateUserRole.setId(IdWorker.getUUID());
            gateUserRole.setRoleId(roleId);
            gateUserRole.setUserId(str);
            userRoleList.add(gateUserRole);
        }

        List<String> results = Lists.newArrayList();
        if(!CheckUtil.isEmpty(userRoleList)){
            Long result = gateUserRoleService.insertBatch(userRoleList);
            if (result <= 0) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
                return;
            } else {
                for (GateUserRole gateUserRole : userRoleList) {
                    results.add(gateUserRole.getUserId());
                }
            }
        }
        new ApiResultWrapper().setObj(JsonHelper.toJson(results)).toResponse();
    }

//	@RequestMapping(value = "/userrole", method = RequestMethod.PATCH)
//	public void update(@RequestBody  String param) {
//		Map<String, Object> paramMap = getTokenParamMap(param);
//
//		GateUserRole gateUserRole = null;
//		try {
//			gateUserRole = JsonHelper.toObject(JsonHelper.toJson(paramMap), GateUserRole.class);
//		} catch (Exception ex) {
//			throw new ApplicationException("请求参数错误");
//		}
//
//		Long result = gateUserRoleService.update(gateUserRole);
//		if(result <= 0){
//		    throw new ApplicationException("操作失败！");
//		}
//	}

//	@RequestMapping(value = "/queryuserbyrole", method = RequestMethod.GET)
//	public void queryuserbyrole(@RequestParam  String param) {
//		Map<String, Object> paramMap = getTokenParamMap(param);
//		new ValidateManager(paramMap)
//				.add(new NotEmptyValidator("roleId,offset,limit","roleId,offset,limit参数不能为空！"))
//				.checkAndReturn();
//
//		List<Map<String,Object>> userRoleList  = gateUserRoleService.queryUserByRole(paramMap);
//		new ApiResultWrapper().setObj(userRoleList).toResponse();
//	}

//    @RequestMapping(value = "/updateuserbyrole", method = RequestMethod.GET)
//    public void updateuserbyrole(@RequestParam String param) {
//        Map<String, Object> paramMap = getTokenParamMap(param);
//        new ValidateManager(paramMap)
//                .add(new NotEmptyValidator("roleId", "roleId参数不能为空！"))
//                .checkAndReturn();
//
//        String roleId = ValueUtil.toString(paramMap.get("roleId"));
//
//        if (CheckUtil.isEmpty(paramMap.get("delete"))) {
//            List<Map<String, Object>> deleteList = JsonHelper.toList(ValueUtil.toString(paramMap.get("delete")));
//            List<GateUserRole> deleteUserRoleList = Lists.newArrayList();
//            if (!CheckUtil.isEmpty(deleteList)) {
//                for (Map<String, Object> deleteMap : deleteList) {
//                    GateUserRole gateUserRole = new GateUserRole();
//                    gateUserRole.setRoleId(roleId);
//                    gateUserRole.setUserId(ValueUtil.toString(deleteMap.get("userId")));
//                    deleteUserRoleList.add(gateUserRole);
//                }
//                gateUserRoleService.deleteBatch(deleteUserRoleList);
//            }
//        }
//
//        if (CheckUtil.isEmpty(paramMap.get("add"))) {
//            List<Map<String, Object>> deleteList = JsonHelper.toList(ValueUtil.toString(paramMap.get("add")));
//            List<GateUserRole> deleteUserRoleList = Lists.newArrayList();
//            if (!CheckUtil.isEmpty(deleteList)) {
//                for (Map<String, Object> deleteMap : deleteList) {
//                    GateUserRole gateUserRole = new GateUserRole();
//                    gateUserRole.setRoleId(roleId);
//                    gateUserRole.setUserId(ValueUtil.toString(deleteMap.get("userId")));
//                    deleteUserRoleList.add(gateUserRole);
//                }
//                gateUserRoleService.insertBatch(deleteUserRoleList);
//            }
//        }
//        new ApiResultWrapper().setObj("success").toResponse();
//    }
}
