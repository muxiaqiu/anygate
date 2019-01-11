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
import com.intime.soa.framework.auth.NoToken;
import com.intime.soa.framework.exception.ApplicationException;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.id.IdWorker;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.framework.validation.ValidateManager;
import com.intime.soa.framework.validation.validator.NotBlankValidator;
import com.intime.soa.framework.validation.validator.NotEmptyValidator;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.service.anygate.GateUserService;
import com.intime.soa.util.AnyGateBaseController;
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
public class GateUserController extends AnyGateBaseController {

    @Autowired
    GateUserService gateUserService;

//    @NoToken
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public void getList(@RequestParam String query) {
        Map<String, Object> paramMap = getTokenParamMap(query);
        //{orderBy: "createTime",desc: "1",offset: "0",limit: "20",like:""}
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("offset,limit", "offset或limit不能为空，请求参数错误!"))
                .checkAndThrow();

        if(!CheckUtil.isEmpty(paramMap.get("like"))){
            String like = ValueUtil.toString(paramMap.get("like"));
            like = "%" + like + "%";
            paramMap.put("like", like);
        }

        List<GateUser> gateUsers = gateUserService.query(paramMap);
        List<Map<String,Object>> list = Lists.newArrayList();
        for(GateUser gateUser : gateUsers){
            Map<String,Object> map =  gateUser.toMap();
            list.add(map);
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("rows", MapUtil.getPartMap("id,name,mobile,createTime,createUserId,createUserName,updateTime,updateUserId,updateUserName,isDisabled",list));
        result.put("total", gateUserService.queryCount(paramMap));
        new ApiResultWrapper().setObj(JsonHelper.toJson(result)).toResponse();
    }

//    @NoToken
    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    public void remove(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "id不能为空，请求参数错误"))
                .checkAndThrow();

        if(paramMap.get("id").equals(paramMap.get("userId"))){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("系统警告：您在启动自毁模式！").toResponse();
            return;
        }

        paramMap.put("isDelete", "1");
        String result = update_user(paramMap);
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
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public void add(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotBlankValidator("name,mobile", "name,mobile不能为空，请求参数错误"))
                .checkAndThrow();

        GateUser gateUser = new GateUser();
        //检查手机号是否被占用
        if (!CheckUtil.isEmpty(ValueUtil.toString(paramMap.get("mobile")))) {
            Map<String, Object> paramUserMap = Maps.newHashMap();
            paramUserMap.put("mobile", paramMap.get("mobile"));
            List<GateUser> gateUsers = gateUserService.query(paramUserMap);
            if (!CheckUtil.isEmpty(gateUsers) ) {
                if(!gateUsers.get(0).getId().equals(paramMap.get("id"))){
                    new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该手机号被占用！").toResponse();
                    return;
                }
            }
        }

        try {
            gateUser = (GateUser) ApiResultWrapper.mapToObject(paramMap, GateUser.class);
        } catch (Exception e) {
            throw new ApplicationException("Map转换Bean出错！");
        }
        gateUser.setId(IdWorker.getUUID());
        gateUser.setCreateTime(new Date());
        gateUser.setCreateUserId(ValueUtil.toString(paramMap.get("userId")));
        Map<String,Object> userMap = (Map<String,Object>)paramMap.get("userMap");
        gateUser.setCreateUserName(ValueUtil.toString(userMap.get("name")));
        Long result = gateUserService.insertPartly(gateUser);
        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", gateUser.getId());
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

//    @NoToken
    @RequestMapping(value = "/user", method = RequestMethod.PATCH)
    public void update(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "id不能为空，请求参数错误!"))
                .checkAndThrow();

        if (!CheckUtil.isEmpty(ValueUtil.toString(paramMap.get("mobile")))) {
            Map<String, Object> paramUserMap = Maps.newHashMap();
            paramUserMap.put("mobile", paramMap.get("mobile"));
            List<GateUser> gateUsers = gateUserService.query(paramUserMap);
            if (!CheckUtil.isEmpty(gateUsers) ) {
                if(!gateUsers.get(0).getId().equals(paramMap.get("id"))){
                    new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该手机号被占用！").toResponse();
                    return;
                }
            }
        }

        String isDisabled = ValueUtil.toString(paramMap.get("isDisabled"));
        if(!ValueUtil.isEmpty(isDisabled)) {
            if(!(paramMap.get("isDisabled").equals("0") || paramMap.get("isDisabled").equals("1"))){
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("参数传递错误！").toResponse();
                return;
            }
            if (paramMap.get("id").equals(paramMap.get("userId")) && isDisabled.equals("1")) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("系统警告：您在启动自毁模式！").toResponse();
                return;
            }
        }

        String result = update_user(paramMap);
        if (CheckUtil.isEmpty(result)) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
            return;
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("id", result);
            new ApiResultWrapper().setObj(JsonHelper.toJson(resultMap)).toResponse();
        }
    }

    /**
     * 查询项目中前20条
     *
     * @param
     */
//    @NoToken
    @RequestMapping(value = "/queryusersby", method = RequestMethod.GET)
    public void queryuserby(@RequestParam String query) {
        Map<String, Object> paramMap = getTokenParamMap(query);
        //{orderBy: "createTime",desc: "1",offset: "0",limit: "20",like:""}
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("offset,limit,like", "offset或limit或like不能为空，请求参数错误"))
                .checkAndThrow();

        String like = ValueUtil.toString(paramMap.get("like"));
        like = "%" + like + "%";
        paramMap.put("like", like);

        Map<String, Object> result = Maps.newHashMap();
        result.put("rows", gateUserService.queryUserBy(paramMap));
        result.put("total", gateUserService.queryUserByCount(paramMap));
        new ApiResultWrapper().setObj(JsonHelper.toJson(result)).toResponse();
    }

    @NoToken
    @RequestMapping(value = "/adminuser", method = RequestMethod.GET)
    public void getuserbyid(@RequestParam String param) {
        Map<String, Object> result = getNoTokenParamMap(param);
        GateUser gateUser = gateUserService.queryById(StringUtils.safeToString(result.get("userId")));
        if(!CheckUtil.isEmpty(gateUser)){
            result = gateUser.toIntimeMap();
        }
        new ApiResultWrapper().setObj(JsonHelper.toJson(result)).toResponse();
    }

    private String update_user(Map<String, Object> param) {
        GateUser gateUser = new GateUser();

        try {
            gateUser = (GateUser) ApiResultWrapper.mapToObject(param, GateUser.class);
        } catch (Exception e) {
            throw new ApplicationException("Map转换Bean出错！");
        }
        gateUser.setUpdateTime(new Date());
        gateUser.setUpdateUserId(ValueUtil.toString(param.get("userId")));
        Map<String,Object> userMap = (Map<String,Object>)param.get("userMap");
        gateUser.setUpdateUserName(ValueUtil.toString(userMap.get("name")));
        Long temp = gateUserService.update(gateUser);
        if (temp > 0) {
            return gateUser.getId();
        }
        return null;
    }
}
