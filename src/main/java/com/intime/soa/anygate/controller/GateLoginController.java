package com.intime.soa.anygate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intime.soa.framework.EnvManager;
import com.intime.soa.framework.LogCollectManager;
import com.intime.soa.framework.auth.Cryptogram;
import com.intime.soa.framework.auth.NoToken;
import com.intime.soa.framework.auth.TokenConstants;
import com.intime.soa.framework.auth.gateauth.Util.EnvEnum;
import com.intime.soa.framework.exception.ApplicationException;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.service.cache.RedisCacheService;
import com.intime.soa.framework.service.message.SMSNewService;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.date.DateUtils;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.string.StringUtils;
import com.intime.soa.framework.validation.ValidateManager;
import com.intime.soa.model.anygate.GateProject;
import com.intime.soa.model.anygate.GateResource;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.service.anygate.*;
import com.intime.soa.service.user.UserService;
import com.intime.soa.util.AnyGateBaseController;
import com.intime.soa.util.EnvResourceEnum;
import com.intime.soa.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

/**
 * Created by qmx on 2017/12/7.
 */
@RestController
@RequestMapping(value = "/anygate")
public class GateLoginController extends AnyGateBaseController {

    private static String redisKeyTemplate = "AnyGate:User:%s:count";
    private static String redisKeyPasswordTemplate = "AnyGate:User:%s:password";
    private static String redisKeyLoginCountTemplate = "AnyGate:User:%s:errorCount";
    private static String redisKeySerialNum = "AnyGate:User:%s:SerialNum";

    private static String QRCodeSign = "AnyGate:Certification:%s:Sign";
    private static String QRCodeToken = "AnyGate:Certification:%s:Token";
    private static String QRCodeStatus = "AnyGate:Certification:%s:Status";

    private static String indexType = "anygate";
    private static String dataType =  "certification";

    @Autowired
    GateUserService gateUserService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    SMSNewService smsNewService;

    @Autowired
    GateRoleResourceService gateRoleResourceService;

    @Autowired
    GateUserRoleService gateUserRoleService;

    @Autowired
    UserService userService;

    @Autowired
    GateResourceService gateResourceService;

    @Autowired
    GateProjectService gateProjectService;

    @NoToken
    @RequestMapping(value = "captcha", method = RequestMethod.POST)
    public void captcha(@RequestBody String param) {
        Map<String, Object> paramMap = getNoTokenParamMap(param);
        String mobile = ValueUtil.toString(paramMap.get("mobile"));

        if (StringUtils.isEmpty(mobile)) {
            throw new ApplicationException("请先输入手机号码。");
        }

        String redisKey = String.format(redisKeyTemplate, mobile);
        String redisKeyPassword = String.format(redisKeyPasswordTemplate, mobile);
        String redisKeySerial = String.format(redisKeySerialNum, mobile);

        Map<String, Object> params = new HashMap();
        params.put("mobile", mobile);
        params.put("phone", mobile);
        List<GateUser> listUser = gateUserService.query(params);

        Map<String, Object> result = new HashMap();
        result.put("enabled", false);
        result.put("retry", false);

        //用户不存在
        if (listUser == null || listUser.size() == 0) {
            result.put("message", "此手机号码未注册。\\n如有疑问，请致电客服 400-188-8848。");
            new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setObj(result).toResponse();
            return;
        }

        //用户可用 不可用：不可用登录失败
        GateUser user = listUser.get(0);
        if (!"0".equals(StringUtils.safeToString(user.getIsDelete()))) {
            result.put("message", "此手机号码已被禁用。\\n如有疑问，请致电客服 400-188-8848。");
            new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setObj(result).toResponse();
            return;
        }

        //60分钟内发送三次，锁定用户
        String count = redisCacheService.get(redisKey);
        if (StringUtils.isEmpty(count)) {
            redisCacheService.set(redisKey, "1", 1 * 60 * 60);
        } else {
            redisCacheService.incr(redisKey);
        }
        count = redisCacheService.get(redisKey);
        int sendLimit = Integer.parseInt(count);
        if (sendLimit > 3) {
            result.put("message", "发送验证码太频繁，请稍后再试。");
            new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setObj(result).toResponse();
            return;
        }

        Map<String, Object> serialNumber = Maps.newHashMap();
        String SerialNum = redisCacheService.get(redisKeySerial);

        if (StringUtils.isBlank(SerialNum)) {
            redisCacheService.set(redisKeySerial, "1", 3600 * 24);
        } else {
            redisCacheService.incr(redisKeySerial);
        }
        SerialNum = redisCacheService.get(redisKeySerial);
        serialNumber.put("serialNumber", SerialNum);

        String password = StringUtils.getRandomNumbers(6);
        String message = "[AnyGate登录] 登录验证码：  " + password + ", 一分钟之内有效，序列号： (" + SerialNum + ")";
        smsNewService.send(mobile, message);
        redisCacheService.set(redisKeyPassword, password, 1 * 60);
        new ApiResultWrapper().setObj(serialNumber).toResponse();
    }

    /**
     * anygate登录
     */
    @NoToken
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public void twAuthenticate(@RequestBody String param) {
        Map<String, Object> paramMap = getNoTokenParamMap(param);
        String mobile = ValueUtil.toString(paramMap.get("mobile"));
        String captcha = ValueUtil.toString(paramMap.get("captcha"));

        String redisKeyPassword = String.format(redisKeyPasswordTemplate, mobile);
        String redisKeyerrorCount = String.format(redisKeyLoginCountTemplate, mobile);

        String password = redisCacheService.get(redisKeyPassword);
        String loginErrorCount = redisCacheService.get(redisKeyerrorCount);

        Boolean isPro = EnvManager.isPro();

        if (isPro == false && captcha.equals("123456")) {
            password = captcha;
        }

        //密码不在有效期 或者 填写的密码不正确
        if (password == null || !captcha.equals(password)) {
            if (loginErrorCount == null) {
                redisCacheService.set(redisKeyerrorCount, "1", 60);

                //错误次数<3 h5端可以重试
                Map<String, Object> result = new HashMap();
                result.put("enabled", true);
                result.put("retry", true);
                result.put("message", "验证码错误，请重新输入！");
                new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setObj(result).toResponse();
                return;

            }

            if (Integer.parseInt(loginErrorCount) >= 3) {
                Map<String, Object> result = new HashMap();
                result.put("enabled", true);
                result.put("retry", false);
                result.put("message", "验证码已失效，请重新获取。");
                new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setObj(result).toResponse();
                return;
            }

            if (Integer.parseInt(loginErrorCount) < 3) {
                //错误次数<3 h5端可以重试
                Map<String, Object> result = new HashMap();
                result.put("enabled", true);
                result.put("retry", true);
                result.put("message", "验证码错误，请重新输入！");
                redisCacheService.incr(redisKeyerrorCount);
                new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setObj(result).toResponse();
                return;
            }

        }
        //登录成功
        else if (password != null && captcha.equals(password)) {
//            Map<String, Object> params = new HashMap();
//            params.put("mobile", mobile);
//            params.put("phone", mobile);
//            params.put("isDisabled","0");
//            List<GateUser> listUser = gateUserService.query(params);
//
//            if (listUser == null || listUser.size() == 0) {
//                new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setMessage("该用户已被禁用！").toResponse();
//                return;
//            }
//            GateUser gateUser = listUser.get(0);
//
//            //修改用户最后登录时间
//            gateUser.setLastLoginTime(DateUtils.getDateTime());
//            gateUser.setUpdateTime(new Date());
//            gateUserService.update(gateUser);
//
//            String token = gateUser.getId() + "-" + gateUser.getName() + "-" + gateUser.getIsAdminManager() + "-" + gateUser.getMobile() + "-" + System.currentTimeMillis();
//            String newToken = Cryptogram.DESEncrypt(token, TokenConstants.TOKEN_KEY, TokenConstants.COMMON_IV);
//
//            response.setHeader("Token", newToken);
            GateUser gateUser = new GateUser();
            String newToken = getTokenByUserMobile(mobile, gateUser);
            if (ValueUtil.isEmpty(newToken)) {
                new ApiResultWrapper(HttpStatus.UNAUTHORIZED).setMessage("该用户已被禁用！").toResponse();
                return;
            }

            redisCacheService.set(TokenConstants.ANYGATE_TOKEN_MAP + gateUser.getId().toString(), newToken, 5 * 24 * 3600);
            response.setHeader("Token", newToken);
            new ApiResultWrapper(ApiResultWrapper.STATUS_OK).toResponse();
        }
    }

    private String getTokenByUserMobile(String mobile, GateUser gateUser) {
        Map<String, Object> params = new HashMap();
        params.put("mobile", mobile);
        params.put("phone", mobile);
        params.put("isDisabled", "0");
        List<GateUser> listUser = gateUserService.query(params);

        if (listUser == null || listUser.size() == 0) {
            return null;
        }
        GateUser gateUserTemp = listUser.get(0);
        gateUser.setId(gateUserTemp.getId());

        //修改用户最后登录时间
        gateUserTemp.setLastLoginTime(DateUtils.getDateTime());
        gateUserTemp.setUpdateTime(new Date());
        gateUserService.update(gateUserTemp);

        String token = gateUserTemp.getId() + "-" + gateUserTemp.getName() + "-" + gateUserTemp.getIsAdminManager() + "-" + gateUserTemp.getMobile() + "-" + System.currentTimeMillis();
        String newToken = Cryptogram.DESEncrypt(token, TokenConstants.TOKEN_KEY, TokenConstants.COMMON_IV);
        return newToken;
    }

    /**
     * anygate登录
     */
    @RequestMapping(value = "userinfo", method = RequestMethod.GET)
    public void userInfo() {
        Map<String, Object> paramMap = getTokenParamMap();
        //返回用户有权限访问的资源以及资源tree
        Map<String, Object> map = getMenu(ValueUtil.toString(paramMap.get("userId")));

        if (EnvManager.isPro()) {
            new ApiResultWrapper(ApiResultWrapper.STATUS_OK).setObj(map).toResponse();
            return;
        }

        String tempEnv = EnvEnum.getEnv(EnvManager.getEnv());
        if (tempEnv != null) {
            response.setHeader("Env", tempEnv);
            new ApiResultWrapper(ApiResultWrapper.STATUS_OK).setObj(map).toResponse();
            return;
        } else {
            new ApiResultWrapper().setStatus(HttpStatus.INTERNAL_SERVER_ERROR).setMessage("环境参数错误！").toResponse();
            return;
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public void logout() {
        Map<String, Object> paramMap = getTokenParamMap();
        //删除userId在Redis中的Token信息
        redisCacheService.del(TokenConstants.ANYGATE_TOKEN_MAP + ValueUtil.toString(paramMap.get("userId")));
    }

    @NoToken
    @RequestMapping(value = "/certification/getsign", method = RequestMethod.GET)
    public void getSign() {

        boolean flag = false;
        String timeStamp = null;
        for (int i = 0; i < 3; i++) {
            timeStamp = ValueUtil.toString(System.nanoTime());
            //存储在Redis中
            String timeStampTemp = String.format(QRCodeSign, timeStamp);
            Long result = redisCacheService.setnx(timeStampTemp.getBytes(), timeStamp.getBytes());
            redisCacheService.expire(timeStampTemp.getBytes(),60);
            if (result == 1) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            new ApiResultWrapper().setStatus(HttpStatus.SERVICE_UNAVAILABLE).setMessage("签名生成失败！").toResponse();
            return;
        }

        Map<String,Object> map = Maps.newHashMap();
        map.put("sign",timeStamp);
        LogCollectManager.common(map,String.format("Bgn <> sign:%s",timeStamp),indexType,dataType);

        //返回信息
        Map<String, Object> response = Maps.newConcurrentMap();
        response.put("sign", timeStamp);
        response.put("validateUrl", "/api/anygate/certification/login".intern());

        new ApiResultWrapper().setStatus(OK).setObj(response).toResponse();
    }

    @NoToken
    @RequestMapping(value = "/certification/auth", method = RequestMethod.OPTIONS)
    public void authOptions(@RequestBody String query) {
        auth(query);
    }

    @NoToken
    @RequestMapping(value = "/certification/auth", method = RequestMethod.GET)
    public void auth(@RequestParam String query) {
        Map paramMap = getNoTokenParamMap(query);
        ValidateManager.checkNotEmpty("sign,status", "sign或status不能为空!").checkAndThrow(paramMap);

        String sign = ValueUtil.toString(paramMap.get("sign"));
        String origin = "https://app.simuyun.com".intern();
        response.setHeader("Access-Control-Allow-Origin", origin);

        //判断sign是否有效签名
        boolean flag = valiedateSign(sign);
        if (!flag) {
            new ApiResultWrapper().setStatus(HttpStatus.UNAUTHORIZED).setMessage("签名无效！").toResponse();
            return;
        }

        String status = ValueUtil.toString(paramMap.get("status"));
        LogCollectManager.common(paramMap,String.format("Log <> sign:%s,status:%s",sign,status),indexType,dataType);
        if (!("0".equals(status) || "1".equals(status) || "2".equals(status))) {
            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("status值不正确！").toResponse();
            return;
        }

        //status:'0':取消  status:‘1’：扫描成功 status：'2':确认登录
        String statusTemp = String.format(QRCodeStatus, sign);
        //存储在Redis中，在轮询中进行返回
        redisCacheService.setObj(statusTemp.getBytes(), status, 1 * 60);
        if ("0".equals(status)) {
            new ApiResultWrapper().setStatus(HttpStatus.OK).toResponse();
            return;
        }

        if ("1".equals(status)) {
            new ApiResultWrapper().setStatus(HttpStatus.OK).toResponse();
            return;
        }

        //status：'2':确认登录 进行校验Token
        if ("2".equals(status)) {
            //解析token
            String token = ValueUtil.toString(paramMap.get("token"));
            //token = EncryptUtil.simpleDecrypt(token);
            if (StringUtils.isEmpty(token)) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("token不能为空！").toResponse();
                return;
            }

            //从私募云token中获取用户的手机号
            String decryptResult = Cryptogram.DESDecrypt(token, TokenConstants.TOKEN_KEY, TokenConstants.COMMON_IV);
            if (StringUtils.isBlank(decryptResult)) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("token解析失败！").toResponse();
                return;
            }

            String[] userInfo = decryptResult.split(TokenConstants.SEPERATOR);
            if (userInfo == null || !(userInfo.length > 2)) {
                new ApiResultWrapper().setStatus(HttpStatus.UNAUTHORIZED).setMessage("token无效！").toResponse();
                return;
            }

            String userId = userInfo[1];
            //根据userId，获取用户信息
            String mobile = "";
            if (StringUtils.isNotEmpty(userId)) {
                Map<String, Object> userMap = userService.selectAppUserById(userId);
                mobile = ValueUtil.toString(userMap.get("phone_num"));
            }

            if (ValueUtil.isEmpty(mobile)) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该用户手机号为空，请在私募云上不填手机号，才可登录AnyGate！").toResponse();
                return;
            }

            GateUser gateUser = new GateUser();
            String newToken = getTokenByUserMobile(mobile, gateUser);
            if (ValueUtil.isEmpty(newToken)) {
                new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("该手机号不存在，请确认之后再试！").toResponse();
                return;
            }

            Map<String, Object> user = Maps.newConcurrentMap();
            user.put("token", newToken);
            user.put("userId", gateUser.getId());
            String userJson = JsonHelper.toJson(user);
            redisCacheService.setObj(String.format(QRCodeToken, sign).getBytes(), userJson, 60 * 1);
            paramMap.put("token",newToken);
            paramMap.put("mobile",mobile);
            paramMap.put("gateUser",ValueUtil.toString(gateUser.getName()));
            LogCollectManager.common(paramMap,String.format("Log <> sign:%s,status:%s,token:%s,mobile:%s,gateUser:%s",sign,status,newToken,mobile,gateUser),indexType,dataType);
            new ApiResultWrapper().setStatus(OK).toResponse();
        }
    }

    @NoToken
    @RequestMapping(value = "/certification/login", method = RequestMethod.POST)
    public void login(@RequestBody String param) {
        Map paramMap = getNoTokenParamMap(param);
        ValidateManager.checkNotEmpty("sign", "签名不能为空！").checkAndThrow(paramMap);

        String sign = ValueUtil.toString(paramMap.get("sign"));
        boolean flag = valiedateSign(sign);
        if (!flag) {
            //202：已失效
            new ApiResultWrapper().setStatus(HttpStatus.ACCEPTED).toResponse();
            return;
        }

        //status:'0':取消  status:‘1’：扫描成功 status：'2':确认登录
        String statusTemp = String.format(QRCodeStatus, sign);
        String status = redisCacheService.getObj(statusTemp.getBytes());

        //200：等待扫描 sign存在
        if (ValueUtil.isEmpty(status)) {
            new ApiResultWrapper().setStatus(HttpStatus.OK).toResponse();
            return;
        }

        //204：已取消
        if ("0".equals(status)) {
            new ApiResultWrapper().setStatus(HttpStatus.NO_CONTENT).toResponse();
            return;
        }

        //201：已扫
        if ("1".equals(status)) {
            new ApiResultWrapper().setStatus(HttpStatus.CREATED).toResponse();
            return;
        }

        if ("2".equals(status)) {
            String userJson = redisCacheService.getObj(String.format(QRCodeToken, sign).getBytes());
            if (StringUtils.isBlank(userJson)) {
                //用户信息不存在
                new ApiResultWrapper().setStatus(HttpStatus.UNAUTHORIZED).toResponse();
                return;
            }
            Map<String, Object> userInfo = JsonHelper.convertJsonToMap(userJson);
            String userId = ValueUtil.toString(userInfo.get("userId"));
            String token = ValueUtil.toString(userInfo.get("token"));
            if (StringUtils.isBlank(userId) && StringUtils.isBlank(token)) {
                //401：用户不存在
                new ApiResultWrapper().setStatus(HttpStatus.UNAUTHORIZED).toResponse();
                return;
            }

            redisCacheService.del(String.format(QRCodeSign, sign));
            redisCacheService.del(String.format(QRCodeToken, sign));
            redisCacheService.set(TokenConstants.ANYGATE_TOKEN_MAP + userId, token, 5 * 24 * 3600);
            response.setHeader("Token", token);
            //203：已确认
            new ApiResultWrapper().setStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION).toResponse();
        }
    }

    private boolean valiedateSign(String sign) {
        byte[] timeByte = redisCacheService.get((String.format(QRCodeSign, sign).getBytes()));
        if(timeByte == null){
            return false;
        }

        String signRedis = new String(timeByte);
        if (ValueUtil.isEmpty(signRedis)) {
            return false;
        }
        if (!signRedis.equals(sign)) {
            return false;
        }
        return true;
    }

    private Map<String, Object> getMenu(String userId) {
        //获取数据库中该用户所有资源
        Map<String,Object> result = gateUserRoleService.getMenu(userId);
        //经过系统环境参数更新该用户资源
        getResourceForEnv(result);
        return result;
    }

    private Map<String,Object> getResourceForEnv(Map<String,Object> result){
        String tempEnv = StringUtils.safeToString(EnvEnum.getEnv(EnvManager.getEnv()));
        if(EnvResourceEnum.TESN.getIndex().equals(tempEnv)){
            Map<String,Object> map = Maps.newHashMap();
            map.put("code", EnvResourceEnum.TESN.getProjectCode());

            List<GateProject> gateProjects = gateProjectService.query(map);
            if(gateProjects != null && gateProjects.size()>0){
                GateProject gateProjectForTesN = gateProjects.get(0);
                gateProjects.clear();
                gateProjects.add(gateProjectForTesN);
                List<Map<String,Object>> projectsListMap = Lists.newArrayList();
                for(GateProject gateProject : gateProjects){
                    Map<String,Object> projectMap =  gateProject.toMap();
                    projectMap.put("resourcesTree",gateProject.getStorage());
                    projectsListMap.add(projectMap);
                }
                result.put("projects", MapUtil.getPartMap("id,protocol,name,domain,code,resourcesTree",projectsListMap));

                map.clear();
                map.put("projectId",gateProjectForTesN.getId());
                map.put("region","tesn");
                List<GateResource> resources = gateResourceService.query(map);
                List<Map<String,Object>> gateResourceListMap = Lists.newArrayList();
                for(GateResource gateResource : resources){
                    Map<String,Object> resourceMap =  gateResource.toMap();
                    resourceMap.put("resourceId",gateResource.getId());
                    gateResourceListMap.add(resourceMap);
                }
                result.put("resources", MapUtil.getPartMap("parentId,icon,resourceId,name,path,method,projectId,type",gateResourceListMap));
                return result;
            }
            throw new ApplicationException("无Galaxy项目，请创建！");
        }
        return result;
    }



    public static void main(String[] args) {
//        String timeStamp = ValueUtil.toString(System.currentTimeMillis());
//        try {
//            byte[] signByte = RSA.encryptByPublicKey(timeStamp.getBytes(), RSAKey.PUBLIC_KEY_FOR_QRCODE);
//            signByte = Base64Utils.encode(signByte);
//            String str = new String(signByte);
//            System.out.println(str);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        String sign = "Ckal48oTVcaKagbU/r37Xz1bRh8qgcIzOk2Tvrf89fJuyS5qdZdIDuvP343269gLd3w5FWmFXrRNkCe8Pnv0sPWYHlonj+GQg1NX2Rjjz05ywxahdfQELirV9dP32VcLI7tPpjAVgEJGtGO0H8sWcRyF8cgm5lkqc08Xl9+gXj0=";
//        String timeStamp = "";
//        try {
//            byte[] temp = Base64Utils.decode(sign.getBytes());
//            byte[] timeStampTemp = RSA.decryptByPrivateKey(temp, RSAKey.PRIVATE_KEY_FOR_QRCODE);
//            timeStamp = new String(timeStampTemp);
//            System.out.println(timeStamp);
//        } catch (Exception e) {
//            System.out.println("123");
//        }

//        String str = ValueUtil.toString(System.nanoTime());
////        System.out.println(str);
//        byte[] value = str.getBytes();
//
//        String result = (String) SerializeUtil.deserialize(value);
//        System.out.println(result);

//        List<GateResource> gateResources = Lists.newArrayList();
//        GateResource gateResource = new GateResource();
//        gateResource.setId("12345666");
//        gateResource.setAuthMode("2");
//        gateResource.setRemark("234556");
//        gateResource.setRegion("tesnm");
//        gateResources.add(gateResource);
//        Map<String,Object> map = JsonHelper.objToMap(gateResource);
//        System.out.println(map);
//        System.out.println(JsonHelper.toJson(map));
    }


}
