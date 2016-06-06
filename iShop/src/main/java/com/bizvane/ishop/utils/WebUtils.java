package com.bizvane.ishop.utils;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.StoreAchvGoal;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.collections.iterators.ObjectArrayIterator;
import org.json.JSONObject;

/**
 * Created by lixiang on 2016/6/3.
 *
 * @@version
 */
public class WebUtils {

    private static SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    public static String getValueForSession(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "";
        }
        String value = session.getAttribute(key).toString();
        return value;
    }

    public static <T> T request2Bean(HttpServletRequest request,
                                     Class<T> beanClass) {
        try {
            T bean = beanClass.newInstance();
            Map map = request.getParameterMap();
            ConvertUtils.register(new Converter() {

                @Override
                public Object convert(Class type, Object value) {
                    if (value == null) {
                        return null;
                    }
                    String str = (String) value;
                    if (str.trim().equals("")) {
                        return null;
                    }
                    //       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    try {

                        //   return df.parse(str);
                        return sdf.parse(str);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }, Date.class);
            BeanUtils.populate(bean, map);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T JSON2Bean(JSONObject jsonObject, Class<T> beanClass) {
        try {
            T bean = beanClass.newInstance();
            Map<String, Object> map = new HashMap<String, Object>();
            Iterator<String> names = jsonObject.keySet().iterator();
            while (names.hasNext()) {
                String name = names.next();
                if (name == null) {
                    continue;
                }
                Object value = jsonObject.get(name);
                map.put(name, value);
            }
            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class type, Object value) {
                    if(value==null){
                        return null;
                    }
                    String str=(String)value;
                    if(str.trim().equals("")){
                        return null;
                    }
                    try{
                        sdf.parse(str);
                    }catch (Exception ex){
                        throw new RuntimeException(ex);
                    }
                    return null;
                }
            },Date.class);
            BeanUtils.populate(bean, map);
            return bean;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

