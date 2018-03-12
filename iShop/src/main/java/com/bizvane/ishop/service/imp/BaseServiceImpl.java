package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.BaseMapper;
import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by yin on 2016/7/14.
 */
@Service
public class BaseServiceImpl implements BaseService {
    @Autowired
    private BaseMapper baseMapper;
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CorpMapper corpMapper;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    StoreService storeService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    @Override
    public PageInfo<HashMap<String, Object>> queryMetaList(int page_number, int page_size, Map<String, Object> params) throws SQLException {
        List<HashMap<String, Object>> list;
        PageHelper.startPage(page_number, page_size);
        list = baseMapper.queryMetaList(params);
        PageInfo<HashMap<String, Object>> page = new PageInfo<HashMap<String, Object>>(list);
        return page;
    }

    /**
     * store_id转store_code
     *
     * @param corp_code
     * @param store_id
     * @return store_code
     * @throws Exception
     */
    public String storeIdConvertStoreCode(String corp_code, String store_id) throws Exception {
        String store_code = "";
        Store store = storeMapper.selStoreByStroeId(corp_code, store_id, Common.IS_ACTIVE_Y);
        if (store != null)
            store_code = store.getStore_code();
        return store_code;
    }

    /**
     * store_code转store_id
     *
     * @param corp_code
     * @param store_code
     * @return store_id
     * @throws Exception
     */
    public String storeCodeConvertStoreId(String corp_code, String store_code) throws Exception {
        String store_id = "";
        Store store = storeService.getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
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
//    public String userIdConvertUserCode(String corp_code,String user_id)throws Exception{
//        String user_code = "";
//        List<User> user = userMapper.selUserByUserId(user_id,corp_code,Common.IS_ACTIVE_Y);
//        if (user.size()>0)
//            user_code = user.get(0).getUser_code();
//        return user_code;
//    }
//
//    /**
//     * user_code转user_id
//     * @param corp_code
//     * @param user_code
//     * @return user_id
//     * @throws Exception
//     */
//    public String userCodeConvertUserId(String corp_code,String user_code)throws Exception{
//        String user_id = "";
//        List<User> user = userMapper.selectUserCode(user_code,corp_code,Common.IS_ACTIVE_Y);
//        if (user.size()>0)
//            user_id = user.get(0).getUser_id();
//        return user_id;
//    }

    /**
     * mongodb插入用户操作记录
     *
     * @param operation_corp_code 操作者corp_code
     * @param operation_user_code 操作者user_code
     * @param function            功能
     * @param action              动作
     * @param corp_code           被操作corp_code
     * @param code                被操作code
     * @param name                被操作name
     * @throws Exception
     */
    public void insertUserOperation(String operation_corp_code, String operation_user_code, String function, String action, String corp_code, String code, String name, String remark) throws Exception {
        Date now = new Date();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_log_user_operation);
        DBObject saveData = new BasicDBObject();
        saveData.put("function", function);
        saveData.put("action", action);
        saveData.put("corp_code", corp_code);
        saveData.put("code", code);
        saveData.put("name", name);
        saveData.put("operation_corp_code", operation_corp_code);
        if (operation_user_code.contains("&&")) {
            System.out.println("=========" + operation_user_code);
            String[] operators = operation_user_code.split("&&");
            operation_user_code = operators[0];
            String operation_user_name = "";
            if (operators.length > 1)
                operation_user_name = operators[1];
            saveData.put("operation_user_name", operation_user_name);
        }
        saveData.put("operation_user_code", operation_user_code);
        saveData.put("remark", remark);
        Corp corp = selectByCorpcode(corp_code);
        if (corp != null) {
            saveData.put("corp_name", corp.getCorp_name());
        } else {
            saveData.put("corp_name", "");
        }
        saveData.put("operation_time", Common.DATETIME_FORMAT.format(now));
        collection.insert(saveData);
    }

    @Override
    public Corp selectByCorpcode(String corp_code) throws SQLException {
        return corpMapper.selectByCorpcode(corp_code);
    }

    public String getStoreByScreen(JSONObject jsonObject, HttpServletRequest request) throws Exception {
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

//        String page_num = jsonObject.get("pageNumber").toString();
//        String page_size = jsonObject.get("pageSize").toString();
//        String vip_group_code = "";
//        if (jsonObject.containsKey("vip_group_code")){
//            vip_group_code = jsonObject.get("vip_group_code").toString();
//        }
        //String user_id = user_code;
        String area_code = "";
        String store_id = "";
        if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_CM)) {
            corp_code = jsonObject.get("corp_code").toString();
            store_id = jsonObject.get("store_code").toString().trim();
            if (store_id.equals("")) {
                area_code = jsonObject.get("area_code").toString().trim();
                String brand_code = jsonObject.get("brand_code").toString().trim();
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        } else if (role_code.equals(Common.ROLE_AM)) {
            store_id = jsonObject.get("store_code").toString().trim();
            area_code = jsonObject.get("area_code").toString().trim();
            String brand_code = jsonObject.get("brand_code").toString().trim();
            String area_store_code = "";
            if (store_id.equals("")) {
                if (area_code.equals("")) {
                    area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    area_store_code = request.getSession().getAttribute("store_code").toString();
                    area_store_code = area_store_code.replace(Common.SPECIAL_HEAD, "");
                }
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", area_store_code);
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        } else {
            store_id = jsonObject.get("store_code").toString().trim();
            if (store_id.equals("")) {
                area_code = jsonObject.get("area_code").toString().trim();
                String brand_code = jsonObject.get("brand_code").toString().trim();
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        }
        String store_code_json = jsonObject.get("store_code").toString().trim();
        String area_code_json = jsonObject.get("area_code").toString().trim();
        String brand_code_json = jsonObject.get("brand_code").toString().trim();

        if (store_code_json.equals("") && area_code_json.equals("") && brand_code_json.equals("")) {
            String[] split = store_id.split(",");
            if (split.length > 5) {
                store_id = split[0] + "," + split[1] + "," + split[2] + "," + split[3];
            }
        }
        return store_id;
    }

    /**
     * 员工管理-功能使用分析
     * 筛选（大于5个取前四个）
     * @param jsonObject
     * @param request
     * @param corp_code
     * @return
     * @throws Exception
     */
    public String getStoreByScreen(JSONObject jsonObject, HttpServletRequest request, String corp_code) throws Exception {
        String user_code = request.getSession().getAttribute("user_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

//        String page_num = jsonObject.get("pageNumber").toString();
//        String page_size = jsonObject.get("pageSize").toString();
//        String vip_group_code = "";
//        if (jsonObject.containsKey("vip_group_code")){
//            vip_group_code = jsonObject.get("vip_group_code").toString();
//        }
        //String user_id = user_code;
        String area_code = "";
        String store_id = "";
        if (role_code.equals(Common.ROLE_SYS)) {
            corp_code = jsonObject.get("corp_code").toString();
            store_id = jsonObject.get("store_code").toString().trim();
            if (store_id.equals("")) {
                area_code = jsonObject.get("area_code").toString().trim();
                String brand_code = jsonObject.get("brand_code").toString().trim();
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        } else if (role_code.equals(Common.ROLE_CM)) {
            store_id = jsonObject.get("store_code").toString().trim();
            if (store_id.equals("")) {
                area_code = jsonObject.get("area_code").toString().trim();
                String brand_code = jsonObject.get("brand_code").toString().trim();
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        } else if (role_code.equals(Common.ROLE_AM)) {
            store_id = jsonObject.get("store_code").toString().trim();
            area_code = jsonObject.get("area_code").toString().trim();
            String brand_code = jsonObject.get("brand_code").toString().trim();
            String area_store_code = "";
            if (store_id.equals("")) {
                if (area_code.equals("")) {
                    area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    area_store_code = request.getSession().getAttribute("store_code").toString();
                    area_store_code = area_store_code.replace(Common.SPECIAL_HEAD, "");
                }
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", area_store_code);
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        } else {
            store_id = jsonObject.get("store_code").toString().trim();
            if (store_id.equals("")) {
                area_code = jsonObject.get("area_code").toString().trim();
                String brand_code = jsonObject.get("brand_code").toString().trim();
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        }
        String store_code_json = jsonObject.get("store_code").toString().trim();
        String area_code_json = jsonObject.get("area_code").toString().trim();
        String brand_code_json = jsonObject.get("brand_code").toString().trim();

        if (store_code_json.equals("") && area_code_json.equals("") && brand_code_json.equals("")) {
            String[] split = store_id.split(",");
            if (split.length > 5) {
                store_id = split[0] + "," + split[1] + "," + split[2] + "," + split[3];
            }
        }
        return store_id;
    }

    /**
     * 任务列表详情筛选
     * @param jsonObject
     * @param request
     * @param corp_code
     * @return
     * @throws Exception
     */
    public String getStoresByScreen(JSONObject jsonObject, HttpServletRequest request, String corp_code) throws Exception {
        String user_code = request.getSession().getAttribute("user_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String area_code = "";
        String store_id = "";
        if (role_code.equals(Common.ROLE_SYS)) {
            corp_code = jsonObject.get("corp_code").toString();
            store_id = jsonObject.get("store_codes").toString().trim();
            if (store_id.equals("")) {
                area_code = jsonObject.get("area_codes").toString().trim();
                String brand_code = jsonObject.get("brand_codes").toString().trim();
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        } else if (role_code.equals(Common.ROLE_CM)) {
            store_id = jsonObject.get("store_codes").toString().trim();
            if (store_id.equals("")) {
                area_code = jsonObject.get("area_codes").toString().trim();
                String brand_code = jsonObject.get("brand_codes").toString().trim();
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        } else if (role_code.equals(Common.ROLE_AM)) {
            store_id = jsonObject.get("store_codes").toString().trim();
            area_code = jsonObject.get("area_codes").toString().trim();
            String brand_code = jsonObject.get("brand_codes").toString().trim();
            String area_store_code = "";
            if (store_id.equals("")) {
                if (area_code.equals("")) {
                    area_code = request.getSession().getAttribute("area_codes").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    area_store_code = request.getSession().getAttribute("store_codes").toString();
                    area_store_code = area_store_code.replace(Common.SPECIAL_HEAD, "");
                }
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", area_store_code);
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        } else {
            store_id = jsonObject.get("store_codes").toString().trim();
            if (store_id.equals("")) {
                area_code = jsonObject.get("area_codes").toString().trim();
                String brand_code = jsonObject.get("brand_codes").toString().trim();
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        }
        String store_code_json = jsonObject.get("store_codes").toString().trim();
        String area_code_json = jsonObject.get("area_codes").toString().trim();
        String brand_code_json = jsonObject.get("brand_codes").toString().trim();

        if (store_code_json.equals("") && area_code_json.equals("") && brand_code_json.equals("")) {
            store_id="";
        }
        if(!store_code_json.equals("")&&(!store_code_json.contains(","))){

            store_id=store_code_json+",";
        }
        return store_id;
    }


    //查找员工所属品牌
    public List<Brand> getBrandByUser(HttpServletRequest request, String corp_code_new) throws Exception {
        List<Brand> brandList = new ArrayList<Brand>();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String[] codes = null;
        if (role_code.equals(Common.ROLE_SYS) && !corp_code_new.equals("")) {
            brandList = brandService.getActiveBrand(corp_code, "", codes);
        } else if (role_code.equals(Common.ROLE_GM)) {
            brandList = brandService.getActiveBrand(corp_code, "", codes);

        } else if (role_code.equals(Common.ROLE_BM)) {
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
            codes = brand_code.split(",");
            brandList = brandService.getActiveBrand(corp_code, "", codes);

        } else if (role_code.equals(Common.ROLE_AM)) {
            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
            String[] areas = area_code.split(",");
            String[] stores = null;
            if (!store_code.equals("")) {
                store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                stores = store_code.split(",");
            }
            List<Store> storeList = storeService.selectByAreaBrand(corp_code, areas, stores, null, Common.IS_ACTIVE_Y);
            String brand_code1 = "";
            for (int i = 0; i < storeList.size(); i++) {
                String brand_code = storeList.get(i).getBrand_code();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                if (!brand_code.endsWith(",")) {
                    brand_code = brand_code + ",";
                }
                brand_code1 = brand_code1 + brand_code;
                codes = brand_code1.split(",");
                brandList = brandService.getActiveBrand(corp_code, "", codes);
            }
        } else if (role_code.equals(Common.ROLE_STAFF) || role_code.equals(Common.ROLE_SM)) {
            List<Store> stores = storeService.selectByStoreCodes(store_code, corp_code, Common.IS_ACTIVE_Y);
            String brand_code1 = "";
            for (int i = 0; i < stores.size(); i++) {
                String brand_code = stores.get(i).getBrand_code();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                if (!brand_code.endsWith(",")) {
                    brand_code = brand_code + ",";
                }
                brand_code1 = brand_code1 + brand_code;
            }
            if (!brand_code1.equals("")) {
                codes = brand_code1.split(",");
                brandList = brandService.getActiveBrand(corp_code, "", codes);
            }

        }

        return brandList;
    }


    //查找员工所属品牌
    public List<Brand> getBrandByUserForApp(String corp_code_app, String brand_code_app, String area_code_app, String store_code_app, String role_code_app) throws Exception {
        List<Brand> brandList = new ArrayList<Brand>();
        String[] codes = null;
        if (role_code_app.equals(Common.ROLE_GM)) {
            brandList = brandService.getActiveBrand(corp_code_app, "", codes);

        } else if (role_code_app.equals(Common.ROLE_BM)) {

            brand_code_app = brand_code_app.replace(Common.SPECIAL_HEAD, "");
            codes = brand_code_app.split(",");
            brandList = brandService.getActiveBrand(corp_code_app, "", codes);

        } else if (role_code_app.equals(Common.ROLE_AM)) {
            area_code_app = area_code_app.replace(Common.SPECIAL_HEAD, "");
            String[] areas = area_code_app.split(",");
            String[] stores = null;
            if (!store_code_app.equals("")) {
                store_code_app = store_code_app.replace(Common.SPECIAL_HEAD, "");
                stores = store_code_app.split(",");
            }
            List<Store> storeList = storeService.selectByAreaBrand(corp_code_app, areas, stores, null, Common.IS_ACTIVE_Y);
            String brand_code1 = "";
            for (int i = 0; i < storeList.size(); i++) {
                String brand_code = storeList.get(i).getBrand_code();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                if (!brand_code.endsWith(",")) {
                    brand_code = brand_code + ",";
                }
                brand_code1 = brand_code1 + brand_code;
                codes = brand_code1.split(",");
                brandList = brandService.getActiveBrand(corp_code_app, "", codes);
            }
        } else if (role_code_app.equals(Common.ROLE_STAFF) || role_code_app.equals(Common.ROLE_SM)) {
            List<Store> stores = storeService.selectByStoreCodes(store_code_app, corp_code_app, Common.IS_ACTIVE_Y);
            String brand_code1 = "";
            for (int i = 0; i < stores.size(); i++) {
                String brand_code = stores.get(i).getBrand_code();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                if (!brand_code.endsWith(",")) {
                    brand_code = brand_code + ",";
                }
                brand_code1 = brand_code1 + brand_code;
            }
            if (!brand_code1.equals("")) {
                codes = brand_code1.split(",");
                brandList = brandService.getActiveBrand(corp_code_app, "", codes);
            }

        }

        return brandList;
    }

    public String getUserByScreen(JSONObject jsonObject, HttpServletRequest request, String corp_code) throws Exception {
        String role_code = request.getSession().getAttribute("role_code").toString();
        String group_code = request.getSession().getAttribute("group_code").toString();
        String user_id = request.getSession().getAttribute("user_id").toString();
        PageInfo<User> list = new PageInfo<User>();
        String user_code_new="";
        try {
            String area_code = "";
            String store_code = "";
            String brand_code = "";

            list.setList(new ArrayList<User>());
            if (jsonObject.containsKey("brand_code") && !jsonObject.get("brand_code").equals("")) {
                brand_code = jsonObject.get("brand_code").toString();
            }
            if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                area_code = jsonObject.get("area_code").toString();
            }
            if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                store_code = jsonObject.get("store_code").toString();
            }
            if (role_code.equals(Common.ROLE_SYS)) {
                if (jsonObject.containsKey("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
                    corp_code = jsonObject.get("corp_code").toString();
                }
            }
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
                if (role_code.equals(Common.ROLE_CM)) {
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                }
                if (!store_code.equals("")) {
//                    String[] areas = area_code.split(",");
                    list = userService.selUserByStoreCode(1, 5000, corp_code, "", store_code, null, Common.ROLE_AM);
                } else {
                    if (!area_code.equals("") || !brand_code.equals("")) {
                        //拉取区域下所有员工（包括区经）
                        String[] areas = null;
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                        if (stores.size() > 0) {
                            for (int i = 0; i < stores.size(); i++) {
                                store_code = store_code + stores.get(i).getStore_code() + ",";
                            }
                            list = userService.selectUsersByRole(1, 5000, corp_code, "", store_code, area_code, null, "");
                        }
                    } else {
                        list = userService.selectUsersByRole(1, 5000, corp_code, "", store_code, area_code, null, "");
                    }
                }
            } else if (role_code.equals(Common.ROLE_STAFF)) {
                User user = userService.getUserById(user_id);
                List<User> users = new ArrayList<User>();
                users.add(user);
                list = new PageInfo<User>();
                list.setList(users);
            } else if (role_code.equals(Common.ROLE_SM)) {
                Group group = groupService.selectByCode(corp_code, group_code, Common.IS_ACTIVE_Y);
                if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                    store_code = jsonObject.get("store_code").toString();
                } else {
                    store_code = request.getSession().getAttribute("store_code").toString();
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                }
                if (group.getIsshow() != null && group.getIsshow().equals("N")) {
                    list = userService.selectUsersByRole(1, 5000, corp_code, "", store_code, "", null, Common.ROLE_AM);
                } else {
                    list = userService.selectUsersByRole(1, 5000, corp_code, "", store_code, "", null, role_code);
                    List<User> users = list.getList();
                    User self = userService.getUserById(user_id);
                    users.add(self);
                }
            } else if (role_code.equals(Common.ROLE_AM)) {
                if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").equals("")) {
                    store_code = jsonObject.get("store_code").toString();
                    list = userService.selectUsersByRole(1, 5000, corp_code, "", store_code, "", null, role_code);
                } else {
                    if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").equals("")) {
                        area_code = jsonObject.get("area_code").toString();
                    } else {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        store_code = request.getSession().getAttribute("store_code").toString();
                        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    list = userService.selectUsersByRole(1, 5000, corp_code, "", store_code, area_code, null, role_code);

                    List<User> users = list.getList();
                    User self = userService.getUserById(user_id);
                    users.add(self);

                }
            } else if (role_code.equals(Common.ROLE_BM)) {
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
                    list = userService.selectUsersByRole(1, 5000, corp_code, "", store_code, "", null, role_code);
            }

            for (User user:list.getList()) {
                user_code_new+=user.getUser_code()+",";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user_code_new;
    }
}
