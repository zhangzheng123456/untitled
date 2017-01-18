package com.bizvane.ishop.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.security.MessageDigest;
import java.util.*;

public class CheckUtils {
    public  static String CheckPhone(String phone){

        return "";
    }


    /**
     * MD5加密
     */
    public static String encryptMD5Hash(String s) throws Exception {
        if (s == null) {
            return null;
        }
        MessageDigest digest;
        StringBuffer hasHexString;

        digest = MessageDigest.getInstance("MD5");
        digest.update(s.getBytes(), 0, s.length());
        byte messageDigest[] = digest.digest();
        hasHexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1)
                hasHexString.append('0');
            hasHexString.append(hex);
        }
        return hasHexString.toString();
    }

    public static String toJSon(int i, String message){
        JSONObject jss = new JSONObject();
        jss.put("code", i);
        jss.put("message", message);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jss);
        return jsonArray.toString();
    }


    /**
     * 是否可用处理null，Y,N
     */
    public static String CheckIsactive(String isactive){
        String result="";
        if(isactive==null){
            result="";
        }else if(isactive.equals("Y")){
            result="是";
        }else if(isactive.equals("N")){
            result="否";
        }else{
            result="";
        }
        return result;
    }
    /**
     * 会员活动类别
     */
    public static String CheckVipActivityType(String run_mode){
        String result="";
        if(run_mode==null){
            result="";
        }else if(run_mode.equals("recruit")){
            result="招募活动";
        }else if(run_mode.equals("sales")){
            result="促销活动";
        }else if(run_mode.equals("h5")){
            result="网页活动";
        }else if(run_mode.equals("coupon")){
            result="优惠券活动";
        }else if(run_mode.equals("invite")){
            result="线下报名活动";
        }else if(run_mode.equals("festival")){
            result="节日活动";
        }
        return result;
    }

    /***
     * 比较两个list之间的元素
     * @param lhs
     * @param rhs
     * @return
     */
    public static List comparator(List lhs, List rhs){
//
        Map map1 = new HashMap();
        Map map2 = new HashMap();
        Iterator iter1 = lhs.iterator();
        Iterator iter2 = rhs.iterator();

//
        while(iter1.hasNext()){
            Object worker = iter1.next();
            map1.put(worker, worker);
        }

        while(iter2.hasNext()){
            Object dutyTime = iter2.next();
            map2.put(dutyTime, dutyTime);

        }

        List list1 = new ArrayList();
        Set set = map1.keySet();
        Iterator it1 = set.iterator();
        while(it1.hasNext()){
            String key = (String)it1.next();
            list1.add(key);
        }

        List list2 = new ArrayList();

        Set set2 = map2.keySet();
        Iterator it2 = set2.iterator();
        while(it2.hasNext()){
            String key = (String)it2.next();
            list2.add(key);
        }

        List list3 = new ArrayList();

        for(int i = 0; i < list1.size(); i++){
            if(list2.contains(list1.get(i))){
                continue;
            }else{
                list3.add(list1.get(i));
            }
        }

        return list3;
    }
    //判断字符创是否是json
    public static boolean checkJson(String json){

        // String jsonstr = "({name: 1, obj: 3,[dd]})";
        Boolean bool=false;
        try
        {
            JSONObject.parseObject(json);
            bool=true;
        }
        catch (Exception e)
        {
           bool=false;
        }
        return  bool;
    }

}
