/*
 * Powered By [rapid-framework]
 * Web Site: http://blog.csdn.net/houfeng30920/article/details/52730893
 * Csdn Code: 
 * Since 2015 - 2017
 */
package com.intime.soa.anygate.controller;

import com.google.common.collect.Lists;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.id.IdWorker;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.framework.validation.ValidateManager;
import com.intime.soa.framework.validation.validator.NotEmptyValidator;
import com.intime.soa.model.anygate.GateRole;
import com.intime.soa.model.anygate.GateRoleResource;
import com.intime.soa.service.anygate.GateRoleResourceService;
import com.intime.soa.service.anygate.GateRoleService;
import com.intime.soa.util.AnyGateBaseController;
import com.intime.soa.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/anygate")
public class GateRoleResourceController extends AnyGateBaseController {

    @Autowired
    GateRoleResourceService gateRoleResourceService;

    @Autowired
    GateRoleService gateRoleService;

//    @NoToken
    @RequestMapping(value = "/roleresources", method = RequestMethod.GET)
    public void getList(@RequestParam String query) {
        Map<String, Object> paramMap = getTokenParamMap(query);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("roleId", "roleId参数不能为空！"))
                .checkAndThrow();

        List<String> userRoleList = gateRoleResourceService.queryResourceByRole(ValueUtil.toString(paramMap.get("roleId")));
        new ApiResultWrapper().setObj(userRoleList).toResponse();
    }

//    @NoToken
    @RequestMapping(value = "/roleresources", method = RequestMethod.PATCH)
    public void add(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("roleId,resourceList", "roleId,resourceList参数不能为空，请求参数错误"))
                .checkAndThrow();

        String roleId = ValueUtil.toString(paramMap.get("roleId"));
        GateRole gateRole = gateRoleService.queryById(roleId);
        if(CheckUtil.isEmpty(gateRole)){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该角色不存在！").toResponse();
            return;
        }
        if(gateRole.getCode().contains(Constants.ADMIN)){
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("项目管理员角色不需要配置资源！").toResponse();
            return;
        }

        List<String> resourseList = Lists.newArrayList();
        resourseList =  (List)paramMap.get("resourceList");

        //先删除
        GateRoleResource gateRoleResourceDel = new GateRoleResource();
        gateRoleResourceDel.setRoleId(roleId);
        gateRoleResourceService.delete(gateRoleResourceDel);

        //再添加
        List<GateRoleResource> resourseRoleList = Lists.newArrayList();
        for (String obj : resourseList) {
            GateRoleResource gateRoleResource = new GateRoleResource();
            gateRoleResource.setRoleId(roleId);
            gateRoleResource.setResourceId(obj);
            gateRoleResource.setId(IdWorker.getUUID());
            resourseRoleList.add(gateRoleResource);
        }

        List<String> results = Lists.newArrayList();
        if(!CheckUtil.isEmpty(resourseRoleList)){
            Long result = gateRoleResourceService.insertBatch(resourseRoleList);
            if (result <= 0) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("操作失败！").toResponse();
                return;
            } else {
                for (GateRoleResource gateRoleResource : resourseRoleList) {
                    results.add(gateRoleResource.getId());
                }
                new ApiResultWrapper().setObj(JsonHelper.toJson(results)).toResponse();
            }
        }
        new ApiResultWrapper().setObj(JsonHelper.toJson(results)).toResponse();
    }

}
