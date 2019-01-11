/*
 * Powered By [rapid-framework]
 * Web Site: http://blog.csdn.net/houfeng30920/article/details/52730893
 * Csdn Code: 
 * Since 2015 - 2017
 */
package com.intime.soa.anygate.controller;

import com.google.common.collect.Maps;
import com.intime.soa.framework.exception.ApplicationException;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.validation.ValidateManager;
import com.intime.soa.framework.validation.validator.NotEmptyValidator;
import com.intime.soa.model.anygate.GateUserIntime;
import com.intime.soa.service.anygate.GateUserIntimeService;
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
@RequestMapping(value = "/anygate")
public class GateUserIntimeController extends AnyGateBaseController {

    @Autowired
    GateUserIntimeService gateUserIntimeService;

    @RequestMapping(value = "/gateuserintime", method = RequestMethod.POST)
    public void add() {
        Long result = gateUserIntimeService.findMobileInsertUser();
        Map<String,Object> map = Maps.newHashMap();
        map.put("result","successful");
        if(result == null){
            map.put("result","fail");
        }
        new ApiResultWrapper().setObj(map).toResponse();
    }
}
