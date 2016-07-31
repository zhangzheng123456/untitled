package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    private FunctionService functionService;
    @Autowired
    private TableManagerService managerService;
    /***
     * 根据区域拉店铺
     */
    @RequestMapping(value = "/selectByAreaCode", method = RequestMethod.POST)
    @ResponseBody
    public String selectByAreaCode(HttpServletRequest request){
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
            String corp_code = jsonObject.get("corp_code").toString();
            String searchValue = jsonObject.get("searchValue").toString();
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = storeService.getAllStore(request, page_number, page_size, "", searchValue);
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStore(request, page_number, page_size, corp_code, searchValue);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String[] areaCodes = area_code.split(",");
                    for (int i = 0; i < areaCodes.length; i++) {
                        areaCodes[i] = areaCodes[i].substring(1, areaCodes[i].length());
                    }
                    list = storeService.selectByAreaCode(page_number, page_size, corp_code, areaCodes, searchValue);
                } else {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = storeService.selectByUserId(page_number, page_size, store_code, corp_code, searchValue);
                }
            }
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
    /**
     * 店铺管理
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String shopManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_id = request.getSession().getAttribute("user_id").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code, user_code, group_code, role_code, function_code);

            JSONObject result = new JSONObject();
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = storeService.getAllStore(request, page_number, page_size, "", "");
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStore(request, page_number, page_size, corp_code, "");
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String[] areaCodes = area_code.split(",");
                    for (int i = 0; i < areaCodes.length; i++) {
                        areaCodes[i] = areaCodes[i].substring(1, areaCodes[i].length());
                    }
                    list = storeService.selectByAreaCode(page_number, page_size, corp_code, areaCodes, "");
                } else {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = storeService.selectByUserId(page_number, page_size, store_code, corp_code, "");
                }
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 新增
     */
    @RequestMapping(value = "/corp_exist", method = RequestMethod.POST)
    @ResponseBody
    public String corpExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {

            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject msg = new JSONObject(message);
            String corp_code = msg.get("corp_code").toString();
            Corp corp = corpService.selectByCorpId(0, corp_code);
            if (corp == null) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该企业编号不存在");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("企业编号可用");
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
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = storeService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add success");
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
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = storeService.update(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");
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
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
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
                    List<User> user = storeService.getStoreUser(corp_code, store_code, role_code, user_id);
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
                }
                storeService.delete(Integer.valueOf(ids[i]));
            }
            if (count > 0) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            } else {
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
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String shop_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(storeService.getStoreById(Integer.parseInt(shop_id)));
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
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员

                list = storeService.getAllStore(request, page_number, page_size, "", search_value);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = storeService.getAllStore(request, page_number, page_size, corp_code, search_value);
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession().getAttribute("area_code").toString();
                String[] areaCodes = area_code.split(",");
                for (int i = 0; i < areaCodes.length; i++) {
                    areaCodes[i] = areaCodes[i].substring(1, areaCodes[i].length());
                }
                list = storeService.selectByAreaCode(page_number, page_size, corp_code, areaCodes, search_value);
            } else {
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = storeService.selectByUserId(page_number, page_size, store_code, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/brand", method = RequestMethod.POST)
    @ResponseBody
    public String getBrand(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<Brand> brand = brandService.getAllBrand(corp_code);
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

    @RequestMapping(value = "/area", method = RequestMethod.POST)
    @ResponseBody
    public String getArea(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<Area> area = areaService.getAllArea(corp_code);
            JSONArray array = new JSONArray();
            JSONObject areas = new JSONObject();
            for (int i = 0; i < area.size(); i++) {
                Area area1 = area.get(i);
                String area_code = area1.getArea_code();
                String area_name = area1.getArea_name();
                JSONObject obj = new JSONObject();
                obj.put("area_code", area_code);
                obj.put("area_name", area_name);
                array.add(obj);
            }
            areas.put("areas", array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(areas.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


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
            String user_id = request.getSession().getAttribute("user_id").toString();
            List<User> user = new ArrayList<User>();
            if (role_code.equals(Common.ROLE_STAFF)) {
                User user1 = userService.getUserById(Integer.parseInt(user_id));
                user.add(user1);
            } else {
                user = storeService.getStoreUser(corp_code, store_code, role_code, user_id);
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

    @RequestMapping(value = "/Store_CodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String Store_CodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String store_code = jsonObject.get("store_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            //         Area area = areaService.getAreaByName(corp_code, store_code);
            Store store = storeService.getStoreByCode(corp_code, store_code, "");

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


    @RequestMapping(value = "/Store_NameExist", method = RequestMethod.POST)
    @ResponseBody
    public String Store_NameExist(HttpServletRequest request) {
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
            Store store = storeService.getStoreByName(corp_code, store_name);

            if (store != null) {
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
            Corp corp = corpService.selectByCorpId(0, corp_code);
            String is_authorize = corp.getIs_authorize();
            if (corp.getApp_id() != null && corp.getApp_id() != "") {
                String auth_appid = corp.getApp_id();
                if (is_authorize.equals("Y")) {
                    String url = "http://wechat.app.bizvane.com/app/wechat/creatQrcode?auth_appid=" + auth_appid + "&prd=ishop&src=s&store_id=" + store_code;
                    String result = IshowHttpClient.get(url);
                    logger.info("------------creatQrcode  result" + result);
                    if (!result.startsWith("{")){
                        dataBean.setId(id);
                        dataBean.setMessage("生成二维码失败");
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        return dataBean.getJsonStr();
                    }
                    JSONObject obj = new JSONObject(result);
                    String picture = obj.get("picture").toString();
                    String qrcode_url = obj.get("url").toString();
                    Store store = storeService.getStoreByCode(corp_code, store_code, "");
                    store.setQrcode(picture);
                    store.setQrcode_content(qrcode_url);
                    Date now = new Date();
                    store.setModified_date(Common.DATETIME_FORMAT.format(now));
                    store.setModifier(user_id);
                    logger.info("------------creatQrcode  update store---");
                    storeService.updateStore(store);
                    dataBean.setId(id);
                    dataBean.setMessage(picture);
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
            JSONArray list = JSONArray.parseArray(jsonObject.get("list").toString());
            for (int i = 0; i < list.size(); i++) {
                JSONObject json = new JSONObject(list.get(i).toString());
                String corp_code = json.get("corp_code").toString();
                String store_code = json.get("store_code").toString();
                Corp corp = corpService.selectByCorpId(0, corp_code);
                String is_authorize = corp.getIs_authorize();
                String corp_name = corp.getCorp_name();
                if (corp.getApp_id() != null && corp.getApp_id() != "") {
                    String auth_appid = corp.getApp_id();
                    if (is_authorize.equals("Y")) {
                        String url = "http://wechat.app.bizvane.com/app/wechat/creatQrcode?auth_appid=" + auth_appid + "&prd=ishop&src=s&store_id=" + store_code;
                        String result = IshowHttpClient.get(url);
                        logger.info("------------creatQrcode  result" + result);
                        if (!result.startsWith("{")){
                            dataBean.setId(id);
                            dataBean.setMessage("生成二维码失败");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            return dataBean.getJsonStr();
                        }
                        JSONObject obj = new JSONObject(result);
                        String picture = obj.get("picture").toString();
                        String qrcode_url = obj.get("url").toString();
                        Store store = storeService.getStoreByCode(corp_code, store_code, "");
                        store.setQrcode(picture);
                        store.setQrcode_content(qrcode_url);
                        Date now = new Date();
                        store.setModified_date(Common.DATETIME_FORMAT.format(now));
                        store.setModifier(user_code);
                        logger.info("------------creatQrcode  update store");
                        storeService.updateStore(store);
                        dataBean.setId(id);
                        dataBean.setMessage("生成完成");
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        return dataBean.getJsonStr();
                    }
                }
                dataBean.setId(id);
                dataBean.setMessage(corp_name + "企业未授权");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                return dataBean.getJsonStr();
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    /***
     * 查出要导出的列
     */
    @RequestMapping(value = "/getCols", method = RequestMethod.POST)
    @ResponseBody
    public String selAllByCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String function_code = jsonObject.get("function_code").toString();
            List<TableManager> tableManagers = managerService.selAllByCode(function_code);
            JSONObject result = new JSONObject();
            result.put("tableManagers", JSON.toJSONString(tableManagers));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
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
        String errormessage = "";
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
                    list = storeService.getAllStore(request, 1, 30000, "", search_value);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = storeService.getAllStore(request, 1, 30000, corp_code, search_value);
                    } else if (role_code.equals(Common.ROLE_AM)) {
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        String[] areaCodes = area_code.split(",");
                        for (int i = 0; i < areaCodes.length; i++) {
                            areaCodes[i] = areaCodes[i].substring(1, areaCodes[i].length());
                        }
                        list = storeService.selectByAreaCode(1, 30000, corp_code, areaCodes, search_value);
                    } else {
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = storeService.selectByUserId(1, 30000, store_code, corp_code, search_value);
                    }
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeService.getAllStoreScreen(1, 30000, "", "", "", map);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStoreScreen(1, 30000, corp_code, "", "", map);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeService.getAllStoreScreen(1, 30000, corp_code, area_codes, "", map);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen(1, 30000, corp_code, "", store_code, map);
                }
            }
            List<Store> stores = list.getList();
            if (stores.size() >= 29999) {
                errormessage = "：导出数据过大";
                int i = 9 / 0;
            }
            Map<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(stores, map, response, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "：数据异常，导出失败";
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
            int clos = rs.getColumns();//得到所有的列
            int rows = rs.getRows();//得到所有的行
            if(rows<4){
                result="：请从模板第4行开始插入正确数据";
                int i=5/0;
            }
            if (rows > 9999) {
                result = "：数据量过大，导入失败";
                int i = 5 / 0;
            }
            Cell[] column3 = rs.getColumn(0);
            Pattern pattern1 = Pattern.compile("C\\d{5}");
            if (!role_code.equals(Common.ROLE_SYS)) {
                for (int i = 3; i < column3.length; i++) {
                    if (!column3[i].getContents().toString().equals(corp_code)) {
                        result = "：第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString());
                    if (matcher.matches() == false) {
                        result = "：第" + (i + 1) + "行企业编号格式不对";
                        int b = 5 / 0;
                        break;
                    }

                }
            }
            for (int i = 3; i < column3.length; i++) {
                Matcher matcher = pattern1.matcher(column3[i].getContents().toString());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行企业编号格式不对";
                    int b = 5 / 0;
                    break;
                }
                Corp corp = corpService.selectByCorpId(0, column3[i].getContents().toString());
                if (corp == null) {
                    result = "：第" + (i + 1) + "行企业编号不存在";
                    int b = 5 / 0;
                    break;
                }

            }
            Cell[] column = rs.getColumn(1);
            for (int i = 3; i < column.length; i++) {
                Store store = storeService.getStoreByCode(column3[i].getContents().toString(), column[i].getContents().toString(), "");
                if (store != null) {
                    result = "：第" + (i + 1) + "行店铺编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column1 = rs.getColumn(2);
            for (int i = 3; i < column1.length; i++) {
                Store store = storeService.getStoreByName(column3[i].getContents().toString(), column1[i].getContents().toString());
                if (store != null) {
                    result = "：第" + (i + 1) + "行店铺名称已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Pattern pattern7 = Pattern.compile("A\\d{4}");
            Cell[] column7 = rs.getColumn(3);
            for (int i = 3; i < column7.length; i++) {
                Matcher matcher = pattern7.matcher(column7[i].getContents().toString());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行区域编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Area area = areaService.getAreaByCode(column3[i].getContents().toString(), column7[i].getContents().toString());
                if (area == null) {
                    result = "：第" + (i + 1) + "行区域编号不存在";
                    int b = 5 / 0;
                    break;
                }
            }
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    Store store = new Store();
                    store.setCorp_code(rs.getCell(j++, i).getContents());
                    store.setStore_code(rs.getCell(j++, i).getContents());
                    store.setStore_name(rs.getCell(j++, i).getContents());
                    store.setArea_code(rs.getCell(j++, i).getContents());
                    store.setBrand_code(rs.getCell(j++, i).getContents());
                    if (rs.getCell(j++, i).getContents().toString().toUpperCase().equals("N")) {
                        store.setFlg_tob("N");
                    } else {
                        store.setFlg_tob("Y");
                    }
                    if (rs.getCell(j++, i).getContents().toString().toUpperCase().equals("N")) {
                        store.setIsactive("N");
                    } else {
                        store.setIsactive("Y");
                    }
                    Date now = new Date();
                    store.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    store.setCreater(user_id);
                    store.setModified_date(Common.DATETIME_FORMAT.format(now));
                    store.setModifier(user_id);
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
//            String screen = jsonObject.get("screen").toString();
//            JSONObject jsonScreen = new JSONObject(screen);
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = storeService.getAllStoreScreen(page_number, page_size, "", "", "", map);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, "", "", map);
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_codes = request.getSession(false).getAttribute("area_code").toString();
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, area_codes, "", map);
            } else {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeService.getAllStoreScreen(page_number, page_size, corp_code, "", store_code, map);
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
}
