package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    IceInterfaceService iceInterfaceService;

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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String searchValue = "";
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            String area_code = "";
            String brand_code = "";
            String city = "";
            if (jsonObject.has("searchValue")) {
                searchValue = jsonObject.get("searchValue").toString();
            }
            if (jsonObject.has("area_code")) {
                area_code = jsonObject.get("area_code").toString();
            }
            if (jsonObject.has("brand_code")) {
                brand_code = jsonObject.get("brand_code").toString();
            }
            if (jsonObject.has("city")) {
                city = jsonObject.get("city").toString();
            }
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                corp_code = jsonObject.get("corp_code").toString();
                list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, "", city);
                // list = storeService.getAllStore(request, page_number, page_size, "", searchValue);
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, "", city);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    if (brand_code.equals("")) {
                        brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, "", city);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_store_code = "";
                    if (area_code.equals("")) {
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        area_store_code = request.getSession().getAttribute("store_code").toString();
                    }
                    list = storeService.selStoreByAreaBrandCity(page_number, page_size, corp_code, area_code, brand_code, searchValue, area_store_code, city);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String searchValue = jsonObject.get("searchValue").toString();
            PageInfo<Store> list;
            String brand_code = "";
            if (jsonObject.has("brand_code")) {
                brand_code = jsonObject.get("brand_code").toString();
            }
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                String area_code = jsonObject.get("area_code").toString();
                if (jsonObject.has("corp_code"))
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
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
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
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                list = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
            } else if (role_code.equals(Common.ROLE_AM)){
                String area_code = request.getSession().getAttribute("area_code").toString();
                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                String area_store_code = request.getSession().getAttribute("store_code").toString();
                list = storeService.selStoreByAreaBrandCode(corp_code,area_code,"","",area_store_code);
            } else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_SYS)){
                if (role_code.equals(Common.ROLE_SYS))
                    corp_code = "C10000";
                list = storeService.selStoreByAreaBrandCode(corp_code,"","","","");
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = storeService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                JSONObject jsonObject = new JSONObject(message);
                String store_code = jsonObject.get("store_code").toString().trim();
                String corp_code = jsonObject.get("corp_code").toString().trim();
                String isactive = jsonObject.get("isactive").toString();
                Store store = storeService.getStoreByCode(corp_code, store_code, isactive);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(store.getId()));

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
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = storeService.update(message, user_id);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Store> list = new PageInfo<Store>();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = storeService.getAllStore(request, page_number, page_size, "", search_value, "", "");
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStore(request, page_number, page_size, corp_code, search_value, "", "");
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    if (brand_code == null || brand_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属品牌");
                        return dataBean.getJsonStr();
                    } else {
                        //加上特殊字符，进行查询
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        list = storeService.selectByAreaBrand(page_number, page_size, corp_code, null, null, brandCodes, search_value, "", "");
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
                        list = storeService.selectByAreaBrand(page_number, page_size, corp_code, areaCodes, storeCodes, null, search_value, "", "");
                    }
                } else if(role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    if (store_code == null || store_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属店铺");
                        return dataBean.getJsonStr();
                    } else {
                        list = storeService.selectByUserId(page_number, page_size, store_code, corp_code, search_value);
                    }
                }else {
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);

            String search_value = "";
            String[] codes = null;
            if (jsonObject.has("searchValue")) {
                search_value = jsonObject.get("searchValue").toString();
            }
            List<Brand> brand = new ArrayList<Brand>();
            if (role_code.equals(Common.ROLE_SYS) && jsonObject.has("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
                corp_code = jsonObject.get("corp_code").toString();
                brand = brandService.getActiveBrand(corp_code, search_value, codes);
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
                if (!brand_code1.equals("")){
                    codes = brand_code1.split(",");
                    brand = brandService.getActiveBrand(corp_code, search_value, codes);
                }
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
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String store_code = jsonObject.get("store_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            List<User> user = new ArrayList<User>();
            if (role_code.equals(Common.ROLE_STAFF)) {
                //列表只显示自己
                String user_id = request.getSession().getAttribute("user_id").toString();
                User user1 = userService.getUserById(Integer.parseInt(user_id));
                user.add(user1);
            } else if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_AM)) {
                //显示导购，店长
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
            logger.info(ex.getMessage() + ex.toString());
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String store_code = jsonObject.get("store_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            List<User> user = new ArrayList<User>();
            if (role_code.equals(Common.ROLE_STAFF)) {
                //列表只显示自己
                String user_id = request.getSession().getAttribute("user_id").toString();
                User user1 = userService.getUserById(Integer.parseInt(user_id));
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);

            String corp_code = jsonObject.get("corp_code").toString();
            Store store = null;
            if (jsonObject.has("store_code")) {
                String store_code = jsonObject.get("store_code").toString();
                store = storeService.getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
            }
            if (jsonObject.has("store_id")) {
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String store_code = jsonObject.get("store_code").toString();
            String app_id = jsonObject.get("app_id").toString();
            CorpWechat corpWechat = corpService.getCorpByAppId(app_id);
            if (corpWechat != null && corpWechat.getApp_id() != null && corpWechat.getApp_id() != "") {
                String is_authorize = corpWechat.getIs_authorize();
                String auth_appid = corpWechat.getApp_id();
                if (is_authorize.equals("Y")) {
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String auth_appid = jsonObject.get("app_id").toString();
            JSONArray list = JSONArray.parseArray(jsonObject.get("list").toString());
            CorpWechat corpWechat = corpService.getCorpByAppId(auth_appid);

            for (int i = 0; i < list.size(); i++) {
                JSONObject json = new JSONObject(list.get(i).toString());
                String corp_code = json.get("corp_code").toString();
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
                        dataBean.setMessage(corp_code + "企业未授权,生成二维码中断");
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        return dataBean.getJsonStr();
                    }
                } else {
                    dataBean.setId(id);
                    dataBean.setMessage(corp_code + "企业未授权,生成二维码中断");
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
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<Store> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, "", search_value, "", "");
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = storeService.getAllStore(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "");
                    } else if (role_code.equals(Common.ROLE_BM)) {
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, null, null, brandCodes, search_value, "", "");
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
                        list = storeService.selectByAreaBrand(1, Common.EXPORTEXECLCOUNT, corp_code, areaCodes, storeCode, null, search_value, "", "");
                    } else {
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = storeService.selectByUserId(1, Common.EXPORTEXECLCOUNT, store_code, corp_code, search_value);
                    }
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", "", map, "", "");
                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "", "");
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", brand_code, "", map, "", "");
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_codes, "", "", map, store_code, "");
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", store_code, map, "", "");
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
            String pathname = OutExeclHelper.OutExecl(json, stores, map, response, request);
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
            int clos = 12;//得到所有的列
            int rows = rs.getRows();//得到所有的行
//            System.out.println("----------------clos--------------------------" + clos);
//            System.out.println("----------------rows--------------------------" + rows);
//            int actualRows = LuploadHelper.getRightRows(rs);
//            if (actualRows != rows) {
//                if (rows - actualRows == 1) {
//                    result = "：第" + rows + "行存在空白行,请删除";
//                } else {
//                    result = "：第" + (actualRows + 1) + "行至第" + rows + "存在空白行,请删除";
//                }
//                int i = 5 / 0;
//            }
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
            for (int i = 3; i < column.length; i++) {
                if (column[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Store store = storeService.getStoreByCode(column3[i].getContents().toString().trim(), column[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (store != null) {
                    result = "：第" + (i + 1) + "行店铺编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }

            for (int i = 3; i < column1.length; i++) {
                if (column1[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                List<Store> store = storeService.getStoreByName(column3[i].getContents().toString().trim(), column1[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (store.size() > 0) {
                    result = "：第" + (i + 1) + "行店铺名称已存在";
                    int b = 5 / 0;
                    break;
                }
            }
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
            for (int i = 0; i < column2.length; i++) {
                if (column2[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Store store = storeService.selStoreByStroeId(column3[i].getContents().toString().trim(), column2[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (store != null) {
                    result = "：第" + (i + 1) + "行店铺ID已存在";
                    int b = 5 / 0;
                    break;
                }
            }
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
                result = storeService.insertExecl(store);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = storeService.getAllStoreScreen(page_number, page_size, "", "", "", "", map, "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, "", "", "", map, "", "");
            } else if (role_code.equals(Common.ROLE_BM)) {
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, "", brand_code, "", map, "", "");
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_codes = request.getSession(false).getAttribute("area_code").toString();
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, area_codes, "", "", map, store_code, "");
            } else {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, "", "", store_code, map, "", "");
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
            JSONObject jsonObj = new JSONObject(jsString);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
}
