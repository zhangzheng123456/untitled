package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by yanyadong on 2017/2/28.
 */
@Controller
@RequestMapping("/VipDevelop")
public class VipDevelopController {

    @Autowired
    MongoDBClient mongodbClient;

    @Autowired
    StoreService storeService;

    @Autowired
    private UserService userService;
    @Autowired
    private BaseService baseService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public String getVipDevelopList(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            // String user_code = request.getSession().getAttribute("user_code").toString();
            String user_id = request.getSession().getAttribute("user_id").toString();

            String jsString = request.getParameter("param");
            JSONObject jsObject = JSONObject.parseObject(jsString);
            String id = jsObject.get("id").toString();
            String message = jsObject.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String date_type = jsonObject.get("date_type").toString();
            String date_value = jsonObject.get("date_value").toString();
            int page_number = Integer.parseInt(jsonObject.get("pageNumber").toString());
            int page_size = Integer.parseInt(jsonObject.get("pageSize").toString());

            String area_code = "";
            String store_code = "";
            String brand_code = "";
            String user_code = "";
            List<User> list = new ArrayList<User>();
            if (jsonObject.containsKey("brand_code") && !jsonObject.get("brand_code").equals("")) {
                brand_code = jsonObject.get("brand_code").toString();
            }
            if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                area_code = jsonObject.get("area_code").toString();
            }
            if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                store_code = jsonObject.get("store_code").toString();
            }
            if (jsonObject.containsKey("user_code") && !jsonObject.get("user_code").equals("")) {
                user_code = jsonObject.get("user_code").toString();
            }
            if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();

                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
            }
            //分权限...............................................................
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_CM)) {
//                if (jsonObject.containsKey("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
//                    corp_code = jsonObject.get("corp_code").toString();
//                }
                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {

                    if (!store_code.equals("")) {
//                    String[] areas = area_code.split(",");
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                    } else {
                        if (!area_code.equals("") || !brand_code.equals("")) {
                            //拉取区域下所有员工（包括区经）
                            String[] areas = null;
                            if (!area_code.equals("")) {
                                areas = area_code.split(",");
                            }
                            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                            for (int i = 0; i < stores.size(); i++) {
                                store_code = store_code + stores.get(i).getStore_code() + ",";
                            }
                          //  System.out.println("-------store_code-----------"+store_code);
                            if (!store_code.equals(""))
                                list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "","");
                        } else {
                            list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                        }
                    }
                }

            } else if (role_code.equals(Common.ROLE_GM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {
                    if (!store_code.equals("")) {
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                    } else {
                        if (!area_code.equals("") || !brand_code.equals("")) {
                            //拉取区域下所有员工（包括区经）
                            String[] areas = area_code.split(",");
                            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                            for (int i = 0; i < stores.size(); i++) {
                                store_code = store_code + stores.get(i).getStore_code() + ",";
                            }
                            if (!store_code.equals(""))
                                list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "","");
                        } else {
                            list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                        }
                    }
                }
            } else if (role_code.equals(Common.ROLE_STAFF)) {
                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {
                    User user = userService.getUserById(user_id);
                    //  List<User> users = new ArrayList<User>();
                    list.add(user);
                    // list = new PageInfo<User>();
                    // list.add(users);
                }
            } else if (role_code.equals(Common.ROLE_SM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {

                    if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                        store_code = jsonObject.get("store_code").toString();
                    } else {
                        store_code = request.getSession().getAttribute("store_code").toString();
                        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
//                if (page_number == 1 && (searchValue.equals("") || user_name.contains(searchValue))) {
//                    List<User> users = list.getList();
//                    User self = userService.getUserById(user_id);
//                    users.add(self);
//                }
                }
            } else if (role_code.equals(Common.ROLE_AM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {

                    if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                        store_code = jsonObject.get("store_code").toString();
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                    } else {
                        if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                            area_code = jsonObject.get("area_code").toString();
                            String storecodes = "";
                            if (!area_code.equals("")) {
                                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                                String[] areas = area_code.split(",");
                                List<Store> store = null;
                                store = storeService.selectByAreaBrand(corp_code, areas, null, null, "");
                                //...
                                for (int i = 0; i < store.size(); i++) {
                                    //      store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                                    storecodes += store.get(i).getStore_code().toString();
                                }
                            }
                            list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "","");
                        } else {
                            area_code = request.getSession().getAttribute("area_code").toString();
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            store_code = request.getSession().getAttribute("store_code").toString();
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            String storecodes = "";
                            if (!area_code.equals("")) {
                                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                                String[] areas = area_code.split(",");
                                String[] storeCodes = null;
                                if (!store_code.equals("")) {
                                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                                    storeCodes = store_code.split(",");
                                }

                                List<Store> store = null;
                                store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");
                                //...
                                for (int i = 0; i < store.size(); i++) {
                                    //      store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                                    storecodes += store.get(i).getStore_code().toString();
                                }
                            }
                            list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "","");
                        }
                    }
                }

            } else if (role_code.equals(Common.ROLE_BM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }

                } else {
                    if (jsonObject.containsKey("brand_code") && !jsonObject.get("brand_code").equals("")) {
                        brand_code = jsonObject.get("brand_code").toString();
                    } else {
                        brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                        area_code = jsonObject.get("area_code").toString();
                    } else {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                        store_code = jsonObject.get("store_code").toString();
                    } else {
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                    }
                    if (!store_code.equals(""))
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                }
            }

            //   System.out.println("list>>>>>>>"+list.size());
            //分权限.........................................................................end

            //mongodb查询
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
            DBCursor dbCursor = null;
            BasicDBObject basicDBObject = new BasicDBObject();
            BasicDBList basicDBList = new BasicDBList();
            BasicDBList userDBList = new BasicDBList();

            for (int i = 0; i < list.size(); i++) {

                userDBList.add(list.get(i).getUser_code());
            }
            BasicDBList storeDBList = new BasicDBList();
            String storeByScreen = baseService.getStoreByScreen(jsonObject, request,corp_code);
            String[] split = storeByScreen.split(",");
            for (int i = 0; i < split.length; i++) {
                storeDBList.add(split[i]);
            }
            if(jsonObject.containsKey("user_code") && jsonObject.getString("user_code").equals("")){
                BasicDBObject basic = new BasicDBObject();
                BasicDBList value2 = new BasicDBList();
                value2.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
                value2.add(new BasicDBObject("store_code", new BasicDBObject("$in", storeDBList)));
                basic.put("$or", value2);
                basicDBList.add(basic);
            }else{
                basicDBList.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
            }
            //企业编号
            basicDBList.add(new BasicDBObject("corp_code", corp_code));
            //userCode


            //日期
            if (date_value != null && !date_value.equals("")) {
                if (date_type.equals("Y")) {
                    List<String> dates = TimeUtils.getYearAllDays(date_value);
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + "-01")));
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) + "-31")));
                } else if (date_type.equals("M")) {
                    List<String> dates = TimeUtils.getMonthAllDays(date_value);
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1)+" 23:59:59")));
                } else if (date_type.equals("W")) {
                    String[] dates = TimeUtils.getWeek2Day(date_value).split(",");

                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates[0]+" 00:00:00")));
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates[dates.length - 1]+" 23:59:59")));
                }
            }

            basicDBObject.put("$and", basicDBList);
            DBCursor dbCursor1 = cursor.find(basicDBObject);
            pages = MongoUtils.getPages(dbCursor1, page_size);
            dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "modified_date", -1);

            ArrayList<HashMap<String, String>> list1 = MongoHelperServiceImpl.dbCursorToList_develop(dbCursor);

            ArrayList<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < list1.size(); i++) {
                HashMap<String, String> map = list1.get(i);
                String code = map.get("store_code").toString();
                List<Store> stores = storeService.selectStore(corp_code, code);

                String store_name = "";
                for (int j = 0; j < stores.size(); j++) {
                    String storeName = stores.get(j).getStore_name();
                    if (storeName == null || storeName.equals("")) {
                        storeName = "";
                    }
                    store_name += storeName + ",";
                }
                if (store_name.endsWith(",")) {
                    store_name = store_name.substring(0, store_name.length() - 1);
                }
                map.put("store_name", store_name);
                list2.add(map);
            }

            result.put("list", list2);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(e.getMessage());
        }


        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/view", method = RequestMethod.POST)
    @ResponseBody
    public String getVipDevelopView(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        JSONArray develop_jsonArray = new JSONArray();
        com.alibaba.fastjson.JSONObject develop_object = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_id = request.getSession().getAttribute("user_id").toString();

            String jsString = request.getParameter("param");
            JSONObject jsObject = JSONObject.parseObject(jsString);
            String id = jsObject.get("id").toString();
            String message = jsObject.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            String date_type = jsonObject.get("date_type").toString();
            String date_value = jsonObject.get("date_value").toString();

            String area_code = "";
            String store_code = "";
            String brand_code = "";
            String user_code = "";
            List<User> list = new ArrayList<User>();
            if (jsonObject.containsKey("brand_code") && !jsonObject.get("brand_code").equals("")) {
                brand_code = jsonObject.get("brand_code").toString();
            }
            if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                area_code = jsonObject.get("area_code").toString();
            }
            if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                store_code = jsonObject.get("store_code").toString();
            }
            if (jsonObject.containsKey("user_code") && !jsonObject.get("user_code").equals("")) {
                user_code = jsonObject.get("user_code").toString();
            }
            if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

//                if(jsonObject.containsKey("corp_code") && !String.valueOf(jsonObject.get("corp_code")).equals("")){
//                    corp_code=jsonObject.get("corp_code").toString();
//                }
//                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
            }
            //分权限...............................................................
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_CM)) {
//                if (jsonObject.containsKey("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
//                    corp_code = jsonObject.get("corp_code").toString();
//                }
                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {

                    if (!store_code.equals("")) {
//                    String[] areas = area_code.split(",");
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                    } else {
                        if (!area_code.equals("") || !brand_code.equals("")) {
                            //拉取区域下所有员工（包括区经）
                            String[] areas = null;
                            if (!area_code.equals("")) {
                                areas = area_code.split(",");
                            }
                            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                            for (int i = 0; i < stores.size(); i++) {
                                store_code = store_code + stores.get(i).getStore_code() + ",";
                            }
                            if (!store_code.equals(""))
                                list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "","");
                        } else {
                            list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                        }
                    }
                }

            } else if (role_code.equals(Common.ROLE_GM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {
                    if (!store_code.equals("")) {
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                    } else {
                        if (!area_code.equals("") || !brand_code.equals("")) {
                            //拉取区域下所有员工（包括区经）
                            String[] areas = area_code.split(",");
                            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                            for (int i = 0; i < stores.size(); i++) {
                                store_code = store_code + stores.get(i).getStore_code() + ",";
                            }
                            if (!store_code.equals(""))
                                list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "","");
                        } else {
                            list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                        }
                    }
                }
            } else if (role_code.equals(Common.ROLE_STAFF)) {
                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {
                    User user = userService.getUserById(user_id);
                    list.add(user);
                }
            } else if (role_code.equals(Common.ROLE_SM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {

                    if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                        store_code = jsonObject.get("store_code").toString();
                    } else {
                        store_code = request.getSession().getAttribute("store_code").toString();
                        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                }
            } else if (role_code.equals(Common.ROLE_AM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {

                    if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                        store_code = jsonObject.get("store_code").toString();
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                    } else {
                        if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                            area_code = jsonObject.get("area_code").toString();
                            String storecodes = "";
                            if (!area_code.equals("")) {
                                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                                String[] areas = area_code.split(",");
                                List<Store> store = null;
                                store = storeService.selectByAreaBrand(corp_code, areas, null, null, "");
                                for (int i = 0; i < store.size(); i++) {
                                    storecodes += store.get(i).getStore_code().toString();
                                }
                            }
                            list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "","");
                        } else {
                            area_code = request.getSession().getAttribute("area_code").toString();
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            store_code = request.getSession().getAttribute("store_code").toString();
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            String storecodes = "";
                            if (!area_code.equals("")) {
                                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                                String[] areas = area_code.split(",");
                                String[] storeCodes = null;
                                if (!store_code.equals("")) {
                                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                                    storeCodes = store_code.split(",");
                                }

                                List<Store> store = null;
                                store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");
                                for (int i = 0; i < store.size(); i++) {
                                    storecodes += store.get(i).getStore_code().toString();
                                }
                            }
                            list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "","");
                        }
                    }
                }

            } else if (role_code.equals(Common.ROLE_BM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }

                } else {
                    if (jsonObject.containsKey("brand_code") && !jsonObject.get("brand_code").equals("")) {
                        brand_code = jsonObject.get("brand_code").toString();
                    } else {
                        brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                        area_code = jsonObject.get("area_code").toString();
                    } else {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                        store_code = jsonObject.get("store_code").toString();
                    } else {
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                    }
                    if (!store_code.equals(""))
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                }
            }

            //分权限.........................................................................end


            //mongodb查询
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
            BasicDBList userDBList = new BasicDBList();
            for (int i = 0; i < list.size(); i++) {
                userDBList.add(list.get(i).getUser_code());
            }
            BasicDBList storeDBList = new BasicDBList();
            String storeByScreen = baseService.getStoreByScreen(jsonObject, request,corp_code);
            String[] split = storeByScreen.split(",");
            for (int i = 0; i < split.length; i++) {
                storeDBList.add(split[i]);
            }

            //日期
            if (date_value != null && !date_value.equals("")) {
                if (date_type.equals("Y")) {
                    List<String> dates = TimeUtils.getYearAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {

                        BasicDBObject basicDBObject = new BasicDBObject();
                        BasicDBList basicDBList = new BasicDBList();
                        //企业编号
                        basicDBList.add(new BasicDBObject("corp_code", corp_code));
                        //userCode
                        if(jsonObject.containsKey("user_code") && jsonObject.getString("user_code").equals("")){
                            BasicDBObject basic = new BasicDBObject();
                            BasicDBList value2 = new BasicDBList();
                            value2.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
                            value2.add(new BasicDBObject("store_code", new BasicDBObject("$in", storeDBList)));
                            basic.put("$or", value2);
                            basicDBList.add(basic);
                        }else{
                            basicDBList.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
                        }

                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        basicDBList.add(new BasicDBObject("modified_date", pattern));
                        basicDBObject.put("$and", basicDBList);
                        DBCursor dbCursor1 = cursor.find(basicDBObject);
                        int login_y = dbCursor1.count();
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i));
                        json_data.put("vip_count", login_y);
                        develop_jsonArray.add(json_data);
                    }

                } else if (date_type.equals("M")) {
                    List<String> dates = TimeUtils.getMonthAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {
                        BasicDBObject basicDBObject = new BasicDBObject();
                        BasicDBList basicDBList = new BasicDBList();
                        //企业编号
                        basicDBList.add(new BasicDBObject("corp_code", corp_code));
                        //userCode
                        if(jsonObject.containsKey("user_code") && jsonObject.getString("user_code").equals("")){
                            BasicDBObject basic = new BasicDBObject();
                            BasicDBList value2 = new BasicDBList();
                            value2.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
                            value2.add(new BasicDBObject("store_code", new BasicDBObject("$in", storeDBList)));
                            basic.put("$or", value2);
                            basicDBList.add(basic);
                        }else{
                            basicDBList.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
                        }

                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        basicDBList.add(new BasicDBObject("modified_date", pattern));
                        basicDBObject.put("$and", basicDBList);
                        DBCursor dbCursor1 = cursor.find(basicDBObject);

                        int login_y = dbCursor1.count();
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i).substring(5, date_value.length()));
                        json_data.put("vip_count", login_y);
                        develop_jsonArray.add(json_data);
                    }
                } else if (date_type.equals("W")) {
                    String[] dates = TimeUtils.getWeek2Day(date_value).split(",");
                    for (int i = 0; i < dates.length; i++) {

                        BasicDBObject basicDBObject = new BasicDBObject();
                        BasicDBList basicDBList = new BasicDBList();
                        //企业编号
                        basicDBList.add(new BasicDBObject("corp_code", corp_code));
                        //userCode
                        if(jsonObject.containsKey("user_code") && jsonObject.getString("user_code").equals("")){
                            BasicDBObject basic = new BasicDBObject();
                            BasicDBList value2 = new BasicDBList();
                            value2.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
                            value2.add(new BasicDBObject("store_code", new BasicDBObject("$in", storeDBList)));
                            basic.put("$or", value2);
                            basicDBList.add(basic);
                        }else{
                            basicDBList.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
                        }

                        Pattern pattern = Pattern.compile("^.*" + dates[i] + ".*$", Pattern.CASE_INSENSITIVE);
                        basicDBList.add(new BasicDBObject("modified_date", pattern));
                        basicDBObject.put("$and", basicDBList);
                        DBCursor dbCursor1 = cursor.find(basicDBObject);
                        int login_y = dbCursor1.count();
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates[i]);
                        json_data.put("vip_count", login_y);
                        develop_jsonArray.add(json_data);
                    }
                }
                develop_object.put("vip_develop", develop_jsonArray);
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(develop_object.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(e.getMessage());
        }


        return dataBean.getJsonStr();
    }

    /**
     * 品牌关注度分析
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String getexportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        //  int pages=0;
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            // String user_code = request.getSession().getAttribute("user_code").toString();
            String user_id = request.getSession().getAttribute("user_id").toString();

            String jsString = request.getParameter("param");
            JSONObject jsObject = JSONObject.parseObject(jsString);
            String id = jsObject.get("id").toString();
            String message = jsObject.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String date_type = jsonObject.get("date_type").toString();
            String date_value = jsonObject.get("date_value").toString();
//            int page_number = Integer.parseInt(jsonObject.get("pageNumber").toString());
//            int page_size = Integer.parseInt(jsonObject.get("pageSize").toString());

            String area_code = "";
            String store_code = "";
            String brand_code = "";
            String user_code = "";
            List<User> list = new ArrayList<User>();
            if (jsonObject.containsKey("brand_code") && !jsonObject.get("brand_code").equals("")) {
                brand_code = jsonObject.get("brand_code").toString();
            }
            if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                area_code = jsonObject.get("area_code").toString();
            }
            if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                store_code = jsonObject.get("store_code").toString();
            }
            if (jsonObject.containsKey("user_code") && !jsonObject.get("user_code").equals("")) {
                user_code = jsonObject.get("user_code").toString();
            }
            if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();

                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
            }
            //分权限...............................................................
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_CM)) {
//                if (jsonObject.containsKey("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
//                    corp_code = jsonObject.get("corp_code").toString();
//                }
                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {

                    if (!store_code.equals("")) {
//                    String[] areas = area_code.split(",");
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                    } else {
                        if (!area_code.equals("") || !brand_code.equals("")) {
                            //拉取区域下所有员工（包括区经）
                            String[] areas = null;
                            if (!area_code.equals("")) {
                                areas = area_code.split(",");
                            }
                            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                            for (int i = 0; i < stores.size(); i++) {
                                store_code = store_code + stores.get(i).getStore_code() + ",";
                            }
                            //  System.out.println("-------store_code-----------"+store_code);
                            if (!store_code.equals(""))
                                list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "","");
                        } else {
                            list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                        }
                    }
                }

            } else if (role_code.equals(Common.ROLE_GM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {
                    if (!store_code.equals("")) {
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                    } else {
                        if (!area_code.equals("") || !brand_code.equals("")) {
                            //拉取区域下所有员工（包括区经）
                            String[] areas = area_code.split(",");
                            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                            for (int i = 0; i < stores.size(); i++) {
                                store_code = store_code + stores.get(i).getStore_code() + ",";
                            }
                            if (!store_code.equals(""))
                                list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "","");
                        } else {
                            list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                        }
                    }
                }
            } else if (role_code.equals(Common.ROLE_STAFF)) {
                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {
                    User user = userService.getUserById(user_id);
                    //  List<User> users = new ArrayList<User>();
                    list.add(user);
                    // list = new PageInfo<User>();
                    // list.add(users);
                }
            } else if (role_code.equals(Common.ROLE_SM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {

                    if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                        store_code = jsonObject.get("store_code").toString();
                    } else {
                        store_code = request.getSession().getAttribute("store_code").toString();
                        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
//                if (page_number == 1 && (searchValue.equals("") || user_name.contains(searchValue))) {
//                    List<User> users = list.getList();
//                    User self = userService.getUserById(user_id);
//                    users.add(self);
//                }
                }
            } else if (role_code.equals(Common.ROLE_AM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }
                } else {

                    if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                        store_code = jsonObject.get("store_code").toString();
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                    } else {
                        if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                            area_code = jsonObject.get("area_code").toString();
                            String storecodes = "";
                            if (!area_code.equals("")) {
                                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                                String[] areas = area_code.split(",");
                                List<Store> store = null;
                                store = storeService.selectByAreaBrand(corp_code, areas, null, null, "");
                                //...
                                for (int i = 0; i < store.size(); i++) {
                                    //      store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                                    storecodes += store.get(i).getStore_code().toString();
                                }
                            }
                            list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "","");
                        } else {
                            area_code = request.getSession().getAttribute("area_code").toString();
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            store_code = request.getSession().getAttribute("store_code").toString();
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            String storecodes = "";
                            if (!area_code.equals("")) {
                                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                                String[] areas = area_code.split(",");
                                String[] storeCodes = null;
                                if (!store_code.equals("")) {
                                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                                    storeCodes = store_code.split(",");
                                }

                                List<Store> store = null;
                                store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");
                                //...
                                for (int i = 0; i < store.size(); i++) {
                                    //      store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                                    storecodes += store.get(i).getStore_code().toString();
                                }
                            }
                            list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "","");
                        }
                    }
                }

            } else if (role_code.equals(Common.ROLE_BM)) {

                if (user_code != null && !user_code.equals("")) {
                    String[] user_codes = user_code.split(",");
                    for (int i = 0; i < user_codes.length; i++) {
                        User user = new User();
                        user.setCorp_code(corp_code);
                        user.setUser_code(user_code);
                        list.add(user);
                    }

                } else {
                    if (jsonObject.containsKey("brand_code") && !jsonObject.get("brand_code").equals("")) {
                        brand_code = jsonObject.get("brand_code").toString();
                    } else {
                        brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                        area_code = jsonObject.get("area_code").toString();
                    } else {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                        store_code = jsonObject.get("store_code").toString();
                    } else {
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                    }
                    if (!store_code.equals(""))
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                }
            }

            //   System.out.println("list>>>>>>>"+list.size());
            //分权限.........................................................................end

            //mongodb查询
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
            DBCursor dbCursor = null;
            BasicDBObject basicDBObject = new BasicDBObject();
            BasicDBList basicDBList = new BasicDBList();
            BasicDBList userDBList = new BasicDBList();

            for (int i = 0; i < list.size(); i++) {

                userDBList.add(list.get(i).getUser_code());
            }
            BasicDBList storeDBList = new BasicDBList();
            String storeByScreen = baseService.getStoreByScreen(jsonObject, request,corp_code);
            String[] split = storeByScreen.split(",");
            for (int i = 0; i < split.length; i++) {
                storeDBList.add(split[i]);
            }
            if(jsonObject.containsKey("user_code") && jsonObject.getString("user_code").equals("")){
                BasicDBObject basic = new BasicDBObject();
                BasicDBList value2 = new BasicDBList();
                value2.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
                value2.add(new BasicDBObject("store_code", new BasicDBObject("$in", storeDBList)));
                basic.put("$or", value2);
                basicDBList.add(basic);
            }else{
                basicDBList.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
            }
            //企业编号
            basicDBList.add(new BasicDBObject("corp_code", corp_code));
            //userCode


            //日期
            if (date_value != null && !date_value.equals("")) {
                if (date_type.equals("Y")) {
                    List<String> dates = TimeUtils.getYearAllDays(date_value);
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + "-01")));
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) + "-31")));
                } else if (date_type.equals("M")) {
                    List<String> dates = TimeUtils.getMonthAllDays(date_value);
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1)+" 23:59:59")));
                } else if (date_type.equals("W")) {
                    String[] dates = TimeUtils.getWeek2Day(date_value).split(",");

                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates[0]+" 00:00:00")));
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates[dates.length - 1]+" 23:59:59")));
                }
            }

            basicDBObject.put("$and", basicDBList);
            DBCursor dbCursor1 = cursor.find(basicDBObject);
//            pages = MongoUtils.getPages(dbCursor1, page_size);
//            dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "modified_date", -1);

            ArrayList<HashMap<String, String>> list1 = MongoHelperServiceImpl.dbCursorToList_develop(dbCursor1);

            ArrayList<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < list1.size(); i++) {
                HashMap<String, String> map = list1.get(i);
                String code = map.get("store_code").toString();
                List<Store> stores = storeService.selectStore(corp_code, code);

                String store_name = "";
                for (int j = 0; j < stores.size(); j++) {
                    String storeName = stores.get(j).getStore_name();
                    if (storeName == null || storeName.equals("")) {
                        storeName = "";
                    }
                    store_name += storeName + ",";
                }
                if (store_name.endsWith(",")) {
                    store_name = store_name.substring(0, store_name.length() - 1);
                }
                map.put("store_name", store_name);
                list2.add(map);
            }
            //导出......
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(list2);
            if (list2.size() >=50000) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }

            //导出字段
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("date", "时间");
            map.put("open_id", "openID");
            map.put("nick_name", "昵称");
            map.put("head_img", "头像");
            map.put("user_code", "员工编号");
            map.put("user_name", "员工名称");
            map.put("phone", "导购手机");
            map.put("store_name", "店铺");
            map.put("app_name", "公众号");

            String pathname = OutExeclHelper.OutExecl(json, list2, map, response, request, "品牌关注度分析");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
            dataBean.setMessage(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();

    }


}
