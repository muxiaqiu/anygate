package com.intime.soa.util;

import com.intime.soa.framework.controller.BaseController;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.service.anygate.GateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by will on 16/5/3.
 */
@RestController
public abstract class AnyGateBaseController extends BaseController {

    @Autowired
    GateUserService gateUserService;

    @Override
    protected Map<String, Object> getUserInfo(String userId) {
        GateUser gateUser = gateUserService.queryById(userId);
        return gateUser.toMap();
    }
}
