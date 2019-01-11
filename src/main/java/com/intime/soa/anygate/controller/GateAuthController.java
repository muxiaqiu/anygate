package com.intime.soa.anygate.controller;

import com.google.common.collect.Maps;
import com.intime.soa.anygate.auth.AuthUtil;
import com.intime.soa.anygate.constants.SwaggerContants;
import com.intime.soa.framework.LogCollectManager;
import com.intime.soa.framework.auth.Cryptogram;
import com.intime.soa.framework.auth.NoToken;
import com.intime.soa.framework.auth.TokenConstants;
import com.intime.soa.framework.service.cache.RedisCacheService;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.string.StringUtils;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.model.anygate.GateLog;
import com.intime.soa.model.anygate.GateProject;
import com.intime.soa.model.anygate.GateResource;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.model.anygate.auth.GateAuth;
import com.intime.soa.service.anygate.*;
import com.intime.soa.util.AnyGateBaseController;
import com.intime.soa.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.intime.soa.anygate.auth.AuthUtil.handle_uri;

/**
 * Created by qmx on 2017/12/7.
 */
@Api(tags = {SwaggerContants.GATE_AUTH_TAG})
@RestController
@RequestMapping(value = "/anygate")
public class GateAuthController extends AnyGateBaseController {

    @Autowired
    GateUserService gateUserService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    GateUserRoleService gateUserRoleService;

    @Autowired
    GateLogService gateLogService;

    @Autowired
    GateProjectService gateProjectService;

    @Autowired
    GateResourceService gateResourceService;

    @ApiOperation(value = "鉴权", notes = "根据token鉴定用户是否具有某Url下指定Method的权限", tags = {SwaggerContants.GATE_AUTH_TAG})
    @NoToken
    @RequestMapping(value = "auth", method = RequestMethod.POST)
    public GateAuth auth(@ApiParam(value = "参数: token、method、uri", required = true) @RequestBody String param) {

        GateAuth gateAuth = new GateAuth();
        Map<String, Object> paramMap = getNoTokenParamMap(param);
        LogCollectManager.common("Bgn <> request :" + JsonHelper.toJson(paramMap), "anygate", "gateAuth");

        if (!CheckUtil.isEmpty(paramMap.get("token"))) {
            String token = ValueUtil.toString(paramMap.get("token"));
            //验证token
            GateUser currentUser = parseToken(token);
            if (currentUser != null) {

                String cacheToken = redisCacheService.get(TokenConstants.ANYGATE_TOKEN_MAP + ValueUtil.toString(currentUser.getId()));
                if (CheckUtil.isEmpty(cacheToken)) {
                    gateAuth.setCode(0);
                    gateAuth.setMessage("token 已失效，请重新登录！");
                    gateAuth.setStatus(401);
                    LogCollectManager.common("Err <> request :" + JsonHelper.toJson(gateAuth), "anygate", "gateAuth");
                    return gateAuth;
                }

                if (!token.equals(cacheToken)) {
                    gateAuth.setCode(0);
                    gateAuth.setMessage("您的会话已超时，请重新登录！");
                    gateAuth.setStatus(401);
                    LogCollectManager.common("Err <> request :" + JsonHelper.toJson(gateAuth), "anygate", "gateAuth");
                    return gateAuth;
                }

                gateAuth = authenticate(ValueUtil.toString(paramMap.get("method")), ValueUtil.toString(paramMap.get("uri")), currentUser);
                List<GateResource> resources = getResource(ValueUtil.toString(paramMap.get("method")), ValueUtil.toString(paramMap.get("uri")));

                GateLog gateLog = new GateLog();
                gateLog.setUserId(currentUser.getId());
                gateLog.setUserName(currentUser.getName());
                gateLog.setMobile(currentUser.getMobile());
                gateLog.setIp(ValueUtil.toString(paramMap.get("ip")));
                gateLog.setUrl(ValueUtil.toString(paramMap.get("uri")));
                gateLog.setResult(ValueUtil.toInt(gateAuth.getCode()));
                if(resources != null && resources.size()>0){
                    gateLog.setUrlName(resources.get(0).getName());
                }
                if(StringUtils.isNotEmpty(StringUtils.safeToString(paramMap.get("param")))){
                    gateLog.setParam(StringUtils.safeToString(paramMap.get("param")));
                }
                gateLogService.insertPartly(gateLog);
                gateAuth.setUserId(ValueUtil.toString(currentUser.getId()));
                gateAuth.setToken(token);
                LogCollectManager.common("End <> request :" + JsonHelper.toJson(gateAuth), "anygate", "gateAuth");
                return gateAuth;
            }
        }

        gateAuth.setCode(0);
        gateAuth.setMessage("您的会话已超时，请重新登录！");
        gateAuth.setStatus(401);
        LogCollectManager.common("Err <> request :" + JsonHelper.toJson(gateAuth), "anygate", "gateAuth");
        return gateAuth;
    }

    protected GateUser parseToken(String token) {
        //验证token
        String decryptResult = Cryptogram.DESDecrypt(token, TokenConstants.TOKEN_KEY, TokenConstants.COMMON_IV);
        if (StringUtils.isBlank(decryptResult)) {
            LogCollectManager.error("Log <> token：" + token + " 解析失败！", logger);
            return null;
        }

        String[] key = decryptResult.split("-");
        if (key != null && key.length >= 5) {
            GateUser currentUser = new GateUser();
            currentUser.setId(key[0]);
            currentUser.setName(key[1]);
            currentUser.setIsAdminManager(ValueUtil.toInt(key[2]));
            currentUser.setMobile(key[3]);
            currentUser.setLastLoginTime(key[4]);
            return currentUser;
        }
        return null;
    }

    private List<GateResource> getResource(String method, String uri){
        Map<String, Object> param = Maps.newHashMap();
        param.put("method", method);
        uri = handle_uri(uri);
        if (!CheckUtil.isEmpty(uri)) {
            param.put("path", uri);
            List<GateResource> resourceList = gateResourceService.query(param);
            return resourceList;
        }
        return null;
    }

    protected GateAuth authenticate(String method, String uri, GateUser currentUser) {

        LogCollectManager.common(String.format("Log <> request < method:%s,uri:%s,currentUser:%s", method, uri, currentUser), "anygate", "gateAuth");
        logger.info(String.format("鉴权 <> request < method:%s,uri:%s,currentUser:%s", method, uri, currentUser));
        GateAuth gateAuth = new GateAuth();
        //鉴权
        Map<String, Object> param = Maps.newHashMap();
        param.put("method", method);
        uri = AuthUtil.handle_uri(uri);
        if (!CheckUtil.isEmpty(uri)) {
            param.put("path", uri);
        } else {
            gateAuth.setCode(0);
            gateAuth.setMessage("请求资源错误！".intern());
            gateAuth.setStatus(404);
            LogCollectManager.common(String.format("Log <> request < method:%s,uri:%s,currentUser:%s > result:%s", method, uri, currentUser, JsonHelper.toJson(gateAuth)), "anygate", "gateAuth");
            return gateAuth;
        }

        List<GateResource> resourceList = gateResourceService.query(param);
        LogCollectManager.common(String.format("Log <> method:%s,uri:%s,result:%s", method, uri, JsonHelper.toJson(resourceList)), "anygate", "gateAuth");
        logger.info("GateAuthController <> method:" + method + ",path:" + uri + ",result:" + JsonHelper.toJson(resourceList));

        if (CheckUtil.isEmpty(resourceList)) {
            gateAuth.setCode(0);
            gateAuth.setMessage("请求资源错误！".intern());
            gateAuth.setStatus(404);
            LogCollectManager.common(String.format("Log <> request < method:%s,uri:%s,currentUser:%s > result:%s", method, uri, currentUser, JsonHelper.toJson(gateAuth)), "anygate", "gateAuth");
            return gateAuth;
        }

        gateAuth.setUserName(currentUser.getName());
        //查看该资源是否是否是超级管理员，如果是超级管理员则不需要进行鉴权
        if (1 == currentUser.getIsAdminManager()) {
            gateAuth.setCode(1);
            return gateAuth;
        }

        //获取资源authMode,[0:all,1:token,2:byrole]
        GateResource gateResource = resourceList.get(0);
        if (CheckUtil.isEmpty(gateResource.getAuthMode())) {
            gateResource.setAuthMode("2");
        }

        if (gateResource.getAuthMode().equals("0") || gateResource.getAuthMode().equals("1")) {
            gateAuth.setCode(1);
            return gateAuth;
        }

        String projectId = ValueUtil.toString(gateResource.getProjectId());

        //查看是否某项目管理员，如果是则不需要进行鉴权
        GateProject gateProject = gateProjectService.queryById(projectId);
        String managers = gateProject.getManagers();
        if (!CheckUtil.isEmpty(managers)) {
            //根据roleId查询用户
            List<String> userIds = gateUserRoleService.queryUserIdsByRoleId(managers);
            for (String user : userIds) {
                if (user.equals(currentUser.getId())) {
                    gateAuth.setCode(1);
                    return gateAuth;
                }
            }
        }

        //查询该资源ID，查看该资源是否是项目管理员可以查看的。
        String resourceId = gateResource.getId();
        List<String> anyGateResourceList = gateUserRoleService.getResourceIdsByRoleCode(Constants.AnyGate_Admin);
        if (anyGateResourceList.contains(resourceId)) {
            List<String> roleCodes = gateUserRoleService.getRoleCodeByUserId(currentUser.getId());
            if (!CheckUtil.isEmpty(roleCodes)) {
                for (String roleCode : roleCodes) {
                    if (roleCode.contains(Constants.ADMIN)) {
                        gateAuth.setCode(1);
                        return gateAuth;
                    }
                }
            }
            gateAuth.setCode(0);
            gateAuth.setMessage("无权访问！".intern());
            gateAuth.setStatus(401);
            LogCollectManager.common(String.format("Err <> request < method:%s,uri:%s,currentUser:%s > result:%s", method, uri, currentUser, JsonHelper.toJson(gateAuth)), "anygate", "gateAuth");
            return gateAuth;
        }

        //如果不是需要鉴权
        List<Map<String, Object>> resources = gateUserRoleService.resourceListByUserId(currentUser.getId());
        for (Map<String, Object> map : resources) {
            if (resourceId.equals(ValueUtil.toString(map.get("resourceId")))) {
                gateAuth.setCode(1);
                return gateAuth;
            }
        }
        gateAuth.setCode(0);
        gateAuth.setMessage("无权访问！".intern());
        gateAuth.setStatus(401);
        return gateAuth;
    }
}
