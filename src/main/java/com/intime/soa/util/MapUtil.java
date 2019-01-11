package com.intime.soa.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intime.soa.framework.util.date.DateUtils;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.framework.validation.ValidateManager;
import com.intime.soa.framework.validation.validator.NotEmptyValidator;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by qmx on 2017/12/19.
 */
public class MapUtil {
    /**
     * 实体类对象转换成Map
     * @param obj
     * @return
     */
    public static Map<String, Object> ConvertObjToMap(Object obj) {
        Map<String, Object> reMap = new HashMap<String, Object>();
        if (obj == null)
            return null;
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                try {
                    Field f = obj.getClass().getDeclaredField(
                            fields[i].getName());
                    f.setAccessible(true);
                    Object o = f.get(obj);
                    reMap.put(fields[i].getName(), o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return reMap;
    }

    /**
     * 保留Map中的某些属性
     * @param key
     * @param mapList
     * @return
     */
    public static List<Map<String,Object>> getPartMap(String key,List<Map<String,Object>> mapList){
        if(CheckUtil.isEmpty(key)){
            return null;
        }
        List<String> keys = Arrays.asList(key.split(","));
        List<Map<String,Object>> result = Lists.newArrayList();
        for(Map<String,Object> map : mapList){
            Map<String,Object> mapTemp = Maps.newHashMap();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if(keys.contains(entry.getKey())){
                    //判断如果是Date类型，将转换为String类型
                    if(!CheckUtil.isEmpty(entry.getValue()) ){
                        Object param =entry.getValue();
                        if (param instanceof Date) {
                            Date d = (Date) param;
                            entry.setValue(DateUtils.formatDateTime(d));
                        }
                    }
                    mapTemp.put(entry.getKey(),entry.getValue());
                }
            }
            result.add(mapTemp);
        }
        return result;
    }
}
