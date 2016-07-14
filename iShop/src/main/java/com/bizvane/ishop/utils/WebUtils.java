package com.bizvane.ishop.utils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bizvane.ishop.constant.Common;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by lixiang on 2016/6/3.
 *
 * @@version
 */
public class WebUtils {

    /**
     * 获取session 中的Attribute的值，通过key
     *
     * @param request : 浏览器请求
     * @param key     ： 所要获取内容的key值
     * @return
     */
    public static String getValueForSession(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "";
        }
        String value = session.getAttribute(key).toString();
        return value;
    }

    /**
     * 将reqeust 中包含的信息转化为实体类
     *
     * @param request   ： 浏览器请求头
     * @param beanClass ： 所要转换的实体类
     * @param <T>       ： 所要转化的实体类类型
     * @return
     */
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
                    try {
                        return Common.DATETIME_FORMAT.parse(str);
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


    /**
     * 将JSONObject 中的值转化为相应的实体类
     *
     * @param jsonObject ： jsonobject 对象
     * @param beanClass  ： 实体类
     * @param <T>        ： 实体类类型
     * @return
     */
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
                Object value = jsonObject.get(name).toString();
                map.put(name, value);
            }
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
                    try {
                        return Common.DATETIME_FORMAT.parse(str);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }, Date.class);
            BeanUtils.populate(bean, map);
            return bean;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean isDecimal(String orginal) {
        return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal);
    }

    private static boolean isMatch(String regex, String orginal) {
        if (orginal == null || orginal.trim().equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(orginal);
        return isNum.matches();
    }


    public static Map Json2Map(JSONObject json)
    {
        if(json == null)
        {
            return null;
        }

        Map result = new HashMap();
        Object key, value;
        Iterator keyIterator = json.keys();
        while(keyIterator.hasNext())
        {
            key = keyIterator.next();
            value = json.get(key.toString());

            if(value instanceof JSONObject)
            {
                result.put(key, Json2Map((JSONObject)value));
            }
            else if(value instanceof JSONArray)
            {
                result.put(key, Json2List((JSONArray)value));
            }
            else
            {
                result.put(key, value);
            }
        }

        return result;
    }
    public static List Json2List(JSONArray json)
    {
        if(json == null)
        {
            return null;
        }

        List result = new ArrayList();
        for(int i=0; i<json.length(); i++)
        {
            if(json.get(i) instanceof JSONObject)
            {
                result.add(Json2Map((JSONObject)json.get(i)));
            }
            else if(json.get(i) instanceof JSONArray)
            {
                result.add(Json2List((JSONArray)json.get(i)));
            }
            else
            {
                result.add(json.get(i));
            }
        }

        return result;
    }


}

