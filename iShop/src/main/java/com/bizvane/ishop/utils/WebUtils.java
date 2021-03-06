package com.bizvane.ishop.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

//import com.alibaba.fastjson.JSONObject;


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
                Object value = jsonObject.get(name).toString().trim();
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

    public static String El2Str(String el) {
        String str = "";
        str = el.replaceAll("\\\\", "");
        str = str.replaceAll("\\$", "");
        str = str.replaceAll("\\*", "\\\\\\\\*");
        str = str.replaceAll("\\(", "\\\\\\\\(");
        str = str.replaceAll("\\)", "\\\\\\\\)");
        str = str.replaceAll("\\[", "\\\\\\\\[");
        str = str.replaceAll("\\]", "\\\\\\\\]");
        str = str.replaceAll("\\+", "\\\\\\\\+");
        str = str.replaceAll("\\?", "\\\\\\\\?");
        return str;
    }

    public static String El2Str1(String el) {
        String str = "";
        str = el.replace("$", "");
        str = str.replace("*", "\\*");
        str = str.replace("(", "\\(");
        str = str.replace(")", "\\)");
        str = str.replace("[", "\\[");
        str = str.replace("]", "\\]");
        str = str.replace("+", "\\+");
        str = str.replace("?", "\\?");
        return str;
    }

    public static String El2Str2(String el) {
        String str = "";
        str = el.replace("$", "");
        str = str.replace("*", "\\*");
        str = str.replace("(", "\\(");
        str = str.replace(")", "\\)");
        str = str.replace("[", "\\[");
        str = str.replace("]", "\\]");
        str = str.replace("+", "\\+");
        str = str.replace("?", "\\?");
        str = str.replace(":","\\:");
        str = str.replace("{","\\{");
        str = str.replace("}","\\}");
        str = str.replace("\"","\\\"");
        return str;
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
            JSONObject json = JSONObject.parseObject(info);
            String screen_key = json.get("screen_key").toString();
            String screen_value = json.get("screen_value").toString();
            screen_value = screen_value.replaceAll("'", "");
            //    System.out.println("---------转义前---------------:"+screen_value);
            if (CheckUtils.checkJson(screen_value) == false) {
                screen_value = screen_value.replaceAll(",", "|");
                screen_value = screen_value.replaceAll("，", "|");
                screen_value = El2Str(screen_value);
                //  System.out.println("------------------特殊地段------------------------");
                //      System.out.println("---------转义后---------------:"+screen_value);
                if (screen_value.startsWith("|") || screen_value.startsWith(",") || screen_value.startsWith("，")) {
                    screen_value = screen_value.substring(1);
                }
                if (screen_value.endsWith("|") || screen_value.endsWith(",") || screen_value.endsWith("，")) {
                    screen_value = screen_value.substring(0, screen_value.length() - 1);
                }
                if(null==screen_value){
                    screen_value="";
                }
                if (!screen_value.equals("")) {
                    screen_value = screen_value.replaceAll("'", "");
                    //   System.out.println("---------再次截取后---------------:"+screen_value);
                    screen_value = "'" + screen_value + "'";
                }
                //   System.out.println("---------截取后---------------:"+screen_value);
            } else {
                //  System.out.println("-----------created_date_login---------------:"+screen_value);
                //  System.out.println("------------------不特殊地段------------------------");
            }
            map.put(screen_key, screen_value);
        }
        return map;
    }

    public static LinkedHashMap Json2ShowName(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        String jlist = jsonObject.get("tablemanager").toString();
        JSONArray array = JSONArray.parseArray(jlist);
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < array.size(); i++) {
            String info = array.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
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
        for (int i = 0; i < json.size(); i++) {
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

    public static List Json2List2(JSONArray json) {
        if (json == null) {
            return null;
        }

        List result = new ArrayList();
        for (int i = 0; i < json.size(); i++) {
            if (json.get(i) instanceof JSONArray) {
                result.add(Json2List((JSONArray) json.get(i)));
            } else {
                result.add(json.get(i));
            }
        }

        return result;
    }

    /**
     * 特殊字符替换(现在主要针对导出)
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String StringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符[`~!@#$%^&*()+=|{}':;',\[\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，？]
        String regEx = ",";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("、").trim();
    }

    /**
     * 检查某个数组中是否有重复值
     *
     * @param array
     * @return Boolean
     * @throws Exception
     */
    public static Boolean checkRepeat(String[] array) throws Exception {
        String[] array1 = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            String a = array[i];

            if (Arrays.asList(array1).contains(a)) {
                return false;
            } else {
                array1[i] = a;
            }
        }
        return true;
    }

    /**
     * 把实体bean对象转换成DBObject
     *
     * @param bean
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> DBObject bean2DBObject(T bean) throws IllegalArgumentException,
            IllegalAccessException {
        if (bean == null) {
            return null;
        }
        DBObject dbObject = new BasicDBObject();
        // 获取对象对应类中的所有属性域
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 获取属性名
            String varName = field.getName();
            // 修改访问控制权限
            boolean accessFlag = field.isAccessible();
            if (!accessFlag) {
                field.setAccessible(true);
            }
            Object param = field.get(bean);
            if (param == null) {
                continue;
            } else if (param instanceof Integer) {//判断变量的类型
                int value = ((Integer) param).intValue();
                dbObject.put(varName, value);
            } else if (param instanceof String) {
                String value = (String) param;
                dbObject.put(varName, value);
            } else if (param instanceof Double) {
                double value = ((Double) param).doubleValue();
                dbObject.put(varName, value);
            } else if (param instanceof Float) {
                float value = ((Float) param).floatValue();
                dbObject.put(varName, value);
            } else if (param instanceof Long) {
                long value = ((Long) param).longValue();
                dbObject.put(varName, value);
            } else if (param instanceof Boolean) {
                boolean value = ((Boolean) param).booleanValue();
                dbObject.put(varName, value);
            } else if (param instanceof Date) {
                Date value = (Date) param;
                dbObject.put(varName, value);
            }
            // 恢复访问控制权限
            field.setAccessible(accessFlag);
        }
        return dbObject;
    }

    /**
     * 把实体bean对象转换成JSONObject
     *
     * @param bean
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> JSONObject bean2JSONObject(T bean) throws IllegalArgumentException,
            IllegalAccessException {
        if (bean == null) {
            return null;
        }
        JSONObject dbObject = new JSONObject();
        // 获取对象对应类中的所有属性域
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 获取属性名
            String varName = field.getName();
            // 修改访问控制权限
            boolean accessFlag = field.isAccessible();
            if (!accessFlag) {
                field.setAccessible(true);
            }
            Object param = field.get(bean);
            if (param == null) {
                continue;
            } else if (param instanceof Integer) {//判断变量的类型
                int value = ((Integer) param).intValue();
                dbObject.put(varName, value);
            } else if (param instanceof String) {
                String value = (String) param;
                dbObject.put(varName, value);
            } else if (param instanceof Double) {
                double value = ((Double) param).doubleValue();
                dbObject.put(varName, value);
            } else if (param instanceof Float) {
                float value = ((Float) param).floatValue();
                dbObject.put(varName, value);
            } else if (param instanceof Long) {
                long value = ((Long) param).longValue();
                dbObject.put(varName, value);
            } else if (param instanceof Boolean) {
                boolean value = ((Boolean) param).booleanValue();
                dbObject.put(varName, value);
            } else if (param instanceof Date) {
                Date value = (Date) param;
                dbObject.put(varName, value);
            }
            // 恢复访问控制权限
            field.setAccessible(accessFlag);
        }
        return dbObject;
    }

    /**
     * 把DBObject转换成bean对象
     *
     * @param dbObject
     * @param bean
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static <T> T dbObject2Bean(DBObject dbObject, T bean) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            return null;
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            String varName = field.getName();
            Object object = dbObject.get(varName);
            if (object != null) {
                BeanUtils.setProperty(bean, varName, object);
            }
        }
        return bean;
    }

    /**
     * 流水号
     */
    private static volatile int serialNumber = 0;

    /**
     * 生成流水号
     * 从1 - 999999，不足六位，从右往左补0
     *
     * @return
     */
    public static synchronized String generateSerialNumber(int j) {
        int n = serialNumber = ++serialNumber;
        if (n == (99999 + j)) {
            serialNumber = n = j;
        }

        StringBuffer strbu = new StringBuffer(5);
        strbu.append(n);
        for (int i = 0, length = 5 - strbu.length(); i < length; i++) {
            strbu.insert(0, 0);
        }

        return strbu.toString();
    }

    public static String[] store2Ts(String store_code){
        String[] split = null;
        if (!store_code.equals("")) {
            split = store_code.split(",");
        } else {
            store_code = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
            split = store_code.split(",");
        }
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i] + ",";
        }
        return split;
    }


    public static List<DBObject> IteratorToList(Iterable<DBObject> iterable){
        List<DBObject> list = new ArrayList<DBObject>();
        Iterator<DBObject> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }


    public static JSONArray sortDesc(JSONArray jsonArray, final String key) {
        JSONArray sortedJsonArray = new JSONArray();

        JSONArray jsonArr = JSONArray.parseArray(jsonArray.toJSONString());

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private String KEY_NAME = key;

            @Override
            public int compare(JSONObject a, JSONObject b) {
                Double valA = 0d;
                Double valB = 0d;

                try {
                    valA = a.getDouble(KEY_NAME);
                    valB = b.getDouble(KEY_NAME);
                } catch (JSONException e) {
                    //do something
                }

                return valB.compareTo(valA);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray;
    }
    /**
     * 集团管理员获取企业
     */
    public static String getCorpCodeByCm(String session_corp,Object json_corp){
        String corp_cm="~!@#$^&*";
        if(null==json_corp || "".equals(String.valueOf(json_corp)) || "null".equals(String.valueOf(json_corp))){
            String[] split = session_corp.split(",");
            if(split.length>0){
                corp_cm=split[0];
            }else{
                corp_cm="~!@#$^&*";
            }
        }else{
            corp_cm=String.valueOf(json_corp);
        }
        return  corp_cm;
    }

    //获取()里的内容
    public  static  List<String> getParamByRegex(String param) {
         Pattern pattern = Pattern.compile("(\\([^\\)]+\\))");
         List<String> list=new ArrayList<String>();
         Matcher match = pattern.matcher(param);
         while (match.find()) {
             String value = match.group();
             value=value.replaceAll("\\(","").replaceAll("\\)","");
             list.add(value);
         }
         return list;
    }

    public static List<List> cutListByArray(List list,int size) throws Exception {
        int blockSize = size;
        List<List> lists = new ArrayList<List>();
        if (null != list && blockSize > 0) {
            int listSize = list.size();
            if (listSize <= blockSize) {
                lists.add(list);
                return lists;
            }
            int batchSize = listSize / blockSize;
            int remain = listSize % blockSize;
            for (int i = 0; i < batchSize; i++) {
                int fromIndex = i * blockSize;
                int toIndex = fromIndex + blockSize;
                lists.add(list.subList(fromIndex, toIndex));
            }
            if (remain > 0) {
                lists.add(list.subList(listSize - remain, listSize));
            }
        }
        return lists;
    }
    
}

