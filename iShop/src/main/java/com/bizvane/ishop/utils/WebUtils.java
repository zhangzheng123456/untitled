package com.bizvane.ishop.utils;

import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lixiang on 2016/6/3.
 *
 * @@version
 */
public class WebUtils {

    @Autowired
    static StoreMapper storeMapper;
    @Autowired
    static UserMapper userMapper;
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


    public static Map Json2Map(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        String jlist = jsonObject.get("list").toString();
        com.alibaba.fastjson.JSONArray array = com.alibaba.fastjson.JSONArray.parseArray(jlist);
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < array.size(); i++) {
            String info = array.get(i).toString();
            JSONObject json = new JSONObject(info);
            String screen_key = json.get("screen_key").toString();
            String screen_value = json.get("screen_value").toString();
            map.put(screen_key, screen_value);
        }
//        Map result = new HashMap();
//        Object key, value;
//        Iterator keyIterator = jsonObject.keys();
//        while (keyIterator.hasNext()) {
//            key = keyIterator.next();
//            value = jsonObject.get(key.toString());
//
//            if (value instanceof JSONObject) {
//                result.put(key, Json2Map((JSONObject) value));
//            } else if (value instanceof JSONArray) {
//                result.put(key, Json2List((JSONArray) value));
//            } else {
//                result.put(key, value);
//            }
//        }
        return map;
    }

    public static LinkedHashMap Json2ShowName(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        String jlist = jsonObject.get("tablemanager").toString();
        com.alibaba.fastjson.JSONArray array = com.alibaba.fastjson.JSONArray.parseArray(jlist);
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < array.size(); i++) {
            String info = array.get(i).toString();
            JSONObject json = new JSONObject(info);
            String screen_key = json.get("column_name").toString();
            String screen_value = json.get("show_name").toString();
            map.put(screen_key, screen_value);
        }
        return map;
    }

    public static List Json2List(JSONArray json) {
        if (json == null) {
            return null;
        }

        List result = new ArrayList();
        for (int i = 0; i < json.length(); i++) {
            if (json.get(i) instanceof JSONObject) {
                result.add(Json2Map((JSONObject) json.get(i)));
            } else if (json.get(i) instanceof JSONArray) {
                result.add(Json2List((JSONArray) json.get(i)));
            } else {
                result.add(json.get(i));
            }
        }

        return result;
    }


    /**
     * 特殊字符替换(现在主要针对导出)
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public  static  String StringFilter(String   str)  throws PatternSyntaxException {
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，？]";
        Pattern   p   =   Pattern.compile(regEx);
        Matcher   m   =   p.matcher(str);
        return   m.replaceAll("、").trim();
    }

    /**
     * 检查某个数组中是否有重复值
     * @param array
     * @return Boolean
     * @throws Exception
     */
    public static Boolean checkRepeat(String[] array) throws Exception{
        String[] array1=new String[array.length];
        for (int i = 0; i < array.length; i++) {
            String a = array[i];

            if (Arrays.asList(array1).contains(a)){
                return false;
            }else {
                array1[i] = a;
            }
        }
        return true;
    }

    /**
     * store_id转store_code
     * @param corp_code
     * @param store_id
     * @return store_code
     * @throws Exception
     */
    public static String storeIdConvertStoreCode(String corp_code,String store_id)throws Exception{
        String store_code = "";
        Store store = storeMapper.selStoreByStroeId(corp_code,store_id,Common.IS_ACTIVE_Y);
        if (store != null)
            store_code = store.getStore_code();
        return store_code;
    }

    /**
     * store_code转store_id
     * @param corp_code
     * @param store_code
     * @return store_id
     * @throws Exception
     */
    public static String storeCodeConvertStoreId(String corp_code,String store_code)throws Exception{
        String store_id = "";
        Store store = storeMapper.selectByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
        if (store != null)
            store_id = store.getStore_id();
        return store_id;
    }

    /**
     * user_id转user_code
     * @param corp_code
     * @param user_id
     * @return user_code
     * @throws Exception
     */
    public static String userIdConvertUserCode(String corp_code,String user_id)throws Exception{
        String user_code = "";
        List<User> user = userMapper.selUserByUserId(user_id,corp_code,Common.IS_ACTIVE_Y);
        if (user.size()>0)
            user_code = user.get(0).getUser_code();
        return user_code;
    }

    /**
     * user_code转user_id
     * @param corp_code
     * @param user_code
     * @return user_id
     * @throws Exception
     */
    public static String userCodeConvertUserId(String corp_code,String user_code)throws Exception{
        String user_id = "";
        List<User> user = userMapper.selectUserCode(user_code,corp_code,Common.IS_ACTIVE_Y);
        if (user.size()>0)
            user_id = user.get(0).getUser_id();
        return user_id;
    }
}

