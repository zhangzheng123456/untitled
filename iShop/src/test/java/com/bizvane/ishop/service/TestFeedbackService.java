package com.bizvane.ishop.service;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.lang.System;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yin on 2016/6/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class TestFeedbackService {
    @Autowired
    private FeedbackService feedbackService = null;
    @Autowired
    private AppversionService appversionService = null;
    @Autowired
    private InterfaceService interfaceService = null;
    @Autowired
    private ValidateCodeService validateCodeService = null;
    @Autowired
    private GroupService groupService=null;
    @Autowired
    private TaskService taskService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;
    //成功
    @Test
    public void testselectAllFeedback() {
        try {
            String stroes="1,2";
            PageInfo<User> pageInfo = userService.selectBySearchPart(1, 100, "C10001","",stroes, "", Common.ROLE_STAFF);
            List<User> list = pageInfo.getList();
            for (User store:list) {
                System.out.println(store.getStore_name()+"---"+store.getUser_name());
            }
        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    //成功
    @Test
    public void testAddFeedback() {
        try {
            Pattern pattern1 = Pattern.compile("A\\d{4}");
            Matcher matcher = pattern1.matcher("A0000");
            if (matcher.matches() == false) {
                System.out.println("----------");
            }else{
                System.out.println("=======");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //成功
    @Test
    public void testSelFeedback() {
        try {
            Feedback feedback = feedbackService.selFeedbackById(1);
            System.out.println(feedback.getPhone() + "----------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //成功
    @Test
    public void testUpdate() {
        try {
            Feedback fb = new Feedback();
            fb.setUser_code("呵呵哒");
            fb.setId(1);
            int i = feedbackService.updFeedbackById(fb);
            System.out.println(i + "-----upd----");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //成功
    @Test
    public void testDel() {
        String str="p";
        if (!str.toString().equals("D") &&
                !str.toString().equals("W") &&
                !str.toString().equals("M") &&
               !str.toString().equals("Y")) {
            System.out.println("00000000000");
        }else {
            System.out.println("1111111111");
        }
    }

    //成功
    @Test
    public void testAppList() {
        try {
            List<Appversion> appversions = appversionService.selectAllAppversion();
            System.out.println(appversions.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //成功
    @Test
    public void testAppUpd() {
        try {
            Appversion appversion = new Appversion();
            appversion.setId(1);
            appversion.setModified_date("2017/1/2");
            int i = appversionService.updAppversionById(appversion);
            System.out.println(i + "----upd--");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //成功
    @Test
    public void testfaceAdd() {
        Interfacers interfacers = new Interfacers();
        interfacers.setModified_date("2010/2/4");
        interfacers.setModifier("1");
        interfacers.setCreater("1");
        interfacers.setCreated_date("2010/2/3");
        interfacers.setCorp_code("222");
        interfacers.setIsactive("1");
        interfacers.setVersion("安卓4.7");
        int i = 0;
        try {
            i = interfaceService.addInterface(interfacers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(i + "-----faceAdd-----");
    }

    //成功
    @Test
    public void TestfaceList() {
        try {
            Interfacers interfacers = interfaceService.selInterfaceById(1);
            System.out.println(interfacers.getVersion());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testfaceUpd() throws SQLException {
        Interfacers interfacers = new Interfacers();
        interfacers.setVersion("IOS 10");
        interfacers.setId(1);
        int i = interfaceService.updInterfaceById(interfacers);
        System.out.println(i + "----faceUpd-------");
    }

    @Test
    public void testVcodeSelAll() throws Exception {
        try {
//            HttpServletRequest request = null;
//            HttpServletResponse response = null;
            PageInfo<ValidateCode> validateCodePageInfo = validateCodeService.selectAllValidateCode(1, 4, "");
            List<ValidateCode> list = validateCodePageInfo.getList();
            //这个相当于前台传过来的字段
            String[] cols = {"id", "phone"};
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("list", list);
            org.json.JSONArray array = jsonObject.getJSONArray("list");
            //String[][] str2=new String[cols.length][list.size()];
            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < list.size(); i++) {
                List<String> temp = new ArrayList<String>();
                for (int j = 0; j < cols.length; j++) {
                    String b = array.getJSONObject(i).get(cols[j]).toString();
                    temp.add(b);
                }
                lists.add(temp);
            }
            for(int i=0;i<cols.length;i++){
                System.out.print("=="+cols[i]+"==");
            }
            System.out.println();
            for (List<String> m : lists) {
                String yy = m.toString();
                String[] split = yy.split(",");
                for (int i=0;i<split.length;i++){
                    System.out.println(split[i].substring(0,1));
                    System.out.print(split[i]);
                }
                System.out.println();
            }



            // System.out.println(aa+"----");
            //     System.out.println(jsonObject.toString() + "--json--");
//            Map<String,Object> map= (Map<String, Object>) jsonObject;
//            for (Map.Entry entry : map.entrySet()) {
//                System.out.println(entry.getKey()+"=="+entry.getValue());
//            }
//            Iterator<String> keys = jsonObject.keys();
//            while (keys.hasNext()){
//                String key = keys.next().toString();
//                org.json.JSONArray m=jsonObject.getJSONArray(key);
//                System.out.println("===");
//            }
//
//            List<Map<String, Object>> olist = new ArrayList<Map<String, Object>>();
//            for (ValidateCode code : list) {
//
//                Map<String, Object> mapresult = new HashMap<String, Object>();
//                mapresult.put(list2.get(0), code.getId());
//                mapresult.put(list2.get(1), code.getPhone());
//                olist.add(mapresult);
//            }
//
//
//
//            List<ValidateCode> newList = validateCodePageInfo.getList();
//            for(int i=0;i< newList.size();i++)
//            {
//                String id1= list2.get(i);
//                if((id1== null) ||("".equals(id1)))
//                {
//                    continue;
//                }
//                for(int j=0;j<list2.size();j++)
//                {
//                    ValidateCode u = newList.get(j);
//                    if(u==null)
//                    {
//                        continue;
//                    }
//                    if(id1.equals())
//                    {
//                        tName( tName((匹配))+"");
//                        d(u);
//                    }
//                }
//            }
//            System.out.println(olist.toString());
//            response.setContentType("application/vnd.ms-excel");
//            //设置响应的字符集
//            response.setCharacterEncoding("utf-8");
//            //1  在servlet上获得out对象：
//            PrintWriter out = response.getWriter();
//            //out.print("zoukx is a good man");
//            //2  打印标签
//            out.print("<table>");
//            out.print("<tr>");
//            out.print("<td>");
//            out.print("id");
//            out.print("</td>");
//            out.print("<td>");
//            out.print("phone");
//            out.print("</td>");
//            out.print("</tr>");
//            for(Map<String,Object> m:olist){
//                for (String k:m.keySet()) {
//                    out.print("<tr>");
//                    out.print("<td>");
//                    out.print(k+":"+m.get(k));
//                    out.print("</td>");
//                    out.print("<td>");
//                    out.print("</tr>");
//                }
//            }
//            out.print("</table>");
//            out.flush();
//            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void Screen() throws SQLException {
        try {
            Map<String,String> map=new HashMap<String, String>();
            map.put("corp_name","罗莱");
            map.put("group_code","G0114");
            PageInfo<Group> allGroupScreen = groupService.getAllGroupScreen(1, 10, "C10141", "", map);
            List<Group> list = allGroupScreen.getList();
            for (Group g : list) {
                System.out.println(g.getGroup_name()+"--"+g.getCorp().getCorp_name());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
