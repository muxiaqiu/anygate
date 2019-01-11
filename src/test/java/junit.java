import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intime.soa.anygate.mapper.GateResourceMapper;
import com.intime.soa.anygate.mapper.GateUserRoleMapper;
import com.intime.soa.framework.auth.Cryptogram;
import com.intime.soa.framework.auth.TokenConstants;
import com.intime.soa.framework.result.ApiResultWrapper;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.json.JsonHelper;
import com.intime.soa.framework.util.string.StringUtils;
import com.intime.soa.model.anygate.GateResource;
import com.intime.soa.model.anygate.GateUser;
import com.intime.soa.model.anygate.ResourceRole;
import com.intime.soa.service.anygate.GateUserRoleService;
import com.intime.soa.service.anygate.GateUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by qmx on 2018/8/20.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
        ({"/spring/spring-datasource-dev.xml","classpath:/spring/spring-framework.xml","/spring/spring-config.xml","classpath*:/spring/spring-dubbo-*.xml"})
public class junit {

    @Autowired
    private GateResourceMapper gateResourceService;

    @Autowired
    private GateUserRoleMapper gateUserRoleMapper;

    @Autowired
    private GateUserService gateUserService;

    @Autowired
    private GateUserRoleService gateUserRoleService;

    @Test
    public void testAdd() throws Exception {
        Map<String, Object> map = Maps.newHashMap();
        map.put("projectId", "f93376545da945fa9abc630f2558bcf9");
        map.put("region", "tesn");
        List<GateResource> gateResources = gateResourceService.query(map);
        List<Map<String, Object>> gateResourcemap = Lists.newArrayList();
        for (int i = 0; i < gateResources.size(); i++) {
            gateResourcemap.add(JsonHelper.objToMap(gateResources.get(i)));
        }
        System.out.println(gateResourcemap);
    }


    @Test
    public void testAdd1() throws Exception {
        /*String userId = "000096c596df45d6b38da48bbe9c0658";
        List<Map<String, Object>> resourceList = gateUserRoleMapper.getResourceListByUserId(userId);
        Map<String, Object> map = resourceList.get(0);
        System.out.println(JsonHelper.toJson(map));*/

        GateUser user = parseToken("null");
        System.out.println(JsonHelper.toJson(user));
    }

    private GateUser parseToken(String token) {
        //验证token
        String decryptResult = Cryptogram.DESDecrypt(token, TokenConstants.TOKEN_KEY, TokenConstants.COMMON_IV);
        if (StringUtils.isBlank(decryptResult)) {
           /* LogCollectManager.error("Log <> token：" + token + " 解析失败！", logger);*/
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

    @Test
    public void testResourceUser() {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("mobile","13466656467");
        paramMap.put("projectId","981adf122d7643aa86324142471410e2");
        if (StringUtils.isEmpty(StringUtils.safeToString(paramMap.get("mobile"))) || StringUtils.isEmpty(StringUtils.safeToString(paramMap.get("projectId")))) {
//            new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("用户手机号或项目名称不能为空！").toResponse();
            System.out.println("用户手机号或项目名称不能为空！");
            return;
        }

        Map<String, Object> result = Maps.newHashMap();
        Map<String, Object> userParam = Maps.newConcurrentMap();
        userParam.put("mobile", StringUtils.safeToString(paramMap.get("mobile")));
        List<GateUser> gateuser = gateUserService.query(userParam);
        if (gateuser != null && gateuser.size() > 0) {
            List<ResourceRole> resources = gateUserRoleService.getResourceRoleByUserId(gateuser.get(0).getId(), StringUtils.safeToString(paramMap.get("projectId")));
            result.put("resources", resources);
            result.put("userName", gateuser.get(0).getName());
//            new ApiResultWrapper(HttpStatus.OK).setObj(result).toResponse();
            System.out.println(JsonHelper.toJson(result));
            return;
        }
//        new ApiResultWrapper().setStatus(HttpStatus.BAD_REQUEST).setMessage("没有该用户，请重试！").toResponse();
        System.out.println("没有该用户，请重试！");
    }
}
