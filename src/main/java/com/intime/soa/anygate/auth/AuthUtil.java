package com.intime.soa.anygate.auth;

import com.intime.soa.framework.config.ConfigManager;
import com.intime.soa.framework.util.ValueUtil;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.util.Constants;

/**
 * Created by qmx on 2018/10/25.
 */
public class AuthUtil {

    public static String handle_url(String requestUrl,String catalog) {

        if (!requestUrl.startsWith("/soa-")) {
            return Constants.SLANT + Constants.ANYGATE_CATALOG + requestUrl;
        }
        String[] str = requestUrl.split(Constants.SLANT);

        if (!CheckUtil.isEmpty(str) && str.length >= 3) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < str.length; i++) {
                if(i==1){
                    stringBuilder.append(Constants.SLANT + catalog);
                }
                if (i > 1) {
                    if (i == 2) {
                        stringBuilder.append(Constants.SLANT);
                    }
                    String temp = str[i];
                    if (i == str.length - 1) {
                        stringBuilder.append(temp);
                    } else {
                        stringBuilder.append(temp).append(Constants.SLANT);
                    }
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }

    public static String handle_uri(String requestUri) {

        if (!requestUri.startsWith("/soa-")) {
            return ValueUtil.toString(ConfigManager.getConfig().getProperty("local.server"), "/api") + requestUri;
        }
        String[] str = requestUri.split("/");

        if (!CheckUtil.isEmpty(str) && str.length >= 3) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < str.length; i++) {
                if (i > 1) {
                    if (i == 2) {
                        stringBuilder.append(ConfigManager.getConfig().getProperty(str[1]) + "/");
                    }
                    String temp = str[i];
                    if (i == str.length - 1) {
                        stringBuilder.append(temp);
                    } else {
                        stringBuilder.append(temp).append("/");
                    }
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }
}
