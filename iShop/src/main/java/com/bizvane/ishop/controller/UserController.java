package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Created by maoweidong on 2016/2/15.
 */

/*
*用户管理
*/
@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    @Autowired
    private UserService userService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private SignService signService;
    @Autowired
    private BrandService brandService;
    @Autowired
    WeimobService weimobService;
    @Autowired
    private BaseService baseService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    String id;

    /***
     * 根据企业，区域，店铺（必填）
     * 拉取员工
     */
    @RequestMapping(value = "/selectByPart", method = RequestMethod.POST)
    @ResponseBody
    public String selectByPart(HttpServletRequest request, HttpServletResponse response) {
        String role_code = request.getSession().getAttribute("role_code").toString();
        int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());

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
            String corp_code = jsonObject.get("corp_code").toString();
            String searchValue = jsonObject.get("searchValue").toString();
            String area = jsonObject.get("area_code").toString();
            String[] areas = area.split(",");
            PageInfo<User> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                String store_code = jsonObject.get("store_code").toString();
                list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, areas, Common.ROLE_STAFF);
            } else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_BM)) {
                String store_code = jsonObject.get("store_code").toString();
                list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, areas, Common.ROLE_STAFF);
                List<User> users = list.getList();
                if (page_number == 1) {
                    User self = userService.getUserById(user_id);
                    users.add(self);
                }
            } else if (role_code.equals(Common.ROLE_STAFF)) {
                User user = userService.getUserById(user_id);
                List<User> users = new ArrayList<User>();
                users.add(user);
                list = new PageInfo<User>();
                list.setList(users);
            } else if (role_code.equals(Common.ROLE_SM)) {
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, null, Common.ROLE_STAFF);
                if (page_number == 1) {
                    List<User> users = list.getList();
                    User self = userService.getUserById(user_id);
                    users.add(self);
                }
            } else if (role_code.equals(Common.ROLE_AM)) {
                String store_code = jsonObject.get("store_code").toString();
                list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, null, Common.ROLE_STAFF);
                if (page_number == 1) {
                    List<User> users = list.getList();
                    User self = userService.getUserById(user_id);
                    users.add(self);
                }
            }
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
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

    /***
     * 根据企业，店铺拉取员工
     */
    @RequestMapping(value = "/selectUsersByRole", method = RequestMethod.POST)
    @ResponseBody
    public String selectUsersByRole(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());

        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String searchValue = jsonObject.get("searchValue").toString();
            String user_name = request.getSession().getAttribute("user_name").toString();

            String area_code = "";
            String store_code = "";
            String brand_code = "";
            PageInfo<User> list = null;
            if (jsonObject.has("brand_code") && !jsonObject.get("brand_code").equals("")){
                brand_code = jsonObject.get("brand_code").toString();
            }
            if (jsonObject.has("area_code") && !jsonObject.get("area_code").equals("")){
                area_code = jsonObject.get("area_code").toString();
            }
            if (jsonObject.has("store_code") && !jsonObject.get("store_code").equals("")){
                store_code = jsonObject.get("store_code").toString();
            }
            if (role_code.equals(Common.ROLE_SYS)) {
                if (jsonObject.has("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
                    corp_code = jsonObject.get("corp_code").toString();
                }
                if (!store_code.equals("")) {
//                    String[] areas = area_code.split(",");
                    list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, null, Common.ROLE_STAFF);
                }else {
                    if(!area_code.equals("") || !brand_code.equals("")){
                        //拉取区域下所有员工（包括区经）
                        String[] areas = null;
                        if (!area_code.equals("")){
                            areas = area_code.split(",");
                        }
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,area_code,brand_code,searchValue,"");
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                        list = userService.selectUsersByRole(page_number, page_size, corp_code, searchValue, store_code, "",areas, "");
                    }else {
                        list = userService.selectUsersByRole(page_number, page_size, corp_code, searchValue, store_code, area_code,null, "");
                    }
                }
            } else if (role_code.equals(Common.ROLE_GM)) {
                if (!store_code.equals("")) {
                    list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, null, Common.ROLE_STAFF);
                }else {
                    if(!area_code.equals("") || !brand_code.equals("")){
                        //拉取区域下所有员工（包括区经）
                        String[] areas = area_code.split(",");
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,area_code,brand_code,searchValue,"");
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                        list = userService.selectUsersByRole(page_number, page_size, corp_code, searchValue, store_code, "",areas, "");
                    }else {
                        list = userService.selectUsersByRole(page_number, page_size, corp_code, searchValue, store_code, area_code,null, "");
                    }
                }
            } else if (role_code.equals(Common.ROLE_STAFF)) {
                User user = userService.getUserById(user_id);
                List<User> users = new ArrayList<User>();
                users.add(user);
                list = new PageInfo<User>();
                list.setList(users);
            } else if (role_code.equals(Common.ROLE_SM)) {

                if (jsonObject.has("store_code") && !jsonObject.get("store_code").equals("")){
                    store_code = jsonObject.get("store_code").toString();
                }else {
                    store_code = request.getSession().getAttribute("store_code").toString();
                    store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                }
                list = userService.selectUsersByRole(page_number, page_size, corp_code, searchValue, store_code, "",null,role_code);
                if (page_number == 1 && (searchValue.equals("") || user_name.contains(searchValue))) {
                    List<User> users = list.getList();
                    User self = userService.getUserById(user_id);
                    users.add(self);
                }
            } else if (role_code.equals(Common.ROLE_AM)) {
                if (jsonObject.has("area_code") && !jsonObject.get("area_code").equals("")){
                    area_code = jsonObject.get("area_code").toString();
                }else {
                    area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                }
                if (jsonObject.has("store_code") && !jsonObject.get("store_code").equals("")){
                    store_code = jsonObject.get("store_code").toString();
                }
                if (!store_code.equals("")){
                    list = userService.selectUsersByRole(page_number, page_size, corp_code, searchValue, store_code, "",null,role_code);
                }else {
                    list = userService.selectUsersByRole(page_number, page_size, corp_code, searchValue, store_code, area_code, null, role_code);
                    if (page_number == 1 && (searchValue.equals("") || user_name.contains(searchValue))) {

                        List<User> users = list.getList();
                        User self = userService.getUserById(user_id);
                        users.add(self);
                    }
                }
            }else if (role_code.equals(Common.ROLE_BM)) {
                if (jsonObject.has("brand_code") && !jsonObject.get("brand_code").equals("")){
                    brand_code = jsonObject.get("brand_code").toString();
                }else {
                    brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                }
                if (jsonObject.has("area_code") && !jsonObject.get("area_code").equals("")) {
                    area_code = jsonObject.get("area_code").toString();
                }
                if (jsonObject.has("store_code") && !jsonObject.get("store_code").equals("")) {
                    store_code = jsonObject.get("store_code").toString();
                }else {
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,"",brand_code,"","");
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + stores.get(i).getStore_code() + ",";
                    }
                }
                if (area_code.equals("")) {
                    list = userService.selectUsersByRole(page_number, page_size, corp_code, searchValue, store_code, area_code, null, role_code);
                }else {
                    String[] area_codes = area_code.split(",");
                    String[] store_codes = store_code.split(",");
                    List<Store> stores = storeService.selectByAreaBrand(corp_code, area_codes,null, null,Common.IS_ACTIVE_Y);
                    String new_store = "";
                    for (int i = 0; i < store_codes.length; i++) {
                        String store_code1 = store_codes[i];
                        for (int j = 0; j < stores.size(); j++) {
                            if (store_code1.equals(stores.get(j).getStore_code())){
                                new_store = new_store + store_code1 + ",";
                            }
                        }
                    }
                    list = userService.selectUsersByRole(page_number, page_size, corp_code, searchValue, new_store, "", null, role_code);
                }
            }
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
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

    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<User> list = null;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = userService.selectBySearch(request, 1, Common.EXPORTEXECLCOUNT, "", search_value);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    //系统管理员
                    list = userService.selectBySearch(request, 1, Common.EXPORTEXECLCOUNT, corp_code, search_value);
                } else if (role_code.equals(Common.ROLE_STAFF)) {
                    //员工
                    User user = userService.getUserById(user_id);
                    List<User> users = new ArrayList<User>();
                    users.add(user);
                    list = new PageInfo<User>();
                    list.setList(users);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = userService.selectBySearchPart(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, store_code,"", "", role_code);
                    List<User> users = list.getList();
                    User self = userService.getUserById(user_id);
                    users.add(self);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String area_store = request.getSession().getAttribute("store_code").toString();
                    list = userService.selectBySearchPart(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "",area_store, area_code, role_code);
                    List<User> users = list.getList();
                    User self = userService.getUserById(user_id);
                    users.add(self);
                }else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
                    String store_code = "";
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    list = userService.selectBySearchPart(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, store_code,"", "", role_code);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = userService.getAllUserScreen(1, Common.EXPORTEXECLCOUNT, "", map);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        //企业管理员
                        list = userService.getAllUserScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map);
                    } else if (role_code.equals(Common.ROLE_SM)) {
                        //店长
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = userService.getScreenPart(1, Common.EXPORTEXECLCOUNT, corp_code, map, store_code, "","", role_code);
                    } else if (role_code.equals(Common.ROLE_AM)) {
                        //区经
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        String area_store = request.getSession().getAttribute("store_code").toString();
                        list = userService.getScreenPart(1, Common.EXPORTEXECLCOUNT, corp_code, map, "", area_store,area_code, role_code);
                    }else if (role_code.equals(Common.ROLE_BM)){
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,"",brand_code,"","");
                        String store_code = "";
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code +  Common.SPECIAL_HEAD +stores.get(i).getStore_code() + ",";
                        }
                        list = userService.getScreenPart(1, Common.EXPORTEXECLCOUNT, corp_code, map, store_code,"", "", role_code);
                    }
                }

            }
            List<User> users = list.getList();
            if (users.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            for (User user : users) {
                String areaCode = user.getArea_code();
                String replaceStr = WebUtils.StringFilter(areaCode);
                user.setArea_code(replaceStr);
                String store_code = user.getStore_code();
                String replaceStore = WebUtils.StringFilter(store_code);
                user.setStore_code(replaceStore);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(users);
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, users, map, response, request);
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
            dataBean.setId(id);
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();

    }

    /**
     * 用户管理
     */
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String userManage(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            JSONObject result = new JSONObject();
//            PageInfo<User> list = null;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                //系统管理员
//                list = userService.selectBySearch(request, page_number, page_size, "", "");
//            } else if (role_code.equals(Common.ROLE_GM)) {
//                //系统管理员
//                list = userService.selectBySearch(request, page_number, page_size, corp_code, "");
//            } else if (role_code.equals(Common.ROLE_SM)) {
//                //店长
//                String store_code = request.getSession().getAttribute("store_code").toString();
//                if (store_code == null || store_code.equals("")) {
//                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                    dataBean.setId("1");
//                    dataBean.setMessage("您还没有所属店铺");
//                    return dataBean.getJsonStr();
//                } else {
//                    list = userService.selectBySearchPart(page_number, page_size, corp_code, "", store_code, "","", role_code);
//                }
//            }else if (role_code.equals(Common.ROLE_BM)) {
//                //品牌管理员
//                String brand_code = request.getSession().getAttribute("brand_code").toString();
//                if (brand_code == null || brand_code.equals("")) {
//                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                    dataBean.setId("1");
//                    dataBean.setMessage("您还没有所属品牌");
//                    return dataBean.getJsonStr();
//                } else {
//                    brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
//                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,"",brand_code,"","");
//                    String store_code = "";
//                    for (int i = 0; i < stores.size(); i++) {
//                        store_code = store_code +  Common.SPECIAL_HEAD +stores.get(i).getStore_code() + ",";
//                    }
//                    list = userService.selectBySearchPart(page_number, page_size, corp_code, "", store_code, "", "",role_code);
//                }
//            } else if (role_code.equals(Common.ROLE_AM)) {
//                //区经
//                String area_code = request.getSession().getAttribute("area_code").toString();
//                String area_store = request.getSession().getAttribute("store_code").toString();
//                if (area_code == null || area_code.equals("")) {
//                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                    dataBean.setId("1");
//                    dataBean.setMessage("您还没有所属区域");
//                    return dataBean.getJsonStr();
//                } else {
//                    list = userService.selectBySearchPart(page_number, page_size, corp_code, "", "", area_store,area_code, role_code);
//                }
//            } else {
//                list = userService.selectBySearch(request, page_number, page_size, Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "###", "");
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage() + ex.toString());
//            logger.info(ex.getMessage() + ex.toString());
//        }
//        return dataBean.getJsonStr();
//    }

    /***
     * Execl增加用户
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
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
 //           int actualRows = LuploadHelper.getRightRows(rs);
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
            Cell[] column4 = rs.getColumn(3);//电话
            Cell[] column1 = rs.getColumn(1);//员工编号
            Cell[] column2 = rs.getColumn(2);//员工ID
            Cell[] column7 = rs.getColumn(6);//群组编号
            Cell[] column8 = rs.getColumn(7);//区域编号
            Cell[] column9 = rs.getColumn(8);//店铺编号
            Cell[] column10 = rs.getColumn(9);//品牌编号
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
//            String onlyCell1 = LuploadHelper.CheckOnly(column4);
//            if (onlyCell1.equals("存在重复值")) {
//                result = "：Execl中手机号码存在重复值";
//                int b = 5 / 0;
//            }
            String onlyCell2 = LuploadHelper.CheckOnly(column1);
            if (onlyCell2.equals("存在重复值")) {
                result = "：Execl中用户编号存在重复值";
                int b = 5 / 0;
            }
            String onlyCel11 = LuploadHelper.CheckOnly(column2);
            if (onlyCel11.equals("存在重复值")) {
                result = "：Execl中用户ID存在重复值";
                int b = 5 / 0;
            }

            Pattern pattern4 = Pattern.compile("(^(\\d{3,4}-)?\\d{7,8})$|(1[3,4,5,7,8]{1}\\d{9})");
            for (int i = 3; i < column4.length; i++) {
                if (column4[i].getContents().toString().trim().equals("")) {
                    continue;
                }
//                Matcher matcher = pattern4.matcher(column4[i].getContents().toString().trim());
//                if (matcher.matches() == false) {
//                    result = "：第" + (i + 1) + "行手机号码格式有误";
//                    int b = 5 / 0;
//                    break;
//                }
            }
            for (int i = 3; i < column4.length; i++) {
                if (column4[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                List<User> user = userService.userPhoneExist(column4[i].getContents().toString().trim());
                if (user.size() > 0) {
                    result = "：第" + (i + 1) + "行的电话号码已存在";
                    int b = 5 / 0;
                    break;
                }
            }

            for (int i = 3; i < column1.length; i++) {
                if (column1[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                List<User> user = userService.userCodeExist(column1[i].getContents().toString().trim(), column3[i].getContents().toString().trim(), "");
                if (user.size() != 0) {
                    result = "：第" + (i + 1) + "行的用户编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }
//            for (int i = 3; i < column2.length; i++) {
//                if (column2[i].getContents().toString().trim().equals("")) {
//                    continue;
//                }
//                List<User> user11 = userService.selUserByUserId(column2[i].getContents().toString().trim(), column3[i].getContents().toString().trim(), "");
//                if (user11.size() != 0) {
//                    result = "：第" + (i + 1) + "行的用户ID已存在";
//                    int b = 5 / 0;
//                    break;
//                }
//            }

            Pattern pattern = Pattern.compile("G\\d{4}");
            //Pattern pattern7 = Pattern.compile("A\\d{4}");

            for (int i = 3; i < column7.length; i++) {
                if (column7[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Matcher matcher = pattern.matcher(column7[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行群组编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Group group = groupService.selectByCode(column3[i].getContents().toString().trim(), column7[i].getContents().toString().trim(), "");
                if (group == null) {
                    result = "：第" + (i + 1) + "行群组编号不存在";
                    int b = 5 / 0;
                    break;
                }
            }
            for (int i = 3; i < column8.length; i++) {
                String areaCheck = column8[i].getContents().toString().trim();
                if (areaCheck == null || areaCheck.equals("")) {
                    continue;
                }
//                String role = groupService.selRoleByGroupCode(column3[i].getContents().toString().trim(), column7[i].getContents().toString().trim());
//                if (role.equals(Common.ROLE_AM) || role.equals(Common.ROLE_SM) || role.equals(Common.ROLE_STAFF)) {
                String areas = column8[i].getContents().toString().trim();
                String[] splitAreas = areas.split(",");
                for (int j = 0; j < splitAreas.length; j++) {
//                        Matcher matcher7 = pattern7.matcher(splitAreas[j]);
//                        if (matcher7.matches() == false) {
//                            result = "：第" + (i + 1) + "行,第"+(j+1)+"个区域编号格式有误";
//                            int b = 5 / 0;
//                            break;
//                        }
                    Area area = areaService.getAreaByCode(column3[i].getContents().toString().trim(), splitAreas[j], Common.IS_ACTIVE_Y);
                    if (area == null) {
                        result = "：第" + (i + 1) + "行,第" + (j + 1) + "个区域编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                }
                //   }
            }
            for (int i = 3; i < column9.length; i++) {
                String storeCheck = column9[i].getContents().toString().trim();
                if (storeCheck == null || storeCheck.equals("")) {
                    continue;
                }
//                String role = groupService.selRoleByGroupCode(column3[i].getContents().toString().trim(), column7[i].getContents().toString().trim());
//                if (role.equals(Common.ROLE_SM) || role.equals(Common.ROLE_STAFF)) {
                String stores = column9[i].getContents().toString().trim();
                String[] splitAreas = stores.split(",");
                for (int j = 0; j < splitAreas.length; j++) {
                    Store store = storeService.getStoreByCode(column3[i].getContents().toString().trim(), splitAreas[j], Common.IS_ACTIVE_Y);
                    if (store == null) {
                        result = "：第" + (i + 1) + "行,第" + (j + 1) + "个店铺编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                }
                //  }
            }

            for (int i = 3; i < column10.length; i++) {
                String brandCheck = column10[i].getContents().toString().trim();
                if (brandCheck == null || brandCheck.equals("")) {
                    continue;
                }
//                String role = groupService.selRoleByGroupCode(column3[i].getContents().toString().trim(), column7[i].getContents().toString().trim());
//                if (role.equals(Common.ROLE_SM) || role.equals(Common.ROLE_STAFF)) {
                String brands = column10[i].getContents().toString().trim();
                String[] splitbrands = brands.split(",");
                for (int j = 0; j < splitbrands.length; j++) {
                    Brand brand = brandService.getBrandByCode(column3[i].getContents().toString().trim(), splitbrands[j], Common.IS_ACTIVE_Y);
                    if (brand == null) {
                        result = "：第" + (i + 1) + "行,第" + (j + 1) + "个品牌编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                }
                //  }
            }
            ArrayList<User> users = new ArrayList<User>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    User user = new User();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    String user_code = rs.getCell(j++, i).getContents().toString().trim();
 //                   String user_id2 = rs.getCell(j++, i).getContents().toString().trim();
//                    if (user_id2.equals("")) {
//                        user_id2 = user_code;
//                    }
                    String user_name = rs.getCell(j++, i).getContents().toString().trim();
                    String phone = rs.getCell(j++, i).getContents().toString().trim();
                    String email = rs.getCell(j++, i).getContents().toString().trim();
                    String sex = rs.getCell(j++, i).getContents().toString().trim();
                    String group_code = rs.getCell(j++, i).getContents().toString().trim();
                    String area_code = rs.getCell(j++, i).getContents().toString().trim();
                    String store_code = rs.getCell(j++, i).getContents().toString().trim();
                    String brand_code = rs.getCell(j++, i).getContents().toString().trim();
                   // System.out.println("-----------EXECL-------store_code---------------------:"+store_code);
                    String position = rs.getCell(j++, i).getContents().toString().trim();
                    if (cellCorp.equals("") && user_code.equals("") && user_name.equals("") && group_code.equals("")) {
                        continue;
                    }
                    if (cellCorp.equals("") || user_code.equals("") || user_name.equals("") || group_code.equals("")) {
                        result = "：第" + (i + 1) + "行信息不完整,请参照Execl中对应的批注";
                        int a = 5 / 0;
                    }
                    String role = groupService.selRoleByGroupCode(cellCorp, group_code);
                    if (!role_code.equals(Common.ROLE_SYS)) {
                        user.setCorp_code(corp_code);
                    } else {
                        user.setCorp_code(cellCorp);
                    }
                    user.setUser_code(user_code);
//                    user.setUser_id(user_id2);
                    user.setUser_name(user_name);
                    user.setAvatar("../img/head.png");//头像
                    user.setPhone(phone);
                    user.setEmail(email);
                    if (sex.equals("女")||sex.equals("F")) {
                        user.setSex("F");
                    } else {
                        user.setSex("M");
                    }
                    user.setGroup_code(group_code);
//                    if (!area_code.equals("all") && !area_code.equals("")) {
//                        String[] areas = area_code.split(",");
//                        area_code = "";
//                        for (int i2 = 0; i2 < areas.length; i2++) {
//                            areas[i2] = Common.SPECIAL_HEAD + areas[i2] + ",";
//                            area_code = area_code + areas[i2];
//                        }
//                    }
                    if (!role.equals(Common.ROLE_AM)) {
                        user.setArea_code("");
                    } else {
                        if(role.equals(Common.ROLE_AM ) && area_code.equals("")){
                            result = "：第" + (i + 1) + "行角色为区经，区域编号为必填项";
                            int a = 5 / 0;
                        }
                        String[] areas = area_code.split(",");
                        int count = 0;
                        for (int k = 0; k < areas.length; k++) {
                            List<Store> stores = storeService.selectStoreCountByArea(corp_code,areas[k],Common.IS_ACTIVE_Y);
                            count = count + stores.size();
                        }
                        if(count>100){
                            result = "：第" + (i + 1) + "行员工拥有店铺数量上限为100";
                            int a = 5 / 0;
                        }
                        if (WebUtils.checkRepeat(areas)) {
                            area_code = "";
                            for (int k = 0; k < areas.length; k++) {
                                areas[k] = Common.SPECIAL_HEAD + areas[k] + ",";
                                area_code = area_code + areas[k];
                            }
                        } else {
                            result = "：第" + (i + 1) + "行Execl中存在重复区域";
                            int a = 5 / 0;
                        }
                        user.setArea_code(area_code);
                    }
//                    if (!store_code.equals("all") && !store_code.equals("")) {
//                        String[] codes = store_code.split(",");
//                        store_code = "";
//                        for (int i2 = 0; i2 < codes.length; i2++) {
//                            codes[i2] = Common.SPECIAL_HEAD + codes[i2] + ",";
//                            store_code = store_code + codes[i2];
//                        }
//                    }
                    if (role.equals(Common.ROLE_BM)) {
                        if(role.equals(Common.ROLE_BM ) && brand_code.equals("")){
                            result = "：第" + (i + 1) + "行角色为品牌管理员，品牌编号为必填项";
                            int a = 5 / 0;
                        }
                        if (!store_code.equals("")) {
                            String[] codes = brand_code.split(",");
                            if (WebUtils.checkRepeat(codes)) {
                                brand_code="";
                                for (int k = 0; k < codes.length; k++) {
                                    codes[k] = Common.SPECIAL_HEAD + codes[k] + ",";
                                    brand_code = brand_code + codes[k];
                                }
                            } else {
                                result = "：第" + (i + 1) + "行Execl中存在重复品牌";
                                int a = 5 / 0;
                            }
                        }
                        user.setBrand_code(brand_code);
                    }
                    if (role.equals(Common.ROLE_SM) || role.equals(Common.ROLE_AM)) {
                        if(role.equals(Common.ROLE_SM ) && store_code.equals("")){
                            result = "：第" + (i + 1) + "行角色为店长，店铺编号为必填项";
                            int a = 5 / 0;
                        }
                        if (!store_code.equals("")) {
                            String[] codes = store_code.split(",");
                            if(codes.length>100){
                                result = "：第" + (i + 1) + "行员工拥有店铺数量上限为100";
                                int a = 5 / 0;
                            }
                         //   String store_code2 = "";
                            if (WebUtils.checkRepeat(codes)) {
                                store_code="";
                                for (int k = 0; k < codes.length; k++) {
                                    codes[k] = Common.SPECIAL_HEAD + codes[k] + ",";
                                    store_code = store_code + codes[k];
                                }
                            } else {
                                result = "：第" + (i + 1) + "行Execl中存在重复店铺";
                                int a = 5 / 0;
                            }
                          //  System.out.println("----------店长店铺----------:"+store_code);
                        }
                        user.setStore_code(store_code);
                    }
                    if(role.equals(Common.ROLE_STAFF)){
                        if(role.equals(Common.ROLE_STAFF ) && store_code.equals("")){
                            result = "：第" + (i + 1) + "行角色为导购，店铺编号为必填项";
                            int a = 5 / 0;
                        }
                        if (!store_code.equals("")) {
                            String[] codes = store_code.split(",");
//                            if (codes.length > 1) {
//                                result = "：第" + (i + 1) + "行角色为导购，只能属于一家店铺";
//                                int a = 5 / 0;
//                            }else{
                                store_code="";
                                for (int k = 0; k < codes.length; k++) {
                                    codes[k] = Common.SPECIAL_HEAD + codes[k] + ",";
                                    store_code = store_code + codes[k];
                                }
                        //    }
                          //  System.out.println("----------导购店铺----------:"+store_code);
                        }
                        user.setStore_code(store_code);
                    }
                    if(role.equals(Common.ROLE_GM)){
                        user.setStore_code("");
                    }

                    user.setPosition(position);
                    String password = CheckUtils.encryptMD5Hash(phone);
                    user.setPassword(password);
                    Date now = new Date();
                    user.setLogin_time_recently("");
                    user.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    user.setCreater(user_id);
                    user.setModified_date(Common.DATETIME_FORMAT.format(now));
                    user.setModifier(user_id);
                    user.setIsactive("Y");
                    user.setCan_login("Y");
                    users.add(user);
                }
            }
            for (User user : users
                    ) {
                result = userService.insert(user);
            }
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
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
     * 用户管理
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code1 = request.getSession().getAttribute("user_code").toString().trim();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_code = jsonObject.get("user_code").toString().trim();
//            String user_id = jsonObject.get("user_id").toString().trim();
            String corp_code = jsonObject.get("corp_code").toString().trim();
            String phone = jsonObject.get("phone").toString().trim();
            User user = new User();
            user.setUser_code(user_code);
//            user.setUser_id(user_id);
            user.setUser_name(jsonObject.get("username").toString());
            user.setAvatar(jsonObject.get("avatar").toString());
            user.setPosition(jsonObject.get("position").toString());
            user.setPhone(phone);
            user.setEmail(jsonObject.get("email").toString().trim());
            user.setSex(jsonObject.get("sex").toString());
            user.setCorp_code(corp_code);
            user.setGroup_code(jsonObject.get("group_code").toString());
            String password = CheckUtils.encryptMD5Hash(phone);
            user.setPassword(password);
            Date now = new Date();
            user.setLogin_time_recently("");
            user.setCreated_date(Common.DATETIME_FORMAT.format(now));
            user.setCreater(user_code1);
            user.setModified_date(Common.DATETIME_FORMAT.format(now));
            user.setModifier(user_code1);
            user.setIsactive(jsonObject.get("isactive").toString());
            user.setCan_login(jsonObject.get("can_login").toString());

            String role_code = jsonObject.get("role_code").toString().trim();
            if (role_code.equals(Common.ROLE_AM)) {
                int count = 0;
                String area_code = jsonObject.get("area_code").toString().trim();
                String store_code = jsonObject.get("store_code").toString().trim();
                List<Store> stores = new ArrayList<Store>();
                if (!area_code.equals("")) {
                    String[] areas = area_code.split(",");
                    area_code = "";
                    for (int i = 0; i < areas.length; i++) {
                        List<Store> stores1 = storeService.selectStoreCountByArea(jsonObject.get("corp_code").toString().trim(),areas[i],Common.IS_ACTIVE_Y);
                        stores.addAll(stores1);
                        area_code = area_code + Common.SPECIAL_HEAD + areas[i] + ",";
                    }
                    count = stores.size();

                    if (!store_code.equals("")){
                        String[] codes = store_code.split(",");
                        count = count + codes.length;
                        store_code = "";
                        for (int i = 0; i < codes.length; i++) {
                            for (int j = 0; j < stores.size(); j++) {
                                if (stores.get(j).getStore_code().equals(codes[i])){
                                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                                    dataBean.setId(id);
                                    dataBean.setMessage("请勿选择重复的店铺:"+stores.get(j).getStore_name());
                                    return dataBean.getJsonStr();
                                }
                            }
                            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                            store_code = store_code + codes[i];
                        }
                    }
                }
                if(count>100){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("员工拥有店铺数量上限为100");
                    return dataBean.getJsonStr();
                }
                user.setArea_code(area_code);
                user.setStore_code(store_code);
            }
            if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) {
                String store_code = jsonObject.get("store_code").toString().trim();
                if (!store_code.equals("all") && !store_code.equals("")) {
                    String[] codes = store_code.split(",");
                    if(codes.length>100){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("员工拥有店铺数量上限为100");
                        return dataBean.getJsonStr();
                    }
                    if (WebUtils.checkRepeat(codes)) {
                        store_code = "";
                        for (int i = 0; i < codes.length; i++) {
                            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                            store_code = store_code + codes[i];
                        }
                    } else {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("请勿选择重复的店铺");
                        return dataBean.getJsonStr();
                    }
                }
                user.setStore_code(store_code);
            }
            if (role_code.equals(Common.ROLE_BM)){
                String brand_code = jsonObject.get("brand_code").toString().trim();
                if (!brand_code.equals("")) {
                    String[] codes = brand_code.split(",");
                    if (WebUtils.checkRepeat(codes)) {
                        brand_code = "";
                        for (int i = 0; i < codes.length; i++) {
                            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                            brand_code = brand_code + codes[i];
                        }
                    } else {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("请勿选择重复的品牌");
                        return dataBean.getJsonStr();
                    }
                }
                user.setBrand_code(brand_code);
            }

            String result = userService.insert(user);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                List<User> users = userService.userCodeExist(user_code,corp_code,jsonObject.get("isactive").toString());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(users.get(0).getId()));

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
                String function = "员工管理_员工列表";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("user_code").toString();
                String t_name = action_json.get("username").toString();
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
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户管理
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString().trim();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_code1 = jsonObject.get("user_code").toString().trim();
//            String user_id = jsonObject.get("user_id").toString().trim();
            User user = new User();
            user.setId(Integer.parseInt(jsonObject.get("id").toString().trim()));
            user.setUser_code(user_code1);
//            user.setUser_id(user_id);
            user.setUser_name(jsonObject.get("username").toString().trim());
            user.setPosition(jsonObject.get("position").toString().trim());
            user.setAvatar(jsonObject.get("avatar").toString().trim());
            user.setPhone(jsonObject.get("phone").toString().trim());
            user.setEmail(jsonObject.get("email").toString().trim());
            user.setSex(jsonObject.get("sex").toString().trim());
            user.setCorp_code(jsonObject.get("corp_code").toString().trim());
            user.setGroup_code(jsonObject.get("group_code").toString().trim());

            Date now = new Date();
            user.setModified_date(Common.DATETIME_FORMAT.format(now));
            user.setModifier(user_code);
            user.setIsactive(jsonObject.get("isactive").toString());
            user.setCan_login(jsonObject.get("can_login").toString());

            String role_code = jsonObject.get("role_code").toString().trim();
            String area_code = jsonObject.get("area_code").toString().trim();
            String store_code = jsonObject.get("store_code").toString().trim();

            user.setArea_code(area_code);
            user.setStore_code(store_code);
            if (role_code.equals(Common.ROLE_AM)) {
                int count = 0;
                List<Store> stores = new ArrayList<Store>();
                if (!area_code.equals("")) {
                    String[] areas = area_code.split(",");
                    area_code = "";
                    for (int i = 0; i < areas.length; i++) {
                        List<Store> stores1 = storeService.selectStoreCountByArea(jsonObject.get("corp_code").toString().trim(),areas[i],Common.IS_ACTIVE_Y);
                        stores.addAll(stores1);
                        area_code = area_code + Common.SPECIAL_HEAD + areas[i] + ",";
                    }
                    count = stores.size();

                    if (!store_code.equals("")){
                        String[] codes = store_code.split(",");
                        count = count + codes.length;
                        store_code = "";
                        for (int i = 0; i < codes.length; i++) {
                            for (int j = 0; j < stores.size(); j++) {
                                if (stores.get(j).getStore_code().equals(codes[i])){
                                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                                    dataBean.setId(id);
                                    dataBean.setMessage("请勿选择重复的店铺:"+stores.get(j).getStore_name());
                                    return dataBean.getJsonStr();
                                }
                            }
                            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                            store_code = store_code + codes[i];
                        }
                    }
                }
                if(count>100){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("员工拥有店铺数量上限为100");
                    return dataBean.getJsonStr();
                }
                user.setArea_code(area_code);
                user.setStore_code(store_code);
            }
            if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) {
                if (!store_code.equals("")) {
                    String[] codes = store_code.split(",");
                    if(codes.length>100){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("员工拥有店铺数量上限为100");
                        return dataBean.getJsonStr();
                    }
                    if (WebUtils.checkRepeat(codes)) {
                        store_code = "";
                        for (int i = 0; i < codes.length; i++) {
                            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                            store_code = store_code + codes[i];
                        }
                    } else {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("请勿选择重复的店铺");
                        return dataBean.getJsonStr();
                    }
                }
                user.setStore_code(store_code);
            }
            if (role_code.equals(Common.ROLE_BM)){
                user.setGroup_code(jsonObject.get("group_code").toString().trim());
                String brand_code = jsonObject.get("brand_code").toString().trim();
                if (!brand_code.equals("")){
                    String[] codes = brand_code.split(",");
                    if (WebUtils.checkRepeat(codes)) {
                        brand_code = "";
                        for (int i = 0; i < codes.length; i++) {
                         codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                          brand_code = brand_code + codes[i];
                        }
                    } else {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("请勿选择重复的品牌");
                        return dataBean.getJsonStr();
                    }
                }

                user.setBrand_code(brand_code);
            }

            logger.info("------update user" + user.toString());
            String result = userService.update(user);
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
                String function = "员工管理_员工列表";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("user_code").toString();
                String t_name = action_json.get("username").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
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
        logger.info("info--------" + dataBean.getJsonStr());
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
            logger.info("json--user delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            String[] ids = user_id.split(",");
            int count = 0;
            String msg = "";
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete user--" + Integer.valueOf(ids[i]));
                User user = userService.getById(Integer.parseInt(ids[i]));
                if (user != null) {
                    List<UserAchvGoal> goal = userService.selectUserAchvCount(user.getCorp_code(), user.getUser_code());
                    count = goal.size();
                    if (count > 0) {
                        msg = "请先删除用户的业绩目标，再删除用户" + user.getUser_code();
                        break;
                    }
                }
            }
            if (count > 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(msg);
            } else {
                for (int i = 0; i < ids.length; i++) {
                    User user = userService.getById(Integer.parseInt(ids[i]));
                    if (user != null) {
                        String user_code = user.getUser_code();
                        String corp_code = user.getCorp_code();
                        userService.deleteUserQrcode(corp_code, user_code);
                        signService.deleteByUser(user_code, corp_code);
                        userService.delete(Integer.valueOf(ids[i]), user_code, corp_code);
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
                    String function = "员工管理_员工列表";
                    String action = Common.ACTION_DEL;
                    String t_corp_code = user.getCorp_code();
                    String t_code = user.getUser_code();
                    String t_name = user.getUser_name();
                    String remark = "";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
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
     * 用户管理
     * 选择用户
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user select-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(userService.getUserById(Integer.parseInt(user_id)));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("用户信息异常");
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
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<User> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = userService.selectBySearch(request, page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    //企业管理员
                    list = userService.selectBySearch(request, page_number, page_size, corp_code, search_value);
                }else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
                    String store_code = "";
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    list = userService.selectBySearchPart(page_number, page_size, corp_code, search_value, store_code,"", "", role_code);

                }else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = userService.selectBySearchPart(page_number, page_size, corp_code, search_value, store_code,"", "", role_code);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String area_store = request.getSession().getAttribute("store_code").toString();
                    list = userService.selectBySearchPart(page_number, page_size, corp_code, search_value, "",area_store, area_code, role_code);
                } else {
                    list = userService.selectBySearch(request, page_number, page_size, Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "###", "");
                }
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


    /**
     * 根据登录用户的角色类型
     * 输入的企业编号
     * 获得该用户可选择的所有群组
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST)
    @ResponseBody
    public String userGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject groups = new JSONObject();
            List<Group> group;
            if (role_code.equals(Common.ROLE_SYS)) {
                //列出企业下所有
                group = groupService.selectUserGroup(corp_code, "");
            } else {
                //比登陆用户角色级别低的群组
                String login_corp_code = request.getSession().getAttribute("corp_code").toString();
                group = groupService.selectUserGroup(login_corp_code, role_code);
            }
            groups.put("group", JSON.toJSONString(group));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(groups.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 根据登录用户的角色类型
     * 输入的企业编号
     * 查找该企业，该用户可选择的所有店铺
     */
    @RequestMapping(value = "/store", method = RequestMethod.POST)
    @ResponseBody
    public String userStore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user store-------------" + jsString);
            System.out.println("json--user store-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            JSONObject stores = new JSONObject();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            List<Store> list;
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM)) {
                //登录用户为admin或企业管理员
                list = storeService.getCorpStore(corp_code);
            } else if (role_code.equals(Common.ROLE_BM)) {
                //登录用户为品牌管理员
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                String corp_code1 = request.getSession().getAttribute("corp_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                String[] brandCodes = brand_code.split(",");

                list = storeService.selectByAreaBrand(corp_code1, null,null, brandCodes, Common.IS_ACTIVE_Y);
            }else if (role_code.equals(Common.ROLE_AM)) {
                //登录用户为区经
                String area_code = request.getSession().getAttribute("area_code").toString();
                String store_code = request.getSession().getAttribute("store_code").toString();
                String corp_code1 = request.getSession().getAttribute("corp_code").toString();
                area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                String[] areaCodes = area_code.split(",");
                String[] storeCodes = null;
                if (!store_code.equals("")){
                    store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                    storeCodes = store_code.split(",");
                }
                list = storeService.selectByAreaBrand(corp_code1, areaCodes, storeCodes,null, Common.IS_ACTIVE_Y);

            } else {
                //登录用户为店长或导购
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = storeService.selectAll(store_code, corp_code, Common.IS_ACTIVE_Y);
            }
            stores.put("stores", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(stores.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 根据用户的ID输出用户的企业
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCorpByUser", method = RequestMethod.POST)
    @ResponseBody
    public String getCorpByUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject corps = new JSONObject();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONArray array = new JSONArray();
            if (role_code.equals((Common.ROLE_SYS))) {
                List<Corp> list = corpService.selectAllCorp();
                for (int i = 0; i < list.size(); i++) {
                    Corp corp = list.get(i);
                    String c_code = corp.getCorp_code();
                    String corp_name = corp.getCorp_name();
                    JSONObject obj = new JSONObject();
                    obj.put("corp_code", c_code);
                    obj.put("corp_name", corp_name);
                    array.add(obj);
                }
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                Corp corp = corpService.selectByCorpId(0, corp_code, Common.IS_ACTIVE_Y);
                String c_code = corp.getCorp_code();
                String corp_name = corp.getCorp_name();
                JSONObject obj = new JSONObject();
                obj.put("corp_code", c_code);
                obj.put("corp_name", corp_name);
                array.add(obj);
            }
            corps.put("corps", array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(corps.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户管理
     * 查看权限
     */
    @RequestMapping(value = "/check_power", method = RequestMethod.POST)
    @ResponseBody
    public String groupCheckPower(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String login_user_code = request.getSession().getAttribute("user_code").toString();
            String login_corp_code = request.getSession().getAttribute("corp_code").toString();
            String login_role_code = request.getSession().getAttribute("role_code").toString();
            String login_group_code = request.getSession().getAttribute("group_code").toString();

            String search_value = "";
            if (jsonObject.has("searchValue")) {
                search_value = jsonObject.get("searchValue").toString();
            }
            //获取登录用户的所有权限
            List<Function> funcs = functionService.selectAllPrivilege(login_corp_code, login_role_code, login_user_code, login_group_code, search_value);

            String group_code = jsonObject.get("group_code").toString();
            String user_id = jsonObject.get("user_id").toString();
            String corp_code = userService.getUserById(Integer.parseInt(user_id)).getCorp_code();
            String role_code = groupService.selectByCode(corp_code, group_code, Common.IS_ACTIVE_Y).getRole_code();
            String user_code = userService.getUserById(Integer.parseInt(user_id)).getUser_code();

            //获取群组自定义的权限
            JSONArray group_privilege = functionService.selectRAGPrivilege(role_code, corp_code + "G" + group_code);

            //获取用户自定义的权限
            JSONArray user_privilege = functionService.selectUserPrivilege(corp_code, user_code);

            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(funcs));
            result.put("die", group_privilege);
            result.put("live", user_privilege);

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

    /**
     * 群组管理之
     * 编辑群组信息之
     * 查看权限之
     * 新增权限
     */
    @RequestMapping(value = "/check_power/save", method = RequestMethod.POST)
    @ResponseBody
    public String addGroupCheckPower(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_id = request.getSession().getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String list = jsonObject.get("list").toString();
            JSONArray array = JSONArray.parseArray(list);

            String user_code = jsonObject.get("group_code").toString();
            String master_code;
            if (jsonObject.has("corp_code")) {
                String corp_code = jsonObject.get("corp_code").toString();
                master_code = corp_code + "U" + user_code;
            } else {
                master_code = user_code;
            }
            String result = functionService.updatePrivilege(master_code, user_id, array);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户编号是否重复
     */
    @RequestMapping(value = "/userCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String UserCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<User> existInfo = new ArrayList<User>();
            if (jsonObject.has("user_code")) {
                String user_code = jsonObject.get("user_code").toString();
                existInfo = userService.userCodeExist(user_code, corp_code, Common.IS_ACTIVE_Y);

            }
            if (jsonObject.has("user_id")) {
                String user_id = jsonObject.get("user_id").toString();
                existInfo = userService.userIdExist(user_id, corp_code);
            }
            if (existInfo.size() != 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("用户编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("用户编号不存在");
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
     * 手机号是否重复
     */
    @RequestMapping(value = "/PhoneExist", method = RequestMethod.POST)
    @ResponseBody
    public String PhoneExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String phone = jsonObject.get("phone").toString();
            List<User> user = userService.userPhoneExist(phone);
            if (user.size() > 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("手机号码已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("手机号码未被使用");
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
     * 邮箱是否重复
     */
    @RequestMapping(value = "/EamilExist", method = RequestMethod.POST)
    @ResponseBody
    public String EamilExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String email = jsonObject.get("email").toString();
            List<User> user = userService.userEmailExist(email);
            if (user.size() > 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("email已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("email未被使用");
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
     * 导购根据user_code生成二维码
     */
    @RequestMapping(value = "/creatQrcode", method = RequestMethod.POST)
    @ResponseBody
    public String creatQrcode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("------------UserController creatQrcode" + jsString);
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String user_code = jsonObject.get("user_code").toString();
            String app_id = jsonObject.get("app_id").toString();
            CorpWechat corpWechat = corpService.getCorpByAppId(app_id);
            if (corpWechat != null && corpWechat.getApp_id() != null && corpWechat.getApp_id() != "") {
                String auth_appid = corpWechat.getApp_id();
                String is_authorize = corpWechat.getIs_authorize();
                if (is_authorize.equals("Y")) {
                    String result = userService.creatUserQrcode(corp_code, user_code, auth_appid, user_id);
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
     * 批量生成二维码
     */
    @RequestMapping(value = "/creatUsersQrcode", method = RequestMethod.POST)
    @ResponseBody
    public String creatUsersQrcode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
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
                String user_code = json.get("user_code").toString();
                if (corpWechat.getCorp_code().equals(corp_code)) {
                    String is_authorize = corpWechat.getIs_authorize();
                    if (is_authorize.equals("Y")) {
                        String result = userService.creatUserQrcode(corp_code, user_code, auth_appid, user_id);
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
     * 自动生成二维码（APP接口）
     */
    @RequestMapping(value = "/creatQrcodesForUser", method = RequestMethod.GET)
    @ResponseBody
    public String creatQrcodesForUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            logger.info("------------UserController creatQrcodesForUser------");
            String corp_code = request.getParameter("corp_code");
            String user_code = request.getParameter("user_code");

            List<User> users = userService.userCodeExist(user_code, corp_code, Common.IS_ACTIVE_Y);
            if (users.size() < 1) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("用户不存在");
                return dataBean.getJsonStr();
            }
            List<CorpWechat> corpWechats = new ArrayList<CorpWechat>();
            if (corp_code.equals("C10016")){
                List<String> brand_codes = userService.getBrandCodeByUser(users.get(0).getId(), corp_code);
                String brand_code = "";
                for (int i = 0; i < brand_codes.size(); i++) {
                     brand_code = brand_code + Common.SPECIAL_HEAD + brand_codes.get(i) + ",";
                }
                corpWechats = corpService.selectWByCorpBrand(corp_code, brand_code);
            }else {
                corpWechats = corpService.getWAuthByCorp(corp_code);
            }
            String count = "0";
                for (int j = 0; j < corpWechats.size(); j++) {
                    String auth_appid = corpWechats.get(j).getApp_id();
                    String app_name = corpWechats.get(j).getApp_name();
                    String result = userService.creatUserQrcode(corp_code, user_code, auth_appid, user_code);
                    if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                        count = app_name;
                    } else if (result.equals("48001")) {
                        count = app_name;
                    }
                }
            if (count.equals("0")){
                dataBean.setId(id);
                dataBean.setMessage("生成完成");
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            }else {
                dataBean.setId(id);
                dataBean.setMessage(count+"生成二维码失败");
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            }
//            weimobService.generateToken(CommonValue.CLIENT_ID, CommonValue.CLIENT_SECRET);
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
            logger.info("------------UserController deletQrcode" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_code = jsonObject.get("user_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String app_id = jsonObject.get("app_id").toString();

            userService.deleteUserQrcodeOne(corp_code, user_code, app_id);
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

    /**
     * 员工管理
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
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<User> list = null;
            List<Store> storeList=null;
            List<Brand> list1=new ArrayList<Brand>();
            if (role_code.equals(Common.ROLE_SYS)) {
                PageInfo<Brand> allBrandByPage = brandService.getAllBrandByPage(1, 20, "", map.get("brand_name"));
                 list1 = allBrandByPage.getList();
            }else{
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                PageInfo<Brand> allBrandByPage =  brandService.getAllBrandByPage(1,20,corp_code,"");
                list1 = allBrandByPage.getList();
            }
            if(!map.get("brand_name").equals("") && list1.size()>1) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("brand_name", map.get("brand_name"));
                if (role_code.equals(Common.ROLE_SYS)) {
                    map1.put("corp_name", map.get("corp_name"));
                    storeList = storeService.getStoreByBrandCode("", "", "", "", map1, "", "");
                } else if (role_code.equals(Common.ROLE_GM)) {
                    map1.put("corp_name","");
                    String corp_code = request.getSession().getAttribute("corp_code").toString();
                    storeList = storeService.getStoreByBrandCode(corp_code, "", "", "", map1, "", "");
                } else if (role_code.equals(Common.ROLE_BM)) {
                    map1.put("corp_name","");
                    String corp_code = request.getSession().getAttribute("corp_code").toString();
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    storeList = storeService.getStoreByBrandCode(corp_code, "", brand_code, "", map1, "", "");
                } else if (role_code.equals(Common.ROLE_AM)) {
                    map1.put("corp_name","");
                    String corp_code = request.getSession().getAttribute("corp_code").toString();
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    storeList = storeService.getStoreByBrandCode(corp_code, area_codes, "", "", map1, store_code, "");
                } else {
                    map1.put("corp_name","");
                    String corp_code = request.getSession().getAttribute("corp_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    storeList = storeService.getStoreByBrandCode(corp_code, "", "", store_code, map1, "", "");
                }
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = userService.getAllUserScreen2(page_number, page_size, "", map, storeList);
                } else {
                    String corp_code = request.getSession().getAttribute("corp_code").toString();
                    if (role_code.equals(Common.ROLE_GM)) {
                        //企业管理员
                        list = userService.getAllUserScreen2(page_number, page_size, corp_code, map, storeList);
                    } else if (role_code.equals(Common.ROLE_SM)) {
                        //店长
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = userService.getScreenPart2(page_number, page_size, corp_code, map, store_code, "", "", role_code, storeList);
                    } else if (role_code.equals(Common.ROLE_BM)) {
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                        String store_code = "";
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                        }
                        list = userService.getScreenPart2(page_number, page_size, corp_code, map, store_code, "", "", role_code, storeList);

                    } else if (role_code.equals(Common.ROLE_AM)) {
                        //区经
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        String area_store = request.getSession().getAttribute("store_code").toString();
                        list = userService.getScreenPart2(page_number, page_size, corp_code, map, "", area_store, area_code, role_code, storeList);
                    } else {
                        list = userService.selectBySearch(request, page_number, page_size, Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "###", "");
                    }
                }
            }else{
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = userService.getAllUserScreen(page_number, page_size, "", map);
                } else {
                    String corp_code = request.getSession().getAttribute("corp_code").toString();
                    if (role_code.equals(Common.ROLE_GM)) {
                        //企业管理员
                        list = userService.getAllUserScreen(page_number, page_size, corp_code, map);
                    } else if (role_code.equals(Common.ROLE_SM)) {
                        //店长
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = userService.getScreenPart(page_number, page_size, corp_code, map, store_code, "", "", role_code);
                    } else if (role_code.equals(Common.ROLE_BM)) {
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                        String store_code = "";
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                        }
                        list = userService.getScreenPart(page_number, page_size, corp_code, map, store_code, "", "", role_code);

                    } else if (role_code.equals(Common.ROLE_AM)) {
                        //区经
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        String area_store = request.getSession().getAttribute("store_code").toString();
                        list = userService.getScreenPart(page_number, page_size, corp_code, map, "", area_store, area_code, role_code);
                    } else {
                        list = userService.selectBySearch(request, page_number, page_size, Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "###", "");
                    }
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
     * 员工管理
     * 重置密码
     */
    @RequestMapping(value = "/change_passwd", method = RequestMethod.POST)
    @ResponseBody
    public String changePasswd(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
            id = jsonObject1.getString("id");
            String message = jsonObject1.get("message").toString();
            JSONObject jsonObject2 = new JSONObject(message);
            String password = jsonObject2.get("password").toString();
            User user = null;
            if (jsonObject2.has("phone")){
                user = userService.userPhoneExist(jsonObject2.get("phone").toString()).get(0);
            }
            if (jsonObject2.has("user_id")) {
                int user_id = Integer.parseInt(jsonObject2.get("user_id").toString());
                user = userService.selectUserById(user_id);
            }
            if (user != null) {
                user.setPassword(password);
                Date now = new Date();
                user.setModified_date(Common.DATETIME_FORMAT.format(now));
                user.setModifier(user_code);
                userService.updateUser(user);
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("重置密码成功");
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 查看我的账户
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/myAccount", method = RequestMethod.GET)
    @ResponseBody
    public String viewAccount(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        String userId = request.getSession().getAttribute("user_id").toString();
        int user_id = Integer.parseInt(userId);
        try {
            User user = userService.getUserById(user_id);
            JSONObject result = new JSONObject();
            result.put("user", JSON.toJSONString(user));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString() + "========ex==========");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 批量签到
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/sign", method = RequestMethod.POST)
    @ResponseBody
    public String sign(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user sign-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String type = jsonObject.get("type").toString();
            if (type.equals("signIn")) {
                userService.signIn(jsonObject, user_code);
            } else if (type.equals("signOut")) {
                userService.signOut(jsonObject, user_code);
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString() + "========ex==========");
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
        String  role_code= request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user sign-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String store_code=jsonObject.get("store_code").toString();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                corp_code = "C10000";
            }
//            com.alibaba.fastjson.JSONObject obj_corp=new com.alibaba.fastjson.JSONObject();
//            obj_corp.put("corp_code",corp_code);
            com.alibaba.fastjson.JSONObject obj_store=new com.alibaba.fastjson.JSONObject();
            obj_store.put("store_code",store_code);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_store_code = new Data("store_code", obj_store.toString(), ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_code.key, data_store_code);

            DataBox dataBox = iceInterfaceService.iceInterfaceV3("DataBackup", datalist);
            String  message_info=dataBox.data.get("message").value.toString();
            com.alibaba.fastjson.JSONObject info= JSON.parseObject(message_info);
           // String store_count=info.get("store_count").toString();

                String hbase_user_count=info.get("hbase_user_count").toString();
                if(Integer.parseInt(hbase_user_count)>0){
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("同步成功");

                }else{
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
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
}
