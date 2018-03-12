package com.bizvane.ishop.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {

    private static URL url;
    private static HttpURLConnection con;
    private static int state = -1;


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
        }else if(isactive.equals("Y") ||isactive.equals("是")){
            result="是";
        }else if(isactive.equals("N") || isactive.equals("否")){
            result="否";
        }else{
            result="";
        }
        return result;
    }



    /**
     * 短信通道类型
     */
    public static String ChannelType(String type){
        String result="";
        if(type==null){
            result="";
        }else if(type.equals("Marketing") ||type.equals("是")){
            result="营销短信通道";
        }else if(type.equals("Production") || type.equals("否")){
            result="生产短信通道";
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
        }else  if(run_mode.equals("register")){
            result="邀请注册";
        } else if(run_mode.equals("online_apply")){
            result="线上报名活动";
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

    /**
     * 功能：检测当前URL是否可连接或是否有效,
     * 描述：最多连接网络 5 次, 如果 5 次都不成功，视为该地址不可用
     *
     * @param urlStr 指定URL网络地址
     * @return URL
     */
    public synchronized static int isConnect(String urlStr) {
        int flg = -1;
        int counts = 0;
        if (urlStr == null || urlStr.length() <= 0) {
            return flg;
        }
        while (counts < 5) {
            try {
                url = new URL(urlStr);
                con = (HttpURLConnection) url.openConnection();
                state = con.getResponseCode();
//                System.out.println(counts + "= " + state);
                if (state == 200) {
                    flg = 0;
                }
                break;
            } catch (Exception ex) {
                counts++;
//                System.out.println("URL不可用，连接第 " + counts + " 次");
                urlStr = null;
                continue;
            }
        }
        return flg;
    }

    /**
     * 判断字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否为小数
     *
     * @param str
     * @return
     */
    public static boolean isNumeric2(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    //通过HashSet踢除重复元素
    public static List removeDuplicate(List<Object> list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * 字符串 转 unicode
     */
    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }
        return string.toString();
    }

    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream   baos   =   new ByteArrayOutputStream();
        int i=-1;
        while((i=is.read())!=-1){
            baos.write(i);
        }
        return baos.toString();
    }

    //用正则表达式判断字符串是否为数字（含负数）
    public static boolean isNumeric_V2(String str) {
        String regEx = "^-?[0-9]+$";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(str);
        if (mat.find()) {
            return true;
        } else {
            return false;
        }
    }
}
