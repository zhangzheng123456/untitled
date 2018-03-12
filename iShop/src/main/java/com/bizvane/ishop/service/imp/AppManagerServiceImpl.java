package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.AppManagerMapper;
import com.bizvane.ishop.entity.AppManager;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.AppManagerService;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by PC on 2017/2/9.
 */
@Service
public class AppManagerServiceImpl implements AppManagerService {
    @Autowired
    private AppManagerMapper appManagerMapper;
    @Autowired
    IceInterfaceService iceInterfaceService;

    @Autowired
    private UserService userService;

    @Autowired
    StoreService storeService;
    @Autowired
    MongoDBClient mongodbClient;

    @Override
    public List<AppManager> getFunctionList(String corp_az) throws Exception {
        return appManagerMapper.getFunctionList(corp_az);
    }

    @Override
    public List<AppManager> getActionList(String app_function, String corp_az, String corp_jnby) throws Exception {
        return appManagerMapper.getActionList(app_function, corp_az, corp_jnby);
    }

    //获取图表
    @Override
    public String getObtainEvents(JSONObject object) throws Exception {
        String corp_code = object.get("corp_code").toString();
        String store_code = object.get("store_code").toString();
        String user_code = object.get("user_code").toString();
        String role_code = object.get("role_code").toString();
        String app_action_code = object.get("app_action_code").toString();
        String dev_type = object.get("dev_type").toString();
        String date_type = object.get("date_type").toString();
        String date_value = object.get("date_value").toString();
        String page_number = object.get("page_number").toString();
        String page_size = object.get("page_size").toString();

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);
        Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
        Data data_app_action_code = new Data("app_action_code", app_action_code, ValueType.PARAM);
        Data data_dev_type = new Data("dev_type", dev_type, ValueType.PARAM);
        Data data_date_type = new Data("date_type", date_type, ValueType.PARAM);
        Data data_date_value = new Data("date_value", date_value, ValueType.PARAM);
        Data data_page_number = new Data("page_number", page_number, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);


        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_role_code.key, data_role_code);
        datalist.put(data_app_action_code.key, data_app_action_code);
        datalist.put(data_dev_type.key, data_dev_type);
        datalist.put(data_date_type.key, data_date_type);
        datalist.put(data_date_value.key, data_date_value);
        datalist.put(data_page_number.key, data_page_number);
        datalist.put(data_page_size.key, data_page_size);

        DataBox dataBox = iceInterfaceService.iceInterfaceV3("ObtainEvents", datalist);
        String result = dataBox.data.get("message").value;
        return result;
    }

    //获取列表
    @Override
    public String getObtainEventTable(JSONObject object) throws Exception {
        String corp_code = object.get("corp_code").toString();
        String store_code = object.get("store_code").toString();
        String user_code = object.get("user_code").toString();
        String role_code = object.get("role_code").toString();
        String app_action_code = object.get("app_action_code").toString();
        String dev_type = object.get("dev_type").toString();
        String date_type = object.get("date_type").toString();
        String date_value = object.get("date_value").toString();
        String page_number = object.get("page_number").toString();
        String page_size = object.get("page_size").toString();

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);
        Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
        Data data_app_action_code = new Data("app_action_code", app_action_code, ValueType.PARAM);
        Data data_dev_type = new Data("dev_type", dev_type, ValueType.PARAM);
        Data data_date_type = new Data("date_type", date_type, ValueType.PARAM);
        Data data_date_value = new Data("date_value", date_value, ValueType.PARAM);
        Data data_page_number = new Data("page_number", page_number, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);


        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_role_code.key, data_role_code);
        datalist.put(data_app_action_code.key, data_app_action_code);
        datalist.put(data_dev_type.key, data_dev_type);
        datalist.put(data_date_type.key, data_date_type);
        datalist.put(data_date_value.key, data_date_value);
        datalist.put(data_page_number.key, data_page_number);
        datalist.put(data_page_size.key, data_page_size);

        DataBox dataBox = iceInterfaceService.iceInterfaceV3("ObtainEventTable", datalist);
        String result = dataBox.data.get("message").value;
        return result;
    }


    //获取图表
    @Override
    public String getCommodityAttention(JSONObject object) throws Exception {
        String corp_code = object.get("corp_code").toString();
        String store_code = object.get("store_code").toString();
        String user_code = object.get("user_code").toString();
        String role_code = object.get("role_code").toString();
        String goods_type = object.get("goods_type").toString();
        String dev_type = object.get("dev_type").toString();
        String date_type = object.get("date_type").toString();
        String date_value = object.get("date_value").toString();
        String page_number = object.get("page_number").toString();
        String page_size = object.get("page_size").toString();

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);
        Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
        Data data_app_action_code = new Data("goods_type", goods_type, ValueType.PARAM);
        Data data_dev_type = new Data("dev_type", dev_type, ValueType.PARAM);
        Data data_date_type = new Data("date_type", date_type, ValueType.PARAM);
        Data data_date_value = new Data("date_value", date_value, ValueType.PARAM);
        Data data_page_number = new Data("page_number", page_number, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);


        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_role_code.key, data_role_code);
        datalist.put(data_app_action_code.key, data_app_action_code);
        datalist.put(data_dev_type.key, data_dev_type);
        datalist.put(data_date_type.key, data_date_type);
        datalist.put(data_date_value.key, data_date_value);
        datalist.put(data_page_number.key, data_page_number);
        datalist.put(data_page_size.key, data_page_size);

        DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetCommodityAttention", datalist);
        String result = dataBox.data.get("message").value;
        return result;
    }

    //获取列表
    @Override
    public String getCommodityAttentionTable(JSONObject object) throws Exception {
        String corp_code = object.get("corp_code").toString();
        String store_code = object.get("store_code").toString();
        String user_code = object.get("user_code").toString();
        String role_code = object.get("role_code").toString();
        String goods_type = object.get("goods_type").toString();
        String dev_type = object.get("dev_type").toString();
        String date_type = object.get("date_type").toString();
        String date_value = object.get("date_value").toString();
        String page_number = object.get("page_number").toString();
        String page_size = object.get("page_size").toString();

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);
        Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
        Data data_app_action_code = new Data("goods_type", goods_type, ValueType.PARAM);
        Data data_dev_type = new Data("dev_type", dev_type, ValueType.PARAM);
        Data data_date_type = new Data("date_type", date_type, ValueType.PARAM);
        Data data_date_value = new Data("date_value", date_value, ValueType.PARAM);
        Data data_page_number = new Data("page_number", page_number, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);


        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_role_code.key, data_role_code);
        datalist.put(data_app_action_code.key, data_app_action_code);
        datalist.put(data_dev_type.key, data_dev_type);
        datalist.put(data_date_type.key, data_date_type);
        datalist.put(data_date_value.key, data_date_value);
        datalist.put(data_page_number.key, data_page_number);
        datalist.put(data_page_size.key, data_page_size);

        DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetCommodityAttentionTable", datalist);
        String result = dataBox.data.get("message").value;
        return result;
    }

    @Override
    public String getCommodityAttentionByXiuDa(JSONObject jsonObject, HttpServletRequest request) throws Exception {
        int pages = 0;
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String user_id = request.getSession().getAttribute("user_id").toString();
        String area_code = "";
        String store_code = "";
        String brand_code = "";
        String user_code = "";
        String date_type = jsonObject.get("date_type").toString();
        String date_value = jsonObject.get("date_value").toString();
        int page_number = Integer.parseInt(jsonObject.get("pageNumber").toString());
        int page_size = Integer.parseInt(jsonObject.get("pageSize").toString());
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
        if (role_code.equals(Common.ROLE_CM)) {
            String manager_corp = request.getSession().getAttribute("manager_corp").toString();

            corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

            System.out.println(corp_code + "<======manager_corp=====>" + manager_corp);
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
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                            list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "", "");
                    } else {
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
                } else {
                    if (!area_code.equals("") || !brand_code.equals("")) {
                        //拉取区域下所有员工（包括区经）
                        String[] areas = area_code.split(",");
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                        if (!store_code.equals(""))
                            list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "", "");
                    } else {
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                        list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "", "");
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
                        list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "", "");
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
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
            }

        }


        //分权限.........................................................................end

        JSONArray develop_jsonArray = new JSONArray();
        com.alibaba.fastjson.JSONObject develop_object = new com.alibaba.fastjson.JSONObject();
        //mongodb查询
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_share_log);
        BasicDBList userDBList = new BasicDBList();
        for (int i = 0; i < list.size(); i++) {
            userDBList.add(list.get(i).getUser_code());
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
                    basicDBList.add(new BasicDBObject("user_id", new BasicDBObject("$in", userDBList)));
                    Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                    basicDBList.add(new BasicDBObject("modified_date", pattern));
                    basicDBObject.put("$and", basicDBList);
                    DBCursor dbCursor1 = cursor.find(basicDBObject);
                    int login_y = dbCursor1.count();
                    com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                    json_data.put("date", dates.get(i));
                    json_data.put("count", login_y);
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

                    basicDBList.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));


                    Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                    basicDBList.add(new BasicDBObject("modified_date", pattern));
                    basicDBObject.put("$and", basicDBList);
                    DBCursor dbCursor1 = cursor.find(basicDBObject);

                    int login_y = dbCursor1.count();
                    com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                    json_data.put("date", dates.get(i).substring(5, date_value.length()));
                    json_data.put("count", login_y);
                    develop_jsonArray.add(json_data);
                }
            } else if (date_type.equals("W")) {
                String[] dates = TimeUtils.getWeek2Day(date_value).split(",");
                for (int i = 0; i < dates.length; i++) {

                    BasicDBObject basicDBObject = new BasicDBObject();
                    BasicDBList basicDBList = new BasicDBList();
                    //企业编号
                    basicDBList.add(new BasicDBObject("corp_code", corp_code));
                    basicDBList.add(new BasicDBObject("user_code", new BasicDBObject("$in", userDBList)));
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

        return null;
    }

    @Override
    public String getCommodityAttentionTableByXiuDa(JSONObject jsonObject, HttpServletRequest request) throws Exception {
        int pages = 0;
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String user_id = request.getSession().getAttribute("user_id").toString();
        String area_code = "";
        String store_code = "";
        String brand_code = "";
        String user_code = "";
        String date_type = jsonObject.get("date_type").toString();
        String date_value = jsonObject.get("date_value").toString();
        int page_number = Integer.parseInt(jsonObject.get("pageNumber").toString());
        int page_size = Integer.parseInt(jsonObject.get("pageSize").toString());
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
        if (role_code.equals(Common.ROLE_CM)) {
            String manager_corp = request.getSession().getAttribute("manager_corp").toString();

            corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

            System.out.println(corp_code + "<======manager_corp=====>" + manager_corp);
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
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                            list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "", "");
                    } else {
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
                } else {
                    if (!area_code.equals("") || !brand_code.equals("")) {
                        //拉取区域下所有员工（包括区经）
                        String[] areas = area_code.split(",");
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                        if (!store_code.equals(""))
                            list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "", "");
                    } else {
                        list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
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
                        list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "", "");
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
                        list = userService.selUserByStoreCode(corp_code, "", storecodes, null, "", "");
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
                    list = userService.selUserByStoreCode(corp_code, "", store_code, null, "", "");
            }

        }
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_share_log);
        DBCursor dbCursor = null;
        BasicDBObject basicDBObject = new BasicDBObject();
        BasicDBList basicDBList = new BasicDBList();
        BasicDBList userDBList = new BasicDBList();

        for (int i = 0; i < list.size(); i++) {

            userDBList.add(list.get(i).getUser_code());
        }
        basicDBList.add(new BasicDBObject("user_id", new BasicDBObject("$in", userDBList)));
        basicDBList.add(new BasicDBObject("corp_code", corp_code));
        //日期
        if (date_value != null && !date_value.equals("")) {
            if (date_type.equals("Y")) {
                List<String> dates = TimeUtils.getYearAllDays(date_value);
                basicDBList.add(new BasicDBObject("share_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + "-01")));
                basicDBList.add(new BasicDBObject("share_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) + "-31")));
            } else if (date_type.equals("M")) {
                List<String> dates = TimeUtils.getMonthAllDays(date_value);
                basicDBList.add(new BasicDBObject("share_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + " 00:00:00")));
                basicDBList.add(new BasicDBObject("share_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) + " 23:59:59")));
            } else if (date_type.equals("W")) {
                String[] dates = TimeUtils.getWeek2Day(date_value).split(",");

                basicDBList.add(new BasicDBObject("share_date", new BasicDBObject(QueryOperators.GTE, dates[0] + " 00:00:00")));
                basicDBList.add(new BasicDBObject("share_date", new BasicDBObject(QueryOperators.LTE, dates[dates.length - 1] + " 23:59:59")));
            }
        }

        basicDBObject.put("$and", basicDBList);
        DBCursor dbCursor1 = cursor.find(basicDBObject);
        pages = MongoUtils.getPages(dbCursor, page_size);
        dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "share_date", -1);
        ArrayList arrayList = MongoUtils.dbCursorToList_id(dbCursor);
        JSONObject result = new JSONObject();
        result.put("list", arrayList);
        result.put("pages", pages);
        result.put("page_number", page_number);
        result.put("page_size", page_size);
        return result.toJSONString();
    }


}
