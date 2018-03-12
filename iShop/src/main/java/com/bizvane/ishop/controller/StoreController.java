
package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.*;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.System;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouying on 2016-04-20.
 */

/**
 * 店铺管理
 */

@Controller
@RequestMapping("/shop")
public class StoreController {

    private static final Logger logger = Logger.getLogger(StoreController.class);


    String id;

    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private BaseService baseService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    GroupService groupService;

    /***
     * 根据区域品牌拉店铺
     */
    @RequestMapping(value = "/selectByAreaCode", method = RequestMethod.POST)
    @ResponseBody
    public String selectByAreaCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String find_type = "";
            if (jsonObject.containsKey("find_type")) {
                find_type = jsonObject.get("find_type").toString();
            }
            String searchValue = "";
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            String area_code = "";
            String brand_code = "";
            String city = "";
            if (jsonObject.containsKey("searchValue")) {
                searchValue = jsonObject.get("searchValue").toString();
            }
            if (jsonObject.containsKey("area_code")) {
                area_code = jsonObject.get("area_code").toString();
            }
            if (jsonObject.containsKey("brand_code")) {
                brand_code = jsonObject.get("brand_code").toString();
            }
            if (jsonObject.containsKey("city")) {
                city = jsonObject.get("city").toString();
            }
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                if (jsonObject.containsKey("corp_code")) {
                    corp_code = jsonObject.get("corp_code").toString();
                } else {
                    corp_code = "C10000";
                }

                list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, "", city, find_type);
                // list = storeService.getAllStore(request, page_number, page_size, "", searchValue);
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
//                if (jsonObject.containsKey("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
//                    corp_code = jsonObject.get("corp_code").toString();
//                } else {
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
//                }
                list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, "", city, find_type);

//                if(corp_code.equals("C10000")) {
//                    list = storeService.selStoreByAreaBrandCity(page_number, page_size, "", area_code, brand_code, searchValue, "", city, find_type, manager_corp);
//                }else{
//                    list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, "", city, find_type);
//                }
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, "", city, find_type);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    if (brand_code.equals("")) {
                        brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    if (area_code.equals("")) {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, "", city, find_type);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_store_code = "";
                    if (area_code.equals("")) {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        area_store_code = request.getSession().getAttribute("store_code").toString();
                    }
                    list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, area_store_code, city, find_type);
                } else {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = storeService.selStoreByStoreCodes(page_number, page_size, store_code, corp_code, searchValue);
                }
            }
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /***
     * 根据区域拉店铺(包含全部)
     */
    @RequestMapping(value = "/findByAreaCode", method = RequestMethod.POST)
    @ResponseBody
    public String findByAreaCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String searchValue = jsonObject.get("searchValue").toString();
            PageInfo<Store> list = null;
            String brand_code = "";
            if (jsonObject.containsKey("brand_code")) {
                brand_code = jsonObject.get("brand_code").toString();
            }
            if (role_code.equals(Common.ROLE_SYS) && page_number == 1) {
                //系统管理员
                String area_code = jsonObject.get("area_code").toString();
                if (jsonObject.containsKey("corp_code"))
                    corp_code = jsonObject.get("corp_code").toString();
                list = storeService.selStoreByAreaBrandCode(page_number, page_size, corp_code, area_code, brand_code, searchValue, "");
                List<Store> stores = new ArrayList<Store>();
                Store store = new Store();
                store.setStore_code("");
                store.setStore_name("全部");
                store.setArea_code("");
                store.setArea_name("");
                store.setCorp_code("");
                store.setCorp_name("");
                store.setId(0);
                stores.add(0, store);
                stores.addAll(list.getList());
                list.setList(stores);
                // list = storeService.getAllStore(request, page_number, page_size, "", searchValue);
            } else if (!role_code.equals(Common.ROLE_SYS) && page_number == 1) {
                if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
                    String area_code = jsonObject.get("area_code").toString();
                    list = storeService.selStoreByAreaBrandCode(page_number, page_size, corp_code, area_code, brand_code, searchValue, "");
                    List<Store> stores = new ArrayList<Store>();
                    Store store = new Store();
                    store.setStore_code("");
                    store.setStore_name("全部");
                    store.setArea_code("");
                    store.setArea_name("");
                    store.setCorp_code("");
                    store.setCorp_name("");
                    store.setId(0);
                    stores.add(0, store);
                    stores.addAll(list.getList());
                    list.setList(stores);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = jsonObject.get("area_code").toString();
                    String area_store_code = "";
                    if (area_code.equals("")) {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        area_store_code = request.getSession().getAttribute("store_code").toString();
                    }
                    list = storeService.selStoreByAreaBrandCode(page_number, page_size, corp_code, area_code, brand_code, searchValue, area_store_code);
                    List<Store> stores = new ArrayList<Store>();
                    Store store = new Store();
                    store.setStore_code("");
                    store.setStore_name("全部");
                    store.setArea_code("");
                    store.setArea_name("");
                    store.setCorp_code("");
                    store.setCorp_name("");
                    store.setId(0);
                    stores.add(0, store);
                    stores.addAll(list.getList());
                    list.setList(stores);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    brand_code = jsonObject.get("brand_code").toString();
                    String area_code = jsonObject.get("area_code").toString();
                    if (brand_code.equals("")) {
                        brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    if (area_code.equals("")) {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    list = storeService.selStoreByAreaBrandCode(page_number, page_size, corp_code, area_code, brand_code, searchValue, "");
                    List<Store> stores = new ArrayList<Store>();
                    Store store = new Store();
                    store.setStore_code("");
                    store.setStore_name("全部");
                    store.setArea_code("");
                    store.setArea_name("");
                    store.setCorp_code("");
                    store.setCorp_name("");
                    store.setId(0);
                    stores.add(0, store);
                    stores.addAll(list.getList());
                    list.setList(stores);
                } else {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = storeService.selStoreByStoreCodes(page_number, page_size, store_code, corp_code, searchValue);
                }
            } else if (role_code.equals(Common.ROLE_SYS) && page_number != 1) {
                String area_code = jsonObject.get("area_code").toString();
                if (jsonObject.containsKey("corp_code"))
                    corp_code = jsonObject.get("corp_code").toString();
                list = storeService.selStoreByAreaBrandCode(page_number, page_size, corp_code, area_code, brand_code, searchValue, "");
            } else if (!role_code.equals(Common.ROLE_SYS) && page_number != 1) {
                if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
                    String area_code = jsonObject.get("area_code").toString();
                    list = storeService.selStoreByAreaBrandCode(page_number, page_size, corp_code, area_code, brand_code, searchValue, "");
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = jsonObject.get("area_code").toString();
                    String area_store_code = "";
                    if (area_code.equals("")) {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        area_store_code = request.getSession().getAttribute("store_code").toString();
                    }
                    list = storeService.selStoreByAreaBrandCode(page_number, page_size, corp_code, area_code, brand_code, searchValue, area_store_code);

                } else if (role_code.equals(Common.ROLE_BM)) {
                    brand_code = jsonObject.get("brand_code").toString();
                    String area_code = jsonObject.get("area_code").toString();
                    if (brand_code.equals("")) {
                        brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    if (area_code.equals("")) {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    list = storeService.selStoreByAreaBrandCode(page_number, page_size, corp_code, area_code, brand_code, searchValue, "");
                } else {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = storeService.selStoreByStoreCodes(page_number, page_size, store_code, corp_code, searchValue);
                }
            }
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺列表(店长面板)
     */
    @RequestMapping(value = "/findStore", method = RequestMethod.GET)
    @ResponseBody
    public String selectStore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject result = new JSONObject();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            List<Store> list = new ArrayList<Store>();
            if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeService.selectStore(corp_code, store_code);
            } else if (role_code.equals(Common.ROLE_BM)) {
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                String area_code = request.getSession().getAttribute("area_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                list = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession().getAttribute("area_code").toString();
                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                String area_store_code = request.getSession().getAttribute("store_code").toString();
                list = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "", area_store_code);
            } else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_SYS)) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    if (request.getParameter("corp_code") != null) {
                        corp_code = request.getParameter("corp_code");
                    } else {
                        corp_code = "C10000";
                    }
                }
                list = storeService.selStoreByAreaBrandCode(corp_code, "", "", "", "");
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取企业下城市
     */
    @RequestMapping(value = "/getCorpCity", method = RequestMethod.POST)
    @ResponseBody
    public String getCorpCity(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String search_value = jsonObject.get("search_value").toString();

            List<Store> list = storeService.selectStoreCity(corp_code, search_value);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addShop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String user_name = request.getSession().getAttribute("user_name").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = storeService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                String store_code = jsonObject.get("store_code").toString().trim();
                String corp_code = jsonObject.get("corp_code").toString().trim();
                String isactive = jsonObject.get("isactive").toString();
                Store store = storeService.getStoreByCode(corp_code, store_code, isactive);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(store.getId()));
                if (null != store.getIsactive() && null != store.getIsopen() && store.getIsactive().equals("Y") && store.getIsopen().equals("Y")) {
                    storeService.insertStoreStartOrEnd(Integer.parseInt(String.valueOf(store.getId())), "start", user_code, user_name);
                }
                //----------------行为日志------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "店铺管理";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("store_code").toString();
                String t_name = action_json.get("store_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editShop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        String user_name = request.getSession().getAttribute("user_name").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = storeService.update(message, user_id, user_name);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");

                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "店铺管理";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("store_code").toString();
                String t_name = action_json.get("store_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {

                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String shop_id = jsonObject.get("id").toString();
            String[] ids = shop_id.split(",");
            String msg = null;
            int count = 0;
            for (int i = 0; i < ids.length; i++) {
                logger.info("inter---------------" + Integer.valueOf(ids[i]));
                Store store = storeService.getById(Integer.valueOf(ids[i]));
                if (store != null) {
                    String store_code = store.getStore_code();
                    String corp_code = store.getCorp_code();
                    List<User> user = storeService.getStoreUser(corp_code, store_code, "", role_code, "");
                    count = user.size();
                    if (count > 0) {
                        msg = "店铺" + store_code + "下有所属员工，请先处理店铺下员工再删除";
                        break;
                    }
                    count = storeService.selectAchCount(corp_code, store.getStore_code());
                    if (count > 0) {
                        msg = "店铺" + store_code + "下的业绩目标，请先处理店铺下业绩再删除";
                        break;
                    }
//                    storeService.deleteStoreQrcode(corp_code, store_code);
                }
//                storeService.delete(Integer.valueOf(ids[i]));

                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "店铺管理";
                String action = Common.ACTION_DEL;
                String t_corp_code = store.getCorp_code();
                String t_code = store.getStore_code();
                String t_name = store.getStore_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
            }
            if (count > 0) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            } else {
                for (int i = 0; i < ids.length; i++) {
                    Store store = storeService.getById(Integer.valueOf(ids[i]));
                    if (store != null) {
                        String store_code = store.getStore_code();
                        String corp_code = store.getCorp_code();
                        storeService.deleteStoreQrcode(corp_code, store_code);
                        storeService.deleteStoreRelation(corp_code, store_code);
                    }
                    storeService.delete(Integer.valueOf(ids[i]));
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 选择店铺
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String shop_id = jsonObject.get("id").toString();
            Store store = storeService.getStoreById(Integer.parseInt(shop_id));
            store.setProvince_location_name(store.getProvince());
            store.setCity_location_name(store.getCity());
            store.setArea_location_name(store.getArea());
            data = JSON.toJSONString(store);
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            e.printStackTrace();
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("店铺信息异常");
            logger.info(e.getMessage() + e.toString());
        }
        logger.info("info-----" + bean.getJsonStr());
        return bean.getJsonStr();
    }

    /**
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String find_type = "";
            if (jsonObject.containsKey("find_type")) {
                find_type = jsonObject.get("find_type").toString();
            }

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Store> list = new PageInfo<Store>();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = storeService.getAllStore(request, page_number, page_size, "", search_value, "Y", "", "", "", "", find_type);
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                list = storeService.getAllStore(request, page_number, page_size, corp_code, search_value, "Y", "", "", "", "", find_type);

                // list = storeService.getAllStore(request, page_number, page_size, "", search_value, "Y", "", "", "", "", find_type,manager_corp);
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStore(request, page_number, page_size, corp_code, search_value, "Y", "", "", "", "", find_type);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    if (brand_code == null || brand_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属品牌");
                        return dataBean.getJsonStr();
                    } else {
                        String[] areaCodes = null;
                        if (area_code != null && !area_code.equals("")) {
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            areaCodes = area_code.split(",");
                            for (int i = 0; i < areaCodes.length; i++) {
                                areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                            }
                        }
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        list = storeService.selectByAreaBrand(page_number, page_size, corp_code, areaCodes, null, brandCodes, search_value, "Y", "", "", "", "", find_type);
                    }
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    if (area_code == null || area_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属区域");
                        return dataBean.getJsonStr();
                    } else {
                        //加上特殊字符，进行查询
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areaCodes = area_code.split(",");
                        String[] storeCodes = null;
                        for (int i = 0; i < areaCodes.length; i++) {
                            areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                        }
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }
                        list = storeService.selectByAreaBrand(page_number, page_size, corp_code, areaCodes, storeCodes, null, search_value, "Y", "", "", "", "", find_type);
                    }
                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    if (store_code == null || store_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属店铺");
                        return dataBean.getJsonStr();
                    } else {
                        list = storeService.selectByUserId(page_number, page_size, store_code, corp_code, search_value, "Y");
                    }
                } else {
                    List<Store> list1 = new ArrayList<Store>();
                    list.setList(list1);
                }
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 分角色
     * 拉取所有的品牌（可用）
     */
    @RequestMapping(value = "/brand", method = RequestMethod.POST)
    @ResponseBody
    public String getBrand(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();

        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String search_value = "";
            String[] codes = null;
            if (jsonObject.containsKey("searchValue")) {
                search_value = jsonObject.get("searchValue").toString();
            }
            List<Brand> brand = new ArrayList<Brand>();
            if (role_code.equals(Common.ROLE_SYS) && jsonObject.containsKey("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
                corp_code = jsonObject.get("corp_code").toString();
                brand = brandService.getActiveBrand(corp_code, search_value, codes);
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
//                if (jsonObject.containsKey("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
//                    corp_code = jsonObject.get("corp_code").toString();
//                } else {
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
//                }
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                brand = brandService.getActiveBrand(corp_code, search_value, codes);
//                corp_code = jsonObject.get("corp_code").toString();
//                if(corp_code.equals("C10000")) {
//                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                    System.out.println("manager_corp=====>" + manager_corp);
//                    corp_code = jsonObject.get("corp_code").toString();
//                    brand = brandService.getActiveBrand(corp_code, search_value, codes);
//                }else{
//                    brand = brandService.getActiveBrand(corp_code, search_value, codes);
//                }
            } else if (role_code.equals(Common.ROLE_BM)) {
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                codes = brand_code.split(",");
                brand = brandService.getActiveBrand(corp_code, search_value, codes);
            } else if (role_code.equals(Common.ROLE_GM)) {
                brand = brandService.getActiveBrand(corp_code, search_value, codes);
            } else if (role_code.equals(Common.ROLE_AM)) {
                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                String[] areas = area_code.split(",");
                String[] stores = null;
                if (!store_code.equals("")) {
                    stores = store_code.split(",");
                }
                List<Store> storeLists = storeService.selectByAreaBrand(corp_code, areas, stores, null, Common.IS_ACTIVE_Y);
                String brand_code1 = "";
                for (int i = 0; i < storeLists.size(); i++) {
                    String brand_code = storeLists.get(i).getBrand_code();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    if (!brand_code.endsWith(",")) {
                        brand_code = brand_code + ",";
                    }
                    brand_code1 = brand_code1 + brand_code;
                }
                codes = brand_code1.split(",");
                brand = brandService.getActiveBrand(corp_code, search_value, codes);
            } else if (role_code.equals(Common.ROLE_STAFF) || role_code.equals(Common.ROLE_SM)) {
//                List<Store> stores = storeService.selectByStoreCodes(store_code, corp_code, Common.IS_ACTIVE_Y);
//                String brand_code1 = "";
//                for (int i = 0; i < stores.size(); i++) {
//                    String brand_code = stores.get(i).getBrand_code();
//                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                    if (!brand_code.endsWith(",")) {
//                        brand_code = brand_code + ",";
//                    }
//                    brand_code1 = brand_code1 + brand_code;
//                }
//                if (!brand_code1.equals("")) {
//                    codes = brand_code1.split(",");
//                    brand = brandService.getActiveBrand(corp_code, search_value, codes);
//                }
            }

            JSONArray array = new JSONArray();
            JSONObject brands = new JSONObject();
            for (int i = 0; i < brand.size(); i++) {
                Brand brand1 = brand.get(i);
                String brand_code = brand1.getBrand_code();
                String brand_name = brand1.getBrand_name();
                JSONObject obj = new JSONObject();
                obj.put("brand_code", brand_code);
                obj.put("brand_name", brand_name);
                array.add(obj);
            }
            brands.put("brands", array);
            brands.put("total", array.size());

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(brands.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 分角色
     * 根据店铺拉取店铺下的员工（可用）
     */
    @RequestMapping(value = "/staff", method = RequestMethod.POST)
    @ResponseBody
    public String getStaff(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String group_code = request.getSession().getAttribute("group_code").toString();

        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String store_code = jsonObject.get("store_code").toString();
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();
            List<User> user = new ArrayList<User>();
            if (role_code.equals(Common.ROLE_STAFF)) {
                //列表只显示自己
                String user_id = request.getSession().getAttribute("user_id").toString();
                User user1 = userService.getUserById(user_id);
                user.add(user1);
            } else if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_AM)) {
                //显示导购，店长
                Group group = groupService.selectByCode(corp_code, group_code, Common.IS_ACTIVE_Y);
                if (group.getIsshow() != null && group.getIsshow().equals("N")) {
                    role_code = Common.ROLE_AM;
                }
                user = storeService.getStoreUser(corp_code, store_code, "", role_code, Common.IS_ACTIVE_Y);

            } else {
                user = storeService.getStoreUser(corp_code, store_code, "", Common.ROLE_AM, Common.IS_ACTIVE_Y);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(JSON.toJSONString(user));
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 分角色
     * 根据店铺拉取店铺下的员工（可用,分页）
     */
    @RequestMapping(value = "/staffPage", method = RequestMethod.POST)
    @ResponseBody
    public String staffPage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String group_code = request.getSession().getAttribute("group_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String store_code = jsonObject.get("store_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();
            PageInfo<User> list = new PageInfo<User>();
            if (role_code.equals(Common.ROLE_STAFF)) {
                //列表只显示自己
                String user_id = request.getSession().getAttribute("user_id").toString();
                User user1 = userService.getUserById(user_id);
                List<User> list1 = new ArrayList<User>();
                list1.add(user1);
                list.setList(list1);
            } else if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_AM)) {
                //显示导购，店长
                Group group = groupService.selectByCode(corp_code, group_code, Common.IS_ACTIVE_Y);
                if (group.getIsshow() != null && group.getIsshow().equals("N")) {
                    role_code = Common.ROLE_AM;
                }
                list = storeService.getStoreUsers(page_number,page_size,corp_code, store_code, "", role_code, Common.IS_ACTIVE_Y,search_value);
            } else {
                list = storeService.getStoreUsers(page_number,page_size,corp_code, store_code, "", Common.ROLE_AM, Common.IS_ACTIVE_Y,search_value);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(JSON.toJSONString(list));
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 分角色
     * 根据店铺拉取店铺下的员工（所有）
     */
    @RequestMapping(value = "/staff_list", method = RequestMethod.POST)
    @ResponseBody
    public String getStaffList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String store_code = jsonObject.get("store_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            List<User> user = new ArrayList<User>();
            if (role_code.equals(Common.ROLE_STAFF)) {
                //列表只显示自己
                String user_id = request.getSession().getAttribute("user_id").toString();
                User user1 = userService.getUserById(user_id);
                user.add(user1);
            } else if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_AM)) {
                //店长，区经显示比自己级别低的
                user = storeService.getStoreUser(corp_code, store_code, "", role_code, "");
            } else {
                //显示店长，导购
                user = storeService.getStoreUser(corp_code, store_code, "", Common.ROLE_AM, "");
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(JSON.toJSONString(user));
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/staff/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String deleteStaff(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String store_code = jsonObject.get("store_code").toString();
            String user_id = jsonObject.get("user_id").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                storeService.deleteStoreUser(ids[i], store_code);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("delete success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/storeCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String storeCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String corp_code = jsonObject.get("corp_code").toString();
            Store store = null;
            if (jsonObject.containsKey("store_code")) {
                String store_code = jsonObject.get("store_code").toString();
                store = storeService.getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
            }
            if (jsonObject.containsKey("store_id")) {
                String store_id = jsonObject.get("store_id").toString();
                store = storeService.storeIdExist(corp_code, store_id);
            }

            if (store != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("店铺编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("店铺编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);

            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);


        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/storeNameExist", method = RequestMethod.POST)
    @ResponseBody
    public String storeNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String store_name = jsonObject.get("store_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            //         Area area = areaService.getAreaByName(corp_code, store_code);
            List<Store> store = storeService.getStoreByName(corp_code, store_name, Common.IS_ACTIVE_Y);

            if (store.size() > 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("店铺名称已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("店铺名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);

            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);


        }
        return dataBean.getJsonStr();
    }


    /**
     * 根据store_code生成店铺二维码
     */
    @RequestMapping(value = "/creatQrcode", method = RequestMethod.POST)
    @ResponseBody
    public String creatQrcode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("------------StoreController creatQrcode" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String store_code = jsonObject.get("store_code").toString();
            String app_id = jsonObject.get("app_id").toString();
            CorpWechat corpWechat = corpService.getCorpByAppId(corp_code,app_id);
            if (corpWechat != null && corpWechat.getApp_id() != null && corpWechat.getApp_id() != "") {
                String is_authorize = corpWechat.getIs_authorize();
                String auth_appid = corpWechat.getApp_id();
                if (is_authorize.equals("Y")) {
                    Store store = storeService.getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
                    if (store == null) {
                        dataBean.setId(id);
                        dataBean.setMessage("该店铺为不可用状态，不支持生成二维码");
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        return dataBean.getJsonStr();
                    }
                    String result = storeService.creatStoreQrcode(corp_code, store_code, auth_appid, user_id);
                    if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                        dataBean.setId(id);
                        dataBean.setMessage("生成二维码失败");
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        return dataBean.getJsonStr();
                    } else if (result.equals("48001")) {
                        dataBean.setId(id);
                        dataBean.setMessage("该功能未授权");
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        return dataBean.getJsonStr();
                    }
                    store.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                    store.setModifier(user_id);
                    storeService.updateStore(store);
                    dataBean.setId(id);
                    dataBean.setMessage(result);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    return dataBean.getJsonStr();
                }
            }
            dataBean.setId(id);
            dataBean.setMessage("所属企业未授权");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


    /**
     * 一键生成所选店铺的店铺二维码
     */
    @RequestMapping(value = "/creatStoresQrcode", method = RequestMethod.POST)
    @ResponseBody
    public String creatStoresQrcode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("------------UserController creatQrcode" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String auth_appid = jsonObject.get("app_id").toString();
            JSONArray list = JSONArray.parseArray(jsonObject.get("list").toString());
            String corp_code = "";
            if (list.size() > 0){
                JSONObject json = JSONObject.parseObject(list.get(0).toString());
                corp_code = json.get("corp_code").toString();
            }
            CorpWechat corpWechat = corpService.getCorpByAppId(corp_code,auth_appid);

            for (int i = 0; i < list.size(); i++) {
                JSONObject json = JSONObject.parseObject(list.get(i).toString());
                corp_code = json.get("corp_code").toString();
                String store_code = json.get("store_code").toString();
                if (corpWechat.getCorp_code().equals(corp_code)) {
                    String is_authorize = corpWechat.getIs_authorize();
                    if (is_authorize.equals("Y")) {
                        String result = storeService.creatStoreQrcode(corp_code, store_code, auth_appid, user_code);
                        if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                            dataBean.setId(id);
                            dataBean.setMessage("生成二维码失败");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            return dataBean.getJsonStr();
                        } else if (result.equals("48001")) {
                            dataBean.setId(id);
                            dataBean.setMessage("该功能未授权");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            return dataBean.getJsonStr();
                        }
                    } else {
                        dataBean.setId(id);
                        dataBean.setMessage(corp_code + "企业未授权");
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        return dataBean.getJsonStr();
                    }
                } else {
                    dataBean.setId(id);
                    dataBean.setMessage(corp_code + "企业未授权");
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    return dataBean.getJsonStr();
                }
            }
            dataBean.setId(id);
            dataBean.setMessage("生成完成");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 删除二维码
     */
    @RequestMapping(value = "/deletQrcode", method = RequestMethod.POST)
    @ResponseBody
    public String deletQrcode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("------------StoreController deletQrcode" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String store_code = jsonObject.get("store_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String app_id = jsonObject.get("app_id").toString();

            storeService.deleteStoreQrcodeOne(corp_code, store_code, app_id);
            dataBean.setId(id);
            dataBean.setMessage("success");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String find_type = "";
            if (jsonObject.containsKey("find_type")) {
                find_type = jsonObject.get("find_type").toString();
            }
            String screen = jsonObject.get("list").toString();
            PageInfo<Store> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, "", search_value, "", "", "", "", "", find_type);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_CM)) {
                        String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                        System.out.println("manager_corp=====>" + manager_corp);
                        corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                        System.out.println("getCorpCodeByCm=====>" + corp_code);
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);

                    } else if (role_code.equals(Common.ROLE_BM)) {
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        String area_code = request.getSession().getAttribute("area_code").toString();

                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        String[] areaCodes = null;
                        if (!area_code.equals("")) {
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            areaCodes = area_code.split(",");
                            for (int i = 0; i < areaCodes.length; i++) {
                                areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                            }
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, null, brandCodes, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_AM)) {
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        String store_code = request.getSession().getAttribute("store_code").toString();

                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                        String[] areaCodes = area_code.split(",");
                        String[] storeCode = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCode = store_code.split(",");
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, storeCode, null, search_value, "", "", "", "", "", find_type);
                    } else {
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = storeService.selectByUserId(1, Common.EXPORTEXECLCOUNT, store_code, corp_code, search_value, "");
                    }
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_CM)) {
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>" + manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>" + corp_code);
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);

                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, brand_code, "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, "", "", map, store_code, "", find_type);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", store_code, map, "", "", find_type);
                }
            }
            List<Store> stores = list.getList();
            System.out.println("TimeOut测试：" + stores.size());
            if (stores.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            for (Store store : stores) {
                String brand_name = store.getBrand_name();
                String replaceStr = WebUtils.StringFilter(brand_name);
                store.setBrand_name(replaceStr);
                String area_name = store.getArea_name();
                String replaceArea = WebUtils.StringFilter(area_name);
                store.setArea_name(replaceArea);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(stores);
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, stores, map, response, request, "店铺列表");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            logger.info(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl_view1", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl_view(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String find_type = "";
            if (jsonObject.containsKey("find_type")) {
                find_type = jsonObject.get("find_type").toString();
            }

            String screen = jsonObject.get("list").toString();
            PageInfo<Store> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, "", search_value, "", "", "", "", "", find_type);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_CM)) {
                        String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                        System.out.println("manager_corp=====>" + manager_corp);
                        corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                        System.out.println("getCorpCodeByCm=====>" + corp_code);
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);

                    } else if (role_code.equals(Common.ROLE_BM)) {
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        String area_code = request.getSession().getAttribute("area_code").toString();

                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        String[] areaCodes = null;
                        if (!area_code.equals("")) {
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            areaCodes = area_code.split(",");
                            for (int i = 0; i < areaCodes.length; i++) {
                                areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                            }
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, null, brandCodes, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_AM)) {
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        String store_code = request.getSession().getAttribute("store_code").toString();

                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                        String[] areaCodes = area_code.split(",");
                        String[] storeCode = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCode = store_code.split(",");
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, storeCode, null, search_value, "", "", "", "", "", find_type);
                    } else {
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = storeService.selectByUserId(1, Common.EXPORTEXECLCOUNT, store_code, corp_code, search_value, "");
                    }
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_CM)) {
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>" + manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>" + corp_code);
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);

                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, brand_code, "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, "", "", map, store_code, "", find_type);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", store_code, map, "", "", find_type);
                }
            }
            List<Store> stores = list.getList();
            //  System.out.println("TimeOut测试：" + stores.size());
            if (stores.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            String pathname = OutHtmlHelper.OutHtml_store(stores, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("api/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            logger.info(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportZip_old", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl_zip(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String is_logo = jsonObject.get("is_logo").toString();
            String find_type = "";
            if (jsonObject.containsKey("find_type")) {
                find_type = jsonObject.get("find_type").toString();
            }
            String screen = jsonObject.get("list").toString();
            PageInfo<Store> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, "", search_value, "", "", "", "", "", find_type);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_CM)) {
                        String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                        System.out.println("manager_corp=====>" + manager_corp);
                        corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                        System.out.println("getCorpCodeByCm=====>" + corp_code);
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);

                    } else if (role_code.equals(Common.ROLE_BM)) {
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        String area_code = request.getSession().getAttribute("area_code").toString();

                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        String[] areaCodes = null;
                        if (!area_code.equals("")) {
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            areaCodes = area_code.split(",");
                            for (int i = 0; i < areaCodes.length; i++) {
                                areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                            }
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, null, brandCodes, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_AM)) {
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        String store_code = request.getSession().getAttribute("store_code").toString();

                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                        String[] areaCodes = area_code.split(",");
                        String[] storeCode = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCode = store_code.split(",");
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, storeCode, null, search_value, "", "", "", "", "", find_type);
                    } else {
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = storeService.selectByUserId(1, Common.EXPORTEXECLCOUNT, store_code, corp_code, search_value, "");
                    }
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_CM)) {
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>" + manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>" + corp_code);
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);

                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, brand_code, "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, "", "", map, store_code, "", find_type);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", store_code, map, "", "", find_type);
                }
            }
            List<Store> stores = list.getList();
            //  System.out.println("TimeOut测试：" + stores.size());
            if (stores.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            String pathname = OutHtmlHelper.OutZip_store(stores, request);


            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            if (pathname.equals("空文件夹")) {
                errormessage = "导出的店铺中都没有二维码";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("api/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            logger.info(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /***
     * 打包带品牌logo的店铺二维码
     */
    @RequestMapping(value = "/exportZipBrandLogo", method = RequestMethod.GET)
    @ResponseBody
    public String exportZipBrandLogo(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String search_value = request.getParameter("search_value");
            String corp_code = "C10222";

            Map<String, String> map1 = new HashMap<String, String>();
            map1.put("store_name", search_value);

            List<Store> storeList = storeService.getStoreByBrandCode(corp_code, "", "R", "", map1, "", "Y");

            int count = 0;
            for (int i = 0; i < storeList.size(); i++) {
                String store_code = storeList.get(i).getStore_code();
                String store_name = storeList.get(i).getStore_name();

                List<StoreQrcode> qrcodes = storeService.selctStoreQrcode(corp_code, store_code, "wx934b3d449f3df95b");
                if (qrcodes.size() > 0) {
                    count++;
                    String qrcode = qrcodes.get(0).getQrcode_content();
                    QrcodeUtils.encode(qrcode, "http://products-image.oss-cn-hangzhou.aliyuncs.com/BRAND/logoC10222_R_20170420143221782.jpg", "/work1/env/web/ishop/apache-tomcat-8.5.3/webapps/相册R", store_name + "_" + store_code);
                }
            }
            //  System.out.println("TimeOut测试：" + stores.size());

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success" + count);
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            logger.info(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /***
     * Execl增加
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String result = "";
        Workbook rwb = null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = 15;//得到所有的列
            int rows = rs.getRows();//得到所有的行

            if (rows < 4) {
                result = "：请从模板第4行开始插入正确数据";
                int i = 5 / 0;
            }
            if (rows > 9999) {
                result = "：数据量过大，导入失败";
                int i = 5 / 0;
            }
            Cell[] column3 = rs.getColumn(0);//企业编号
            Cell[] column = rs.getColumn(1);//店铺编号
            Cell[] column2 = rs.getColumn(2);//店铺ID
            Cell[] column1 = rs.getColumn(3);//店铺名称
            Cell[] column4 = rs.getColumn(4);//区域编号
            Cell[] column5 = rs.getColumn(5);//品牌编号

            Pattern pattern1 = Pattern.compile("C\\d{5}");
            if (!role_code.equals(Common.ROLE_SYS)) {
                for (int i = 3; i < column3.length; i++) {
                    if (column3[i].getContents().toString().trim().equals("")) {
                        continue;
                    }
                    if (!column3[i].getContents().toString().trim().equals(corp_code)) {
                        result = "：第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                    if (matcher.matches() == false) {
                        result = "：第" + (i + 1) + "行企业编号格式有误";
                        int b = 5 / 0;
                        break;
                    }
                }
            }
            for (int i = 3; i < column3.length; i++) {
                if (column3[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行企业编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Corp corp = corpService.selectByCorpId(0, column3[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (corp == null) {
                    result = "：第" + (i + 1) + "行企业编号不存在";
                    int b = 5 / 0;
                    break;
                }

            }
            String onlyCell1 = LuploadHelper.CheckOnly(column);
            if (onlyCell1.equals("存在重复值")) {
                result = "：Execl中店铺编号存在重复值";
                int b = 5 / 0;
            }
            String onlyCell2 = LuploadHelper.CheckOnly(column1);
            if (onlyCell2.equals("存在重复值")) {
                result = "：Execl中店铺名称存在重复值";
                int b = 5 / 0;
            }
            String onlyCell8 = LuploadHelper.CheckOnly(column2);
            if (onlyCell8.equals("存在重复值")) {
                result = "：Execl中店铺ID存在重复值";
                int b = 5 / 0;
            }
//            for (int i = 3; i < column.length; i++) {
//                if (column[i].getContents().toString().trim().equals("")) {
//                    continue;
//                }
//                Store store = storeService.getStoreByCode(column3[i].getContents().toString().trim(), column[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
//                if (store != null) {
//                    result = "：第" + (i + 1) + "行店铺编号已存在";
//                    int b = 5 / 0;
//                    break;
//                }
//            }
//
//            for (int i = 3; i < column1.length; i++) {
//                if (column1[i].getContents().toString().trim().equals("")) {
//                    continue;
//                }
//                List<Store> store = storeService.getStoreByName(column3[i].getContents().toString().trim(), column1[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
//                if (store.size() > 0) {
//                    result = "：第" + (i + 1) + "行店铺名称已存在";
//                    int b = 5 / 0;
//                    break;
//                }
//            }
            //    Pattern pattern = Pattern.compile("B\\d{4}");
            for (int i = 3; i < column5.length; i++) {
                if (column5[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                String brands = column5[i].getContents().toString().trim();
                String[] splitBrands = brands.split(",");
                if (splitBrands.length > 10) {
                    result = "：第" + (i + 1) + "行品牌编号上限10个";
                    int b = 5 / 0;
                }
                for (int j = 0; j < splitBrands.length; j++) {
//                    Matcher matcher = pattern.matcher(splitBrands[j]);
//                    if (matcher.matches() == false) {
//                        result = "：第" + (i + 1) + "行,第" + (j + 1) + "个品牌编号格式有误";
//                        int b = 5 / 0;
//                        break;
//                    }

                    Brand brand = brandService.getBrandByCode(column3[i].getContents().toString().trim(), splitBrands[j], Common.IS_ACTIVE_Y);
                    if (brand == null) {
                        result = "：第" + (i + 1) + "行,第" + (j + 1) + "个品牌编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                }
            }
            //  Pattern pattern7 = Pattern.compile("A\\d{4}");

            for (int i = 3; i < column4.length; i++) {
//                Matcher matcher = pattern7.matcher(column7[i].getContents().toString().trim());
//                if (matcher.matches() == false) {
//                    result = "：第" + (i + 1) + "行区域编号格式有误";
//                    int b = 5 / 0;
//                    break;
//                }
                if (column4[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                String areas = column4[i].getContents().toString().trim();
                String[] splitAreas = areas.split(",");
                if (splitAreas.length > 20) {
                    result = "：第" + (i + 1) + "行区域编号上限20个";
                    int b = 5 / 0;
                }
                for (int j = 0; j < splitAreas.length; j++) {
                    Area area = areaService.getAreaByCode(column3[i].getContents().toString().trim(), splitAreas[j], Common.IS_ACTIVE_Y);
                    if (area == null) {
                        result = "：第" + (i + 1) + "行,第" + (j + 1) + "个区域编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                }

            }
//            for (int i = 0; i < column2.length; i++) {
//                if (column2[i].getContents().toString().trim().equals("")) {
//                    continue;
//                }
//                Store store = storeService.selStoreByStroeId(column3[i].getContents().toString().trim(), column2[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
//                if (store != null) {
//                    result = "：第" + (i + 1) + "行店铺ID已存在";
//                    int b = 5 / 0;
//                    break;
//                }
//            }
            ArrayList<Store> stores = new ArrayList<Store>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    Store store = new Store();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    String store_code = rs.getCell(j++, i).getContents().toString().trim();
                    String store_id = rs.getCell(j++, i).getContents().toString().trim();
                    System.out.println("-----------------------store_id----------------------:" + store_id);
//                    if(store_id.equals("")){
//                        store_id=store_code;
//                    }
                    String store_name = rs.getCell(j++, i).getContents().toString().trim();
                    String area_code = rs.getCell(j++, i).getContents().toString().trim();
                    String brand_code = rs.getCell(j++, i).getContents().toString().trim();
                    String province = rs.getCell(j++, i).getContents().toString().trim();
                    String city = rs.getCell(j++, i).getContents().toString().trim();
                    String area = rs.getCell(j++, i).getContents().toString().trim();
                    String street = rs.getCell(j++, i).getContents().toString().trim();
                    String store_type = rs.getCell(j++, i).getContents().toString().trim();
                    String offline_area = rs.getCell(j++, i).getContents().toString().trim();
                    String dealer = rs.getCell(j++, i).getContents().toString().trim();
                    String flg_tob = rs.getCell(j++, i).getContents().toString().trim();
                    String isactive = rs.getCell(j++, i).getContents().toString().trim();
                    if (cellCorp.equals("") && store_code.equals("") && store_name.equals("") && area_code.equals("") && brand_code.equals("")) {
                        continue;
                    }
                    if (cellCorp.equals("") || store_code.equals("") || store_name.equals("") || area_code.equals("") || brand_code.equals("")) {
                        result = "：第" + (i + 1) + "行信息不完整,请参照Execl中对应的批注";
                        int a = 5 / 0;
                    }
                    if (!role_code.equals(Common.ROLE_SYS)) {
                        store.setCorp_code(corp_code);
                    } else {
                        store.setCorp_code(cellCorp);
                    }
                    store.setStore_code(store_code);
                    store.setStore_id(store_id);
                    store.setStore_name(store_name);
                    store.setArea_code(area_code);
                    store.setBrand_code(brand_code);
                    store.setProvince(province);
                    store.setCity(city);
                    store.setArea(area);
                    store.setStreet(street);
                    store.setStore_type(store_type);
                    store.setOffline_area(offline_area);
                    store.setDealer(dealer);
                    if (flg_tob.toUpperCase().equals("N")) {
                        store.setFlg_tob("N");
                    } else {
                        store.setFlg_tob("Y");
                    }
                    if (isactive.toUpperCase().equals("N")) {
                        store.setIsactive("N");
                    } else {
                        store.setIsactive("Y");
                    }
                    Date now = new Date();
                    store.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    store.setCreater(user_id);
                    store.setModified_date(Common.DATETIME_FORMAT.format(now));
                    store.setModifier(user_id);
                    stores.add(store);
                    //  result = storeService.insertExecl(store);
                }
            }

            for (Store store : stores
                    ) {
                Store store1 = storeService.getStoreByCode(store.getCorp_code().trim(), store.getStore_code().toString().trim(), Common.IS_ACTIVE_Y);
                if (store1 != null) {
                    store.setId(store1.getId());
                    result = storeService.updateExecl(store);
                } else {
                    result = storeService.insertExecl(store);
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } finally {
            if (rwb != null) {
                rwb.close();
            }
            System.gc();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺管理
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String find_type = "";
            if (jsonObject.containsKey("find_type")) {
                find_type = jsonObject.get("find_type").toString();
            }
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = storeService.getAllStoreScreen(page_number, page_size, "", "", "", "", map, "", "", find_type);
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, "", "", "", map, "", "", find_type);

                //  list = storeService.getAllStoreScreen(page_number, page_size, corp_code, "", "", "", map, "", "", find_type,manager_corp);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, "", "", "", map, "", "", find_type);
            } else if (role_code.equals(Common.ROLE_BM)) {
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                String area_codes = request.getSession(false).getAttribute("area_code").toString();
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, area_codes, brand_code, "", map, "", "", find_type);
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_codes = request.getSession(false).getAttribute("area_code").toString();
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, area_codes, "", "", map, store_code, "", find_type);
            } else {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, "", "", store_code, map, "", "", find_type);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 同步
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/synchronization", method = RequestMethod.POST)
    @ResponseBody
    public String synchronization(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        try {
            String jsString = request.getParameter("param");
            logger.info("json--user sign-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                corp_code = "C10000";
            }
//            com.alibaba.fastjson.JSONObject obj_corp = new com.alibaba.fastjson.JSONObject();
//            obj_corp.put("corp_code", corp_code);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("DataSynchronization", datalist);
            if (dataBox.status.toString().equals("SUCCESS")) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("同步成功");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("数据已是最新，无需同步");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString() + "========ex==========");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺管理
     * 根据店铺编号，获取店铺信息
     */
    @RequestMapping(value = "/getStoreList", method = RequestMethod.POST)
    @ResponseBody
    public String getStoreList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String store_code = jsonObject.getString("store_code");

            JSONObject result = new JSONObject();
            List<Store> list = storeService.selectByStoreCodes(store_code, corp_code, Common.IS_ACTIVE_Y);

            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    //拉取店铺群组下的所属经销商（dealer）,所属区域（offline_area），所属店铺类型（store_type）
    @RequestMapping(value = "/getStoreByOdsType", method = RequestMethod.POST)
    @ResponseBody
    public String getStoreByOdsType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("getStoreByOdsType---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.getString("corp_code");
            }
            List<Store> offline_areas = storeService.getStoreByOdsType(corp_code, "offline_area");
            List<Store> dealers = storeService.getStoreByOdsType(corp_code, "dealer");
            List<Store> store_types = storeService.getStoreByOdsType(corp_code, "store_type");
            JSONObject result = new JSONObject();
            result.put("offline_areas", JSON.toJSONString(offline_areas));
            result.put("dealers", JSON.toJSONString(dealers));
            result.put("store_types", JSON.toJSONString(store_types));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("获取失败");
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/batchStartOrEnd", method = RequestMethod.POST)
    @ResponseBody
    public String getStoreByStart(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String user_name = request.getSession().getAttribute("user_name").toString();
            String jsString = request.getParameter("param");
            logger.info("getStoreByStart---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String type = jsonObject.get("type").toString();
            String store_id = jsonObject.get("id").toString();
            String[] split = store_id.split(",");
            int count = 0;
            if (type.equals("start")) {
                for (int i = 0; i < split.length; i++) {

                    storeService.insertStoreStartOrEnd(Integer.parseInt(split[i]), "start", user_code, user_name);
                    count += 1;
                }
            } else {
                for (int i = 0; i < split.length; i++) {
                    storeService.insertStoreStartOrEnd(Integer.parseInt(split[i]), "end", user_code, user_name);
                    count += 1;
                }
            }
            if (count > 0) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("操作成功");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("操作失败");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("操作失败");
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/startStoreLogListBySearch", method = RequestMethod.POST)
    @ResponseBody
    public String startStoreLogList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int pages = 0;
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_store_start_end_log);

            String[] column_names = new String[]{"corp_name", "store_id", "store_name", "store_code"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor1.count());
            } else if (role_code.equals(Common.ROLE_CM)) {
//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>"+manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if(manager_corp_arr.size()>0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                DBCursor dbCursor2 = cursor.find(queryCondition1);
//
//                pages = MongoUtils.getPages(dbCursor2, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor2.count());
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor2.count());
            }
            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("操作失败");
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/startStoreLogListByScreen", method = RequestMethod.POST)
    @ResponseBody
    public String getScreen(HttpServletRequest request) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        DataBean dataBean = new DataBean();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String lists = jsonObject.get("list").toString();

            JSONArray array = JSONArray.parseArray(lists);

            BasicDBObject queryCondition = MongoHelperServiceImpl.andStartStoreLogListByScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_store_start_end_log);
            DBCursor dbCursor = null;
            int total = 0;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                total = dbCursor1.count();
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);

            } else if (role_code.equals(Common.ROLE_CM)) {
//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>"+manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if(manager_corp_arr.size()>0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                DBCursor dbCursor1 = cursor.find(queryCondition1);
//                total = dbCursor1.count();
//
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);

                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                total = dbCursor1.count();

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);

            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                total = dbCursor1.count();

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);

            }
            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", total);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());

        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/storeLogDelete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String callbackDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String[] ids = jsonObject.get("id").toString().split(",");
            //       System.out.println(ids.length+"------ids[i]jsonObject-------"+jsonObject.get("id").toString());
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_store_start_end_log);

            for (int i = 0; i < ids.length; i++) {
                //  System.out.println("-----delete---------"+ids[i]);
                DBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", new ObjectId(ids[i]));
                cursor.remove(deleteRecord);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /***
     * 导出数据
     * 导出带有logo的二维码
     */
    @RequestMapping(value = "/exportExecl_view", method = RequestMethod.POST)
    @ResponseBody
    public String export_view_qrcode(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String path = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String is_logo = jsonObject.get("is_logo").toString();

            String find_type = "";
            if (jsonObject.containsKey("find_type")) {
                find_type = jsonObject.get("find_type").toString();
            }

            String screen = jsonObject.get("list").toString();
            PageInfo<Store> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, "", search_value, "", "", "", "", "", find_type);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_CM)) {
                        String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                        System.out.println("manager_corp=====>" + manager_corp);
                        corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                        System.out.println("getCorpCodeByCm=====>" + corp_code);
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);

                    } else if (role_code.equals(Common.ROLE_BM)) {
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        String area_code = request.getSession().getAttribute("area_code").toString();

                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        String[] areaCodes = null;
                        if (!area_code.equals("")) {
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            areaCodes = area_code.split(",");
                            for (int i = 0; i < areaCodes.length; i++) {
                                areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                            }
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, null, brandCodes, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_AM)) {
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        String store_code = request.getSession().getAttribute("store_code").toString();

                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                        String[] areaCodes = area_code.split(",");
                        String[] storeCode = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCode = store_code.split(",");
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, storeCode, null, search_value, "", "", "", "", "", find_type);
                    } else {
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = storeService.selectByUserId(1, Common.EXPORTEXECLCOUNT, store_code, corp_code, search_value, "");
                    }
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_CM)) {
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>" + manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>" + corp_code);
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);

                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, brand_code, "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, "", "", map, store_code, "", find_type);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", store_code, map, "", "", find_type);
                }
            }
            List<Store> stores = list.getList();
            //  System.out.println("TimeOut测试：" + stores.size());
            if (stores.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            String url = "";
            JSONArray arr = new JSONArray();

            for (int i = 0; i < stores.size(); i++) {
                Store store = stores.get(i);
                String corp_codes = store.getCorp_code();
                String store_name = store.getStore_name();
                String store_code = store.getStore_code();
                String[] album = null;
                String album_obj = store.getQrcode();
                if (!album_obj.equals("")) {
                    album = album_obj.split("、");
                }
                if (album != null) {
                    for (int j = 0; j < album.length; j++) {
                        //根据store_code getQrcode
                        StoreQrcode storeQrcode = storeService.selectAppIdByQrcode(corp_codes, store_code, album[j]);
                        String appid = storeQrcode.getApp_id();
                        //  System.out.println("===============appid================"+appid);

                        //再根据appid获取applogo
                        //  getCorpByAppId
                        CorpWechat corpByAppId = corpService.getCorpByAppId(corp_code,appid);
                        if(null!=corpByAppId){
                            String logo = corpByAppId.getApp_logo();
                            JSONObject obj = new JSONObject();
                            obj.put("qrcode", album[j]);
                            obj.put("logo", logo);
                            if (null != logo && !logo.equals("") && !logo.equals("../img/bg1.png")) {
                                //带有logo的二维码图片 encode 【二维码 logo 地址 文件名】

                                obj.put("qrcode", album[j]);
                                obj.put("logo", logo);
                                arr.add(obj);
                                store.setQrcode_logo(arr.toJSONString());
                            } else {
                                obj.put("qrcode", album[j]);
                                obj.put("logo", "");
                                arr.add(obj);
                                store.setQrcode_logo(arr.toJSONString());
                            }
                        }else{
                            JSONObject obj1 = new JSONObject();
                            obj1.put("qrcode", album[j]);
                            obj1.put("logo", "");
                            arr.add(obj1);
                            store.setQrcode_logo(arr.toJSONString());
                        }



                    }

                }

            }
            String pathname = "";

            if (is_logo.equals("Y")) {
                logger.info("=========================is_logo================================" + is_logo);
                pathname = OutHtmlHelper.OutHtml_store_new(stores, request);
            } else {
                pathname = OutHtmlHelper.OutHtml_store(stores, request);

            }

            JSONObject result = new JSONObject();
            logger.info("==========pathname====================" + pathname);
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("api/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            logger.info(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportZip", method = RequestMethod.POST)
    @ResponseBody
    public String exportStoreQrcodeLogo_zip(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String path = "";
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String is_logo = jsonObject.get("is_logo").toString();
            String find_type = "";
            if (jsonObject.containsKey("find_type")) {
                find_type = jsonObject.get("find_type").toString();
            }
            String screen = jsonObject.get("list").toString();
            PageInfo<Store> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, "", search_value, "", "", "", "", "", find_type);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_CM)) {
                        String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                        System.out.println("manager_corp=====>" + manager_corp);
                        corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                        System.out.println("getCorpCodeByCm=====>" + corp_code);
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", "", "", "", find_type);

                    } else if (role_code.equals(Common.ROLE_BM)) {
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        String area_code = request.getSession().getAttribute("area_code").toString();

                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        String[] areaCodes = null;
                        if (!area_code.equals("")) {
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            areaCodes = area_code.split(",");
                            for (int i = 0; i < areaCodes.length; i++) {
                                areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                            }
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, null, brandCodes, search_value, "", "", "", "", "", find_type);
                    } else if (role_code.equals(Common.ROLE_AM)) {
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        String store_code = request.getSession().getAttribute("store_code").toString();

                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                        String[] areaCodes = area_code.split(",");
                        String[] storeCode = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCode = store_code.split(",");
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, storeCode, null, search_value, "", "", "", "", "", find_type);
                    } else {
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = storeService.selectByUserId(1, Common.EXPORTEXECLCOUNT, store_code, corp_code, search_value, "");
                    }
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_CM)) {
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>" + manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>" + corp_code);
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);

                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, brand_code, "", map, "", "", find_type);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, "", "", map, store_code, "", find_type);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", store_code, map, "", "", find_type);
                }
            }
            List<Store> stores = list.getList();
            //  System.out.println("TimeOut测试：" + stores.size());
            if (stores.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            String url = "";

            String path1 = "" + System.currentTimeMillis();
            String pathname = "";


            if (is_logo.equals("Y")) {

                for (int i = 0; i < stores.size(); i++) {
                    Store store = stores.get(i);
                    String corp_codes = store.getCorp_code();
                    String store_name = store.getStore_name();
                    if (store_name.contains("/")) {
                        store_name = store_name.replace("/", "");
                    }

                    String store_code = store.getStore_code();

                    String album_obj = store.getQrcode();
                    //判断为空的情况
                    String[] album = null;

                    if (!album_obj.equals("")) {
                        album = album_obj.split("、");
                    }
                    if (album != null) {
                        for (int j = 0; j < album.length; j++) {
                            //根据store_code getQrcode
                            StoreQrcode storeQrcode = storeService.selectAppIdByQrcode(corp_codes, store_code, album[j]);
                            logger.info("=========store_code=======store_code========================" + storeQrcode);
                            String appid = storeQrcode.getApp_id();
                            //  System.out.println("===============appid================"+appid);
                                String qr_img=storeQrcode.getQrcode_content();
                            //再根据appid获取applogo
                            //  getCorpByAppId
                            CorpWechat corpByAppId = corpService.getCorpByAppId(corp_code,appid);
                            String logo = corpByAppId.getApp_logo();
                            if (null != logo && !logo.equals("") && !logo.equals("../img/bg1.png")) {
                                store.setQrcode_logo(logo);
                                //带有logo的二维码图片 encode 【二维码 logo 地址 文件名】
                                QrcodeUtils.encode(qr_img, logo, "/work1/env/web/ishop/apache-tomcat-8.5.3/webapps/iShop/image/storeQrcode/" + path1, store_code + "_" + store_name + "_" + j);
                            } else {
                                QrcodeUtils.encode(qr_img, "", "/work1/env/web/ishop/apache-tomcat-8.5.3/webapps/iShop/image/storeQrcode/" + path1, store_code + "_" + store_name + "_" + j);
                            }
                        }
                    }
                }
                pathname = OutHtmlHelper.OutZip_store_new(stores, path1, request);
            } else {
                pathname = OutHtmlHelper.OutZip_store(stores, request);
            }

            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            if (pathname.equals("空文件夹")) {
                errormessage = "导出的店铺中都没有二维码";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("api/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            logger.info(errormessage);
        }
        return dataBean.getJsonStr();
    }


}
