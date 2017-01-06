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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.json.JSONObject;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/brand")
public class BrandController {

    String id;

    @Autowired
    private BrandService brandService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private BaseService baseService;
    private static final Logger logger = Logger.getLogger(BrandController.class);

    /**
     * session企业拉取品牌
     * (包含全部)
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/findBrandByCorpCode", method = RequestMethod.POST)
    @ResponseBody
    public String findBrandByCorpCode(HttpServletRequest request) {
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
            List<Brand> brandList = new ArrayList<Brand>();

            if (role_code.equals(Common.ROLE_SYS) && jsonObject.has("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
                corp_code = jsonObject.get("corp_code").toString();
                List<Brand> brand = brandService.getActiveBrand(corp_code, search_value, codes);
                Brand brand_new = new Brand();
                brand_new.setBrand_code("");
                brand_new.setBrand_name("全部");
                brand_new.setCorp_code("");
                brand_new.setCorp_name("");
                brand_new.setCreated_date("");
                brand_new.setCreater("");
                brand_new.setId(0);
                brandList.add(0, brand_new);
                brandList.addAll(brand);
            } else if (role_code.equals(Common.ROLE_GM)) {
                List<Brand> brand = brandService.getActiveBrand(corp_code, search_value, codes);
                Brand brand_new = new Brand();
                brand_new.setBrand_code("");
                brand_new.setBrand_name("全部");
                brand_new.setCorp_code("");
                brand_new.setCorp_name("");
                brand_new.setCreated_date("");
                brand_new.setCreater("");
                brand_new.setId(0);
                brandList.add(0, brand_new);
                brandList.addAll(brand);
            } else if (role_code.equals(Common.ROLE_BM)) {
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                codes = brand_code.split(",");
                List<Brand> brand = brandService.getActiveBrand(corp_code, search_value, codes);
                brandList.addAll(brand);
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
                    brandList = brandService.getActiveBrand(corp_code, search_value, codes);
                }
            } else if (role_code.equals(Common.ROLE_STAFF) || role_code.equals(Common.ROLE_SM)) {
                List<Store> stores = storeService.selectAll(store_code, corp_code, Common.IS_ACTIVE_Y);
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
                    brandList = brandService.getActiveBrand(corp_code, search_value, codes);
                }
            }

            JSONArray array = new JSONArray();
            JSONObject brands = new JSONObject();
            for (int i = 0; i < brandList.size(); i++) {
                Brand brand1 = brandList.get(i);
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


//    /**
//     * 品牌列表
//     */
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String brandManage(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            JSONObject result = new JSONObject();
//            PageInfo<Brand> list = new PageInfo<Brand>();
//            if (role_code.equals(Common.ROLE_SYS)) {
//                //系统管理员
//                list = brandService.getAllBrandByPage(page_number, page_size, "", "");
//            } else if (role_code.equals(Common.ROLE_BM)) {
//                String brand_code = request.getSession().getAttribute("brand_code").toString();
//                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                String[] codes = brand_code.split(",");
//                list = brandService.getPartBrandByPage(page_number, page_size, corp_code, codes, "");
//            } else {
//                list = brandService.getAllBrandByPage(page_number, page_size, corp_code, "");
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 品牌新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addBrand(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--brand add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            String result = brandService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
                String brand_code = jsonObject.get("brand_code").toString().trim();
                String corp_code = jsonObject.get("corp_code").toString().trim();
                String isactive = jsonObject.get("isactive").toString();
                Brand brand = brandService.getBrandByCode(corp_code, brand_code, isactive);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(brand.getId()));

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
                String function = "品牌管理";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("brand_code").toString();
                String t_name = action_json.get("brand_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 品牌编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editBrand(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--brand edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = brandService.update(message, user_id);
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
                String function = "品牌管理";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("brand_code").toString();
                String t_name = action_json.get("brand_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
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
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json-- delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String brand_id = jsonObject.get("id").toString();
            String[] ids = brand_id.split(",");
            String msg = null;
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                Brand brand = brandService.getBrandById(Integer.valueOf(ids[i]));
                if (brand != null) {
                    logger.info("------------得到brand" + brand.getId());
                    String brand_code = brand.getBrand_code();
                    String corp_code = brand.getCorp_code();
                    int count = 0;
                    count = brandService.getGoodsCount(corp_code, brand_code);
                    if (count > 0) {
                        msg = "有使用品牌" + brand_code + "的商品，请先处理品牌下商品再删除";
                        break;
                    }
                    count = storeService.selectStoreCountByBrand(corp_code, brand_code, "", "").size();
                    if (count > 0) {
                        msg = "有使用品牌" + brand_code + "的店铺，请先处理品牌下店铺再删除";
                        break;
                    }
                }
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
                String function = "品牌管理";
                String action = Common.ACTION_DEL;
                String t_corp_code = brand.getCorp_code();
                String t_code = brand.getBrand_code();
                String t_name = brand.getBrand_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
            if (msg == null) {
                for (int i = 0; i < ids.length; i++) {
                    brandService.delete(Integer.valueOf(ids[i]));
                }
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("删除成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 品牌管理
     * 选择品牌
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");

            logger.info("json-select-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(brandService.getBrandById(Integer.parseInt(user_id)));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("品牌信息异常");
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
            logger.info("json---------------" + jsString);
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
            PageInfo<Brand> list = new PageInfo<Brand>();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = brandService.getAllBrandByPage(page_number, page_size, "", search_value);
            } else if(role_code.equals(Common.ROLE_GM)){
                list = brandService.getAllBrandByPage(page_number, page_size, corp_code, "");
            } else if (role_code.equals(Common.ROLE_BM)) {
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                String[] codes = brand_code.split(",");
                list = brandService.getPartBrandByPage(page_number, page_size, corp_code, codes, search_value);
            } else if (role_code.equals(Common.ROLE_AM)){
                list = brandService.getAllBrandByPage(page_number, page_size, corp_code, search_value);
            } else {
                List<Brand> list1 = new ArrayList<Brand>();
                list.setList(list1);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/brandCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String brandCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String brand_code = jsonObject.get("brand_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Brand brand = brandService.getBrandByCode(corp_code, brand_code, Common.IS_ACTIVE_Y);
            if (brand != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("品牌编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("品牌编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/brandNameExist", method = RequestMethod.POST)
    @ResponseBody
    public String brandNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String brand_name = jsonObject.get("brand_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Brand brand = brandService.getBrandByName(corp_code, brand_name, Common.IS_ACTIVE_Y);
            if (brand != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("品牌名称已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("品牌名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
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
            PageInfo<Brand> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = brandService.getAllBrandByPage(1, Common.EXPORTEXECLCOUNT, "", search_value);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    String[] codes = brand_code.split(",");
                    list = brandService.getPartBrandByPage(1, Common.EXPORTEXECLCOUNT, corp_code, codes, search_value);
                } else {
                    list = brandService.getAllBrandByPage(1, Common.EXPORTEXECLCOUNT, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = brandService.getAllBrandScreen(1, Common.EXPORTEXECLCOUNT, "", null, map);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    String[] codes = brand_code.split(",");
                    list = brandService.getAllBrandScreen(1, Common.EXPORTEXECLCOUNT, corp_code, codes, map);
                } else {
                    list = brandService.getAllBrandScreen(1, Common.EXPORTEXECLCOUNT, corp_code, null, map);
                }
            }
            List<Brand> brands = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(brands);
            if (brands.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json, brands, map, response, request);
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
        }
        return dataBean.getJsonStr();
    }

    /***
     * Execl增加
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String result = "";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        Workbook rwb = null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = 4;//得到所有的列
            int rows = rs.getRows();//得到所有的行
            //      int actualRows = LuploadHelper.getRightRows(rs);
//            if(actualRows != rows){
//                if(rows-actualRows==1){
//                    result = "：第"+rows+"行存在空白行,请删除";
//                }else{
//                    result = "：第"+(actualRows+1)+"行至第"+rows+"存在空白行,请删除";
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
            Cell[] column3 = rs.getColumn(0);
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

            String onlyCell1 = LuploadHelper.CheckOnly(rs.getColumn(1));
            if (onlyCell1.equals("存在重复值")) {
                result = "：Execl中品牌编号存在重复值";
                int b = 5 / 0;
            }
            String onlyCell2 = LuploadHelper.CheckOnly(rs.getColumn(2));
            if (onlyCell2.equals("存在重复值")) {
                result = "：Execl中品牌名称存在重复值";
                int b = 5 / 0;
            }
            //   Pattern pattern = Pattern.compile("B\\d{4}");
            Cell[] column = rs.getColumn(1);
            for (int i = 3; i < column.length; i++) {
                if (column[i].getContents().toString().trim().equals("")) {
                    continue;
                }
//                Matcher matcher = pattern.matcher(column[i].getContents().toString().trim());
//                if (matcher.matches() == false) {
//                    result = "：第" + (i + 1) + "行品牌编号格式有误";
//                    int b = 5 / 0;
//                    break;
//                }
                Brand brand = brandService.getBrandByCode(column3[i].getContents().toString().trim(), column[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (brand != null) {
                    result = "：第" + (i + 1) + "行品牌编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column1 = rs.getColumn(2);
            for (int i = 3; i < column1.length; i++) {
                if (column1[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Brand brand = brandService.getBrandByName(column3[i].getContents().toString().trim(), column1[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (brand != null) {
                    result = "：第" + (i + 1) + "行品牌名称已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            ArrayList<Brand> brands = new ArrayList<Brand>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    Brand brand = new Brand();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    String brand_code = rs.getCell(j++, i).getContents().toString().trim();
                    String brand_name = rs.getCell(j++, i).getContents().toString().trim();
                    String isactive = rs.getCell(j++, i).getContents().toString().trim();
//                    if(cellCorp.equals("")  && brand_code.equals("") && brand_code.equals("") && isactive.equals("")){
//                        result = "：第"+(i+1)+"行存在空白行,请删除";
//                        int a=5/0;
//                    }
                    if (cellCorp.equals("") && brand_code.equals("") && brand_name.equals("")) {
                        continue;
                    }
                    if (cellCorp.equals("") || brand_code.equals("") || brand_name.equals("")) {
                        result = "：第" + (i + 1) + "行信息不完整,请参照Execl中对应的批注";
                        int a = 5 / 0;
                    }
                    if (!role_code.equals(Common.ROLE_SYS)) {
                        brand.setCorp_code(corp_code);
                    } else {
                        brand.setCorp_code(cellCorp);
                    }
                    brand.setBrand_code(brand_code);
                    brand.setBrand_name(brand_name);
                    if (isactive.toUpperCase().equals("N")) {
                        brand.setIsactive("N");
                    } else {
                        brand.setIsactive("Y");
                    }
                    Date now = new Date();
                    brand.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    brand.setCreater(user_id);
                    brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                    brand.setModifier(user_id);
                    brands.add(brand);
                    //  result = brandService.insertExecl(brand);
                }
            }
            for (Brand brand : brands) {
                result = brandService.insertExecl(brand);
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
     * 品牌筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
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
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Brand> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = brandService.getAllBrandScreen(page_number, page_size, "", null, map);
            } else if (role_code.equals(Common.ROLE_BM)) {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                String[] codes = brand_code.split(",");
                list = brandService.getAllBrandScreen(page_number, page_size, corp_code, codes, map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = brandService.getAllBrandScreen(page_number, page_size, corp_code, null, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


//    /**
//     * 获取所选品牌下的店铺
//     */
//    @RequestMapping(value = "/getStores", method = RequestMethod.POST)
//    @ResponseBody
//    public String getStores(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("json---------------" + jsString);
//            JSONObject jsonObj = new JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = new JSONObject(message);
//            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
//            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String corp_code = jsonObject.get("corp_code").toString();
//            String brand_code = jsonObject.get("brand_code").toString();
//            String search_value = jsonObject.get("search_value").toString();
//            PageInfo<Store> stores = storeService.selectStoreByBrand(page_number,page_size,corp_code,brand_code,search_value,Common.IS_ACTIVE_Y);
//            JSONObject result = new JSONObject();
//            result.put("list", JSON.toJSONString(stores));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage() + ex.toString());
//        }
//        return dataBean.getJsonStr();
//    }
}
