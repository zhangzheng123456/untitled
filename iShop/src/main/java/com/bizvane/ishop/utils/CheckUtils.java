package com.bizvane.ishop.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.security.MessageDigest;

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
    
}
