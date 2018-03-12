//package com.bizvane.ishop.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.bizvane.ishop.bean.DataBean;
//import com.bizvane.ishop.constant.Common;
//import com.bizvane.ishop.constant.CommonValue;
//import com.bizvane.ishop.entity.*;
//import com.bizvane.ishop.service.*;
//import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
//import com.bizvane.ishop.utils.CheckUtils;
//import com.bizvane.ishop.utils.MongoUtils;
//import com.bizvane.ishop.utils.OutExeclHelper;
//import com.bizvane.ishop.utils.WebUtils;
//import com.bizvane.sun.common.service.mongodb.MongoDBClient;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.pagehelper.PageInfo;
//import com.mongodb.*;
//import org.bson.types.ObjectId;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.*;
//
///**
// * Created by yin on 2016/8/24.
// */
//@Controller
//@RequestMapping("/apploginlog")
//public class AppLoginLogController {
//    @Autowired
//    private AppLoginLogService loginLogService;
//    @Autowired
//    private StoreService storeService;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private BrandService brandService;
//    @Autowired
//    private BaseService baseService;
//    @Autowired
//    MongoDBClient mongodbClient;
//    String id;
//
////    /**
////     * 列表
////     * @param request
////     * @return
////     */
////    @RequestMapping(value = "/list", method = RequestMethod.GET)
////    @ResponseBody
////    public String selectAll(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        try {
////            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
////            int page_size = Integer.parseInt(request.getParameter("pageSize"));
////            JSONObject result = new JSONObject();
////            String corp_code = request.getSession().getAttribute("corp_code").toString();
////            String role_code = request.getSession().getAttribute("role_code").toString();
////            PageInfo<AppLoginLog> list=new PageInfo<AppLoginLog>();
////            //系统管理员，不传企业编号
////            if (role_code.equals(Common.ROLE_SYS)) {
////                list = loginLogService.selectAllAppLoginLog(page_number, page_size, "", "");
////            }else {
////                list = loginLogService.selectAllAppLoginLog(page_number, page_size, corp_code, "");
////            }
////            result.put("list", JSON.toJSONString(list));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId(id);
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            ex.printStackTrace();
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId(id);
////            dataBean.setMessage(ex.getMessage());
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//
//
////    //条件查询
////    @RequestMapping(value = "/search", method = RequestMethod.POST)
////    @ResponseBody
////    public String search(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        try {
////            String jsString = request.getParameter("param");
////            JSONObject jsonObj = new JSONObject(jsString);
////            id = jsonObj.get("id").toString();
////            String message = jsonObj.get("message").toString();
////            JSONObject jsonObject = new JSONObject(message);
////            String corp_code = request.getSession().getAttribute("corp_code").toString();
////            String role_code = request.getSession().getAttribute("role_code").toString();
////            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
////            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
////            String search_value = jsonObject.get("searchValue").toString();
////            JSONObject result = new JSONObject();
////            PageInfo<AppLoginLog> list=new PageInfo<AppLoginLog>();
////            if (role_code.equals(Common.ROLE_SYS)) {
////                list = loginLogService.selectAllAppLoginLog(page_number, page_size, "", search_value);
////            }else {
////                list = loginLogService.selectAllAppLoginLog(page_number, page_size, corp_code, search_value);
////            }
////            result.put("list", JSON.toJSONString(list));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId(id);
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            ex.printStackTrace();
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId(id);
////            dataBean.setMessage(ex.getMessage());
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//
////    /**
////     * 筛选
////     *
////     * @param request
////     * @return
////     */
////    @RequestMapping(value = "/screen", method = RequestMethod.POST)
////    @ResponseBody
////    public String selectByScreen(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        try {
////            String jsString = request.getParameter("param");
////            JSONObject jsonObj = new JSONObject(jsString);
////            id = jsonObj.get("id").toString();
////            String message = jsonObj.get("message").toString();
////            JSONObject jsonObject = new JSONObject(message);
////            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
////            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
////            // String type = jsonObject.get("type").toString();
////            Map<String, String> map = WebUtils.Json2Map(jsonObject);
////            String role_code = request.getSession().getAttribute("role_code").toString();
////            String corp_code = request.getSession().getAttribute("corp_code").toString();
////            JSONObject result = new JSONObject();
////            PageInfo<AppLoginLog> list = new PageInfo<AppLoginLog>();
////            if (role_code.equals(Common.ROLE_SYS)) {
////                list = loginLogService.selectAllScreen(page_number, page_size, "", map);
////            } else {
////                list = loginLogService.selectAllScreen(page_number, page_size, corp_code, map);
////            }
////            result.put("list", JSON.toJSONString(list));
////            dataBean.setId(id);
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            ex.printStackTrace();
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId(id);
////            dataBean.setMessage(ex.getMessage() + ex.toString());
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//
////    /**
////     * 删除(用了事务)
////     */
////    @RequestMapping(value = "/delete", method = RequestMethod.POST)
////    @ResponseBody
////    @Transactional
////    public String delete(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        try {
////            String jsString = request.getParameter("param");
////            JSONObject jsonObj = new JSONObject(jsString);
////            id = jsonObj.get("id").toString();
////            String message = jsonObj.get("message").toString();
////            JSONObject jsonObject = new JSONObject(message);
////            String app_id = jsonObject.get("id").toString();
////            String[] ids = app_id.split(",");
////            for (int i = 0; i < ids.length; i++) {
////                AppLoginLog appLoginLog = loginLogService.selByLogId(Integer.valueOf(ids[i]));
////                loginLogService.delAppLoginlogById(Integer.valueOf(ids[i]));
////                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////                dataBean.setId(id);
////                dataBean.setMessage("success");
////
////                //----------------行为日志开始------------------------------------------
////                /**
////                 * mongodb插入用户操作记录
////                 * @param operation_corp_code 操作者corp_code
////                 * @param operation_user_code 操作者user_code
////                 * @param function 功能
////                 * @param action 动作
////                 * @param corp_code 被操作corp_code
////                 * @param code 被操作code
////                 * @param name 被操作name
////                 * @throws Exception
////                 */
////                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
////                String operation_user_code = request.getSession().getAttribute("user_code").toString();
////                String function = "员工管理_登录日志";
////                String action = Common.ACTION_DEL;
////                String t_corp_code = appLoginLog.getCorp_code();
////                String t_code = appLoginLog.getUser_code();
////                String t_name = appLoginLog.getUser_name();
////                String remark = appLoginLog.getTime() + "(" + appLoginLog.getPlatform() + ")";
////                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
////            }
////        } catch (Exception ex) {
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId(id);
////            dataBean.setMessage(ex.getMessage());
////            return dataBean.getJsonStr();
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//
//
////    /***
////     * 导出数据
////     */
////    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
////    @ResponseBody
////    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
////        DataBean dataBean = new DataBean();
////        String errormessage = "数据异常，导出失败";
////        try {
////            String jsString = request.getParameter("param");
////             JSONObject jsonObj = JSONObject.parseObject(jsString);
////            String message = jsonObj.get("message").toString();
////             JSONObject jsonObject = JSONObject.parseObject(message);
////            //系统管理员(官方画面)
////            String corp_code = request.getSession().getAttribute("corp_code").toString();
////            String role_code = request.getSession().getAttribute("role_code").toString();
////            String search_value = jsonObject.get("searchValue").toString();
////            String screen = jsonObject.get("list").toString();
////            //String type = jsonObject.get("type").toString();
////            PageInfo<AppLoginLog> pageInfo = null;
////            if (screen.equals("")) {
////                if (role_code.equals(Common.ROLE_SYS)) {
////                    pageInfo = loginLogService.selectAllAppLoginLog(1, 30000, "", search_value);
////                } else {
////                    pageInfo = loginLogService.selectAllAppLoginLog(1, 30000, corp_code, search_value);
////                }
////            } else {
////                Map<String, String> map = WebUtils.Json2Map(jsonObject);
////                if (role_code.equals(Common.ROLE_SYS)) {
////                    pageInfo = loginLogService.selectAllScreen(1, 30000, "", map);
////                } else {
////                    pageInfo = loginLogService.selectAllScreen(1, 30000, corp_code, map);
////                }
////            }
////            List<AppLoginLog> appLoginLogs = pageInfo.getList();
////            if (appLoginLogs.size() >= 29999) {
////                errormessage = "导出数据过大";
////                int i = 9 / 0;
////            }
//////            List<AppLoginLog> appLoginLogs1 = new ArrayList<AppLoginLog>();
//////            for (AppLoginLog appLoginLog:appLoginLogs) {
//////                appLoginLog.setIsactive(CheckUtils.CheckIsactive(appLoginLog.getIsactive()));
//////                appLoginLog.setBrand_name("");
//////                if (appLoginLog.getStore_name() != null && !appLoginLog.getStore_name().equals("") && appLoginLog.getCorp_code() != null
//////                        && !appLoginLog.getCorp_code().equals("")) {
//////                    List<Store> stores = storeService.getStoreByName(appLoginLog.getCorp_code(), appLoginLog.getStore_name(), Common.IS_ACTIVE_Y);
//////                    if (stores.size() > 0) {
//////                        String brand_code = stores.get(0).getBrand_code();
//////                        if (brand_code != null && !brand_code.equals("")) {
//////                            brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//////                            String[] ids = brand_code.split(",");
//////                            String brand_name = "";
//////                            for (int i = 0; i < ids.length; i++) {
//////                                Brand brand = brandService.getBrandByCode(appLoginLog.getCorp_code(), ids[i], Common.IS_ACTIVE_Y);
//////                                if (brand != null) {
//////                                    String brand_name1 = brand.getBrand_name();
//////                                    brand_name = brand_name + brand_name1 + "、";
//////                                }
//////                            }
//////                            if (brand_name.endsWith("、"))
//////                                brand_name = brand_name.substring(0, brand_name.length() - 1);
//////                            appLoginLog.setBrand_name(brand_name);
//////                        }
//////                    }
//////                }
//////                appLoginLogs1.add(appLoginLog);
//////            }
////            ObjectMapper mapper = new ObjectMapper();
////            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
////            String json = mapper.writeValueAsString(appLoginLogs);
////
////
////            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
////            String pathname = OutExeclHelper.OutExecl(json, appLoginLogs, map, response, request);
////            JSONObject result = new JSONObject();
////            if (pathname == null || pathname.equals("")) {
////                errormessage = "数据异常，导出失败";
////                int a = 8 / 0;
////            }
////            result.put("path", JSON.toJSONString("lupload/" + pathname));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId(id);
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            ex.printStackTrace();
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId("-1");
////            dataBean.setMessage(errormessage);
////        }
////        return dataBean.getJsonStr();
////    }
//}
