package com.intime.soa.anygate.auth;

import com.google.common.collect.Maps;
import com.intime.soa.framework.LogCollectManager;
import com.intime.soa.framework.auth.*;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.service.cache.RedisCacheService;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.env.MacAddressHelper;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.model.anygate.GateLog;
import com.intime.soa.model.anygate.GateProject;
import com.intime.soa.model.anygate.GateResource;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.service.anygate.*;
import com.intime.soa.util.Constants;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by will on 16/9/4.
 */
public class AnygateAuthInterceptor extends AbstractAuthInterceptor {

    @Autowired
    protected RedisCacheService redisCacheService;

    @Autowired
    protected GateUserService gateUserService;

    @Autowired
    protected GateResourceService gateResourceService;

    @Autowired
    protected GateProjectService gateProjectService;

    @Autowired
    protected GateUserRoleService gateUserRoleService;

    @Autowired
    protected GateLogService gateLogService;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AnygateAuthInterceptor.class);

    //存放鉴权信息的Header名称，默认是Authorization
    protected String httpCookieName = "Token";
    //鉴权失败后返回的错误信息，默认为401 unauthorized
    protected String unauthorizedErrorMessage = "401 unauthorized";
    //鉴权失败后返回的HTTP错误码，默认为401
    protected int unauthorizedErrorCode = HttpServletResponse.SC_UNAUTHORIZED;

    public void setHttpCookieName(String httpCookieName) {
        this.httpCookieName = httpCookieName;
    }

    public void setUnauthorizedErrorMessage(String unauthorizedErrorMessage) {
        this.unauthorizedErrorMessage = unauthorizedErrorMessage;
    }

    public void setUnauthorizedErrorCode(int unauthorizedErrorCode) {
        this.unauthorizedErrorCode = unauthorizedErrorCode;
    }

    @Override
    protected boolean doAuth(HttpServletRequest request, HttpServletResponse response, Method method) {

        String token = request.getHeader("Token".intern());
        if (token != null && token.length() > 0) {
            //验证token
            GateUser currentUser = parseToken(token);
            if (currentUser != null) {

                String cacheToken = redisCacheService.get(TokenConstants.ANYGATE_TOKEN_MAP + ValueUtil.toString(currentUser.getId()));
                if (CheckUtil.isEmpty(cacheToken)) {
                    new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setMessage("token 已失效，请重新登录！".intern()).toResponse();
                    return false;
                }

                LogCollectManager.common(String.format("Bgn <> token:%s,cacheToken:%s",token,cacheToken), "anygate", "own");

                if (!token.equals(cacheToken)) {
                    LogCollectManager.common(String.format("Err <> token:%s,cacheToken:%s 不匹配",token,cacheToken), "anygate", "own");
                    new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setMessage("您的会话已超时，请重新登录！".intern()).toResponse();
                    return false;
                }

                User user = new User();
                user.setUserId(ValueUtil.toString(currentUser.getId()));
                user.setToken(token);
                UserUtil.setCurrentUser(user);

                List<GateResource> resourceList = getResource(request);
                if(resourceList == null){
                    return false;
                }

                if (!authenticate(resourceList, currentUser)) {
                    return false;
                } else {
                    String ip = "";
                    try {
                        ip = MacAddressHelper.getIpAddr(request);
                    } catch (Exception e) {

                    }
                    GateLog gateLog = new GateLog();
                    gateLog.setUserId(currentUser.getId());
                    gateLog.setUserName(currentUser.getName());
                    gateLog.setMobile(currentUser.getMobile());
                    gateLog.setIp(ip);
                    gateLog.setUrl(request.getRequestURL().toString());
                    if(resourceList.size() > 0){
                        gateLog.setUrlName(resourceList.get(0).getName());
                    }
                    gateLog.setParam(request.getQueryString());
                    gateLog.setResult(response.getStatus());
                    gateLogService.insertPartly(gateLog);

                    return true;
                }
            }
        }

        new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setMessage("您的会话已超时，请重新登录！").toResponse();
        return false;
    }

    protected GateUser parseToken(String token) {
        //验证token
        String decryptResult = Cryptogram.DESDecrypt(token, TokenConstants.TOKEN_KEY, TokenConstants.COMMON_IV);
        if (com.intime.soa.framework.util.string.StringUtils.isBlank(decryptResult)) {
            LogCollectManager.error("Err <> token：" + token + " 解析失败！", logger);
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

    private List<GateResource> getResource(HttpServletRequest request){
        //鉴权
        String requestMethod = request.getMethod();
        String requestUri = request.getRequestURI().intern();
        String rel = request.getParameter(Constants.REL);

        if(!CheckUtil.isEmpty(rel)){
            requestUri += Constants.PARAM_REL + rel;
        }

        Map<String, Object> param = Maps.newHashMap();
        param.put("method", requestMethod);
        if (!CheckUtil.isEmpty(AuthUtil.handle_url(requestUri,Constants.ANYGATE_CATALOG))) {
            param.put("path", AuthUtil.handle_url(requestUri,Constants.ANYGATE_CATALOG));
        } else {
            new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setMessage("请求资源错误！".intern()).toResponse();
            return null;
        }

        List<GateResource> resourceList = gateResourceService.query(param);
        LogCollectManager.common(String.format("Log <> method:%s,path:%s,result:%s",requestMethod,requestUri,JsonHelper.toJson(resourceList)), "anygate", "own");
        logger.info("Log <> method:" + requestMethod + ",path:" + requestUri + ",result:" + JsonHelper.toJson(resourceList));
        return resourceList;
    }

    protected boolean authenticate(List<GateResource> resourceList, GateUser currentUser) {

        if (CheckUtil.isEmpty(resourceList)) {
            new ApiResultWrapper(HttpStatus.NOT_FOUND).setMessage("没有该资源！".intern()).toResponse();
            return false;
        }

        //查看该资源是否是否是超级管理员，如果是超级管理员则不需要进行鉴权
        if (1 == currentUser.getIsAdminManager()) {
            return true;
        }

        //获取资源authMode,[0:all,1:token,2:byrole]
        GateResource gateResource = resourceList.get(0);
        if(gateResource.getIsDelete()!= null && gateResource.getIsDelete().equals("1")){
            new ApiResultWrapper(HttpStatus.NOT_FOUND).setMessage("没有该资源！".intern()).toResponse();
            return false;
        }

        if(gateResource.getIsDisabled()!= null && gateResource.getIsDisabled().equals("1")){
            new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setMessage("该资源已禁用！".intern()).toResponse();
            return false;
        }

        if(CheckUtil.isEmpty(gateResource.getAuthMode())){
            gateResource.setAuthMode("2");
        }

        if (gateResource.getAuthMode().equals("0") || gateResource.getAuthMode().equals("1")) {
            return true;
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
                    return true;
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
                        return true;
                    }
                }
            }
        }

        //如果不是需要鉴权
        List<Map<String, Object>> resources = gateUserRoleService.resourceListByUserId(currentUser.getId());
        for (Map<String, Object> map : resources) {
            if (resourceId.equals(ValueUtil.toString(map.get("resourceId")))) {
                return true;
            }
        }
        return false;
    }
}
