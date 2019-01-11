/*
 * Powered By [rapid-framework]
 * Web Site: http://blog.csdn.net/houfeng30920/article/details/52730893
 * Csdn Code: 
 * Since 2015 - 2017
 */
package com.intime.soa.anygate.controller;

import com.intime.soa.framework.exception.ApplicationException;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.validation.ValidateManager;
import com.intime.soa.framework.validation.validator.NotEmptyValidator;
import com.intime.soa.model.anygate.GateUserEip;
import com.intime.soa.service.anygate.GateUserEipService;
import com.intime.soa.util.AnyGateBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author intime
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/tracker")
public class GateUserEipController extends AnyGateBaseController {

    @Autowired
    GateUserEipService gateUserEipService;

    @RequestMapping(value = "/gateusereip/list", method = RequestMethod.GET)
    public void getList(@RequestParam String query) {
        Map<String, Object> paramMap = getTokenParamMap(query);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("xxx", "请求参数错误"))
                .checkAndThrow();

        new ApiResultWrapper().setObj(gateUserEipService.query(paramMap)).toResponse();
    }

    @RequestMapping(value = "/gateusereip", method = RequestMethod.GET)
    public void getById(@RequestParam String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "请求参数错误"))
                .checkAndThrow();

        long id = ValueUtil.toLong(paramMap.get("id"));

        new ApiResultWrapper().setObj(gateUserEipService.queryById(id)).toResponse();
    }

    @RequestMapping(value = "/gateusereip", method = RequestMethod.DELETE)
    public void remove(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);
        new ValidateManager(paramMap)
                .add(new NotEmptyValidator("id", "请求参数错误"))
                .checkAndThrow();

        long id = ValueUtil.toLong(paramMap.get("id"));
        Long result = gateUserEipService.deleteById(id);
        if (result <= 0) {
            throw new ApplicationException("操作失败！");
        }
    }

    @RequestMapping(value = "/gateusereip", method = RequestMethod.POST)
    public void add(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);

        GateUserEip gateUserEip = null;
        try {
            gateUserEip = JsonHelper.toObject(JsonHelper.toJson(paramMap), GateUserEip.class);
        } catch (Exception ex) {
            throw new ApplicationException("请求参数错误");
        }

        Long result = gateUserEipService.insertPartly(gateUserEip);
        if (result <= 0) {
            throw new ApplicationException("操作失败！");
        }
    }

    @RequestMapping(value = "/gateusereip", method = RequestMethod.PATCH)
    public void update(@RequestBody String param) {
        Map<String, Object> paramMap = getTokenParamMap(param);

        GateUserEip gateUserEip = null;
        try {
            gateUserEip = JsonHelper.toObject(JsonHelper.toJson(paramMap), GateUserEip.class);
        } catch (Exception ex) {
            throw new ApplicationException("请求参数错误");
        }

        Long result = gateUserEipService.update(gateUserEip);
        if (result <= 0) {
            throw new ApplicationException("操作失败！");
        }
    }
}
