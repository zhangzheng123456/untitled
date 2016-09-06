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
    private TableManagerService managerService;
    String id;

    /***
     * 根据企业，店铺拉取员工
     */
    @RequestMapping(value = "/selectByPart", method = RequestMethod.POST)
    @ResponseBody
    public String selectByPart(HttpServletRequest request, HttpServletResponse response) {
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
            String role_code = request.getSession().getAttribute("role_code").toString();
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());
            String area = jsonObject.get("area_code").toString();
            String[] areas = area.split(",");
            PageInfo<User> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                String store_code = jsonObject.get("store_code").toString();
                list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, areas, Common.ROLE_STAFF);
            } else if (role_code.equals(Common.ROLE_GM)) {
                String store_code = jsonObject.get("store_code").toString();
                list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, areas, Common.ROLE_STAFF);
                List<User> users = list.getList();
                User self = userService.getUserById(user_id);
                users.add(self);
            } else if (role_code.equals(Common.ROLE_STAFF)) {
                User user = userService.getUserById(user_id);
                List<User> users = new ArrayList<User>();
                users.add(user);
                list = new PageInfo<User>();
                list.setList(users);
            } else if (role_code.equals(Common.ROLE_SM)) {
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, null, Common.ROLE_STAFF);
                List<User> users = list.getList();
                User self = userService.getUserById(user_id);
                users.add(self);
            } else if (role_code.equals(Common.ROLE_AM)) {
                String store_code = jsonObject.get("store_code").toString();
                list = userService.selUserByStoreCode(page_number, page_size, corp_code, searchValue, store_code, null, Common.ROLE_STAFF);
                List<User> users = list.getList();
                User self = userService.getUserById(user_id);
                users.add(self);
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
                    list = userService.selectBySearch(request, 1, 30000, "", search_value);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    //系统管理员
                    list = userService.selectBySearch(request, 1, 30000, corp_code, search_value);
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
                    list = userService.selectBySearchPart(1, 30000, corp_code, search_value, store_code, "", role_code);
                    List<User> users = list.getList();
                    User self = userService.getUserById(user_id);
                    users.add(self);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    list = userService.selectBySearchPart(1, 30000, corp_code, search_value, "", area_code, role_code);
                    List<User> users = list.getList();
                    User self = userService.getUserById(user_id);
                    users.add(self);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = userService.getAllUserScreen(1, 30000, "", map);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        //企业管理员
                        list = userService.getAllUserScreen(1, 30000, corp_code, map);
                    } else if (role_code.equals(Common.ROLE_SM)) {
                        //店长
                        String store_code = request.getSession().getAttribute("store_code").toString();
                        list = userService.getScreenPart(1, 30000, corp_code, map, store_code, "", role_code);
                    } else if (role_code.equals(Common.ROLE_AM)) {
                        //区经
                        String area_code = request.getSession().getAttribute("area_code").toString();
                        list = userService.getScreenPart(1, 30000, corp_code, map, "", area_code, role_code);
                    }
                }

            }
            List<User> users = list.getList();
            if (users.size() >= 29999) {
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
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String userManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONObject result = new JSONObject();
            PageInfo<User> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = userService.selectBySearch(request, page_number, page_size, "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                //系统管理员
                list = userService.selectBySearch(request, page_number, page_size, corp_code, "");
            } else if (role_code.equals(Common.ROLE_SM)) {
                //店长
                String store_code = request.getSession().getAttribute("store_code").toString();
                if (store_code == null || store_code.equals("")) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("您还没有所属店铺");
                    return dataBean.getJsonStr();
                } else {
                    list = userService.selectBySearchPart(page_number, page_size, corp_code, "", store_code, "", role_code);
                }
            } else if (role_code.equals(Common.ROLE_AM)) {
                //区经
                String area_code = request.getSession().getAttribute("area_code").toString();
                if (area_code == null || area_code.equals("")) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("您还没有所属区域");
                    return dataBean.getJsonStr();
                } else {
                    list = userService.selectBySearchPart(page_number, page_size, corp_code, "", "", area_code, role_code);
                }
            } else {
                list = userService.selectBySearch(request, page_number, page_size, Common.STORE_HEAD + Common.STORE_HEAD + "###", "");
            }
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

    /**
     * 普通用户新增
     * 获取公司编号
     */
    @RequestMapping(value = "/add_code", method = RequestMethod.POST)
    @ResponseBody
    public String addCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        System.out.println("add-corp_code" + corp_code);
        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
        dataBean.setId(id);
        dataBean.setMessage(corp_code);
        return dataBean.getJsonStr();
    }



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
            int clos = rs.getColumns();//得到所有的列
            int rows = rs.getRows();//得到所有的行
            int actualRows = LuploadHelper.getRightRows(rs);
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
            Cell[] column4 = rs.getColumn(4);//电话
            Cell[] column1 = rs.getColumn(1);//员工编号
            Cell[] column2 = rs.getColumn(2);//员工ID
            Cell[] column7 = rs.getColumn(7);//群组编号
            Cell[] column8 = rs.getColumn(8);//区域编号
            Cell[] column9 = rs.getColumn(9);//店铺编号
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
            String onlyCell1 = LuploadHelper.CheckOnly(column4);
            if (onlyCell1.equals("存在重复值")) {
                result = "：Execl中手机号码存在重复值";
                int b = 5 / 0;
            }
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
                Matcher matcher = pattern4.matcher(column4[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行手机号码格式有误";
                    int b = 5 / 0;
                    break;
                }
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
            for(int i=3;i<column2.length;i++){
                if (column2[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                List<User> user11 = userService.selUserByUserId(column2[i].getContents().toString().trim(), column3[i].getContents().toString().trim(), "");
                if (user11.size() != 0) {
                    result = "：第" + (i + 1) + "行的用户ID已存在";
                    int b = 5 / 0;
                    break;
                }
            }

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

                String role = groupService.selRoleByGroupCode(column3[i].getContents().toString().trim(), column7[i].getContents().toString().trim());
                if (role.equals(Common.ROLE_AM) || role.equals(Common.ROLE_SM) || role.equals(Common.ROLE_STAFF)) {
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
                }
                if (role.equals(Common.ROLE_SM) || role.equals(Common.ROLE_STAFF)) {
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
                }
            }
            ArrayList<User> users = new ArrayList<User>();
            for (int i = 3; i < rows; i++) {
                String role = groupService.selRoleByGroupCode(column3[i].getContents().toString().trim(), column7[i].getContents().toString().trim());
                for (int j = 0; j < clos; j++) {
                    User user = new User();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    String user_code = rs.getCell(j++, i).getContents().toString().trim();
                    String user_id2 = rs.getCell(j++, i).getContents().toString().trim();
                    if(user_id2.equals("")){
                        user_id2 = user_code;
                    }
                    String user_name = rs.getCell(j++, i).getContents().toString().trim();
                    String phone = rs.getCell(j++, i).getContents().toString().trim();
                    String email = rs.getCell(j++, i).getContents().toString().trim();
                    String sex = rs.getCell(j++, i).getContents().toString().trim();
                    String group_code = rs.getCell(j++, i).getContents().toString().trim();
                    String area_code = rs.getCell(j++, i).getContents().toString().trim();
                    String store_code = rs.getCell(j++, i).getContents().toString().trim();
                    String position = rs.getCell(j++, i).getContents().toString().trim();
                    if (cellCorp.equals("") && user_code.equals("")  && user_name.equals("") && phone.equals("") && group_code.equals("")) {
                        continue;
                    }
                    if (cellCorp.equals("") || user_code.equals("")|| user_name.equals("") || phone.equals("") || group_code.equals("")) {
                        result = "：第" + (i + 1) + "行信息不完整,请参照Execl中对应的批注";
                        int a = 5 / 0;
                    }
                    if (!role_code.equals(Common.ROLE_SYS)) {
                        user.setCorp_code(corp_code);
                    } else {
                        user.setCorp_code(cellCorp);
                    }
                    user.setUser_code(user_code);
                    user.setUser_id(user_id2);
                    user.setUser_name(user_name);
                    user.setAvatar("../img/head.png");//头像
                    user.setPhone(phone);
                    user.setEmail(email);
                    if (sex.equals("男")) {
                        user.setSex("M");
                    } else {
                        user.setSex("F");
                    }
                    user.setGroup_code(group_code);
                    if (!area_code.equals("all") && !area_code.equals("")) {
                        String[] areas = area_code.split(",");
                        area_code = "";
                        for (int i2 = 0; i2 < areas.length; i2++) {
                            areas[i2] = Common.STORE_HEAD + areas[i2] + ",";
                            area_code = area_code + areas[i2];
                        }
                    }
                    if (!role.equals(Common.ROLE_AM)) {
                        user.setArea_code("");
                    } else {
                        user.setArea_code(area_code);
                    }
                    if (!store_code.equals("all") && !store_code.equals("")) {
                        String[] codes = store_code.split(",");
                        store_code = "";
                        for (int i2 = 0; i2 < codes.length; i2++) {
                            codes[i2] = Common.STORE_HEAD + codes[i2] + ",";
                            store_code = store_code + codes[i2];
                        }
                    }
                    if (role.equals(Common.ROLE_SM) || role.equals(Common.ROLE_STAFF)) {
                        user.setStore_code(store_code);
                    } else {
                        user.setStore_code("");
                    }
                    user.setPosition(position);
                    user.setPassword(user.getPhone());
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
        String user_code1 = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_code = jsonObject.get("user_code").toString();
            String user_id = jsonObject.get("user_id").toString();

            String corp_code = jsonObject.get("corp_code").toString();
            String phone = jsonObject.get("phone").toString();
            User user = new User();
            user.setUser_code(user_code);
            if (user_id.equals("")){
                user.setUser_id(user_code);
            }else {
                user.setUser_id(user_id);
            }
            user.setUser_name(jsonObject.get("username").toString());
            user.setAvatar(jsonObject.get("avater").toString());
            user.setPosition(jsonObject.get("position").toString());
            user.setPhone(phone);
            user.setEmail(jsonObject.get("email").toString());
            user.setSex(jsonObject.get("sex").toString());
            //     user.setBirthday(jsonObject.get("birthday").toString());
            user.setCorp_code(corp_code);
            user.setGroup_code(jsonObject.get("group_code").toString());
            Group group = groupService.selectByCode(user.getCorp_code(), user.getGroup_code(), "");
            String role_code = group.getRole_code();
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM)) {
                user.setGroup_code(jsonObject.get("group_code").toString());
            }
            if (role_code.equals(Common.ROLE_AM)) {
                user.setGroup_code(jsonObject.get("group_code").toString());
                String area_code = jsonObject.get("area_code").toString();
                if (!area_code.equals("all") && !area_code.equals("")) {
                    String[] areas = area_code.split(",");
                    if (WebUtils.checkRepeat(areas)) {
                        area_code = "";
                        for (int i = 0; i < areas.length; i++) {
                            areas[i] = Common.STORE_HEAD + areas[i] + ",";
                            area_code = area_code + areas[i];
                        }
                    } else {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("请勿选择重复的区域");
                        return dataBean.getJsonStr();
                    }
                }
                user.setArea_code(area_code);
            }
            if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) {
                user.setGroup_code(jsonObject.get("group_code").toString());
                String store_code = jsonObject.get("store_code").toString();
                if (!store_code.equals("all") && !store_code.equals("")) {
                    String[] codes = store_code.split(",");
                    if (WebUtils.checkRepeat(codes)) {
                        store_code = "";
                        for (int i = 0; i < codes.length; i++) {
                            codes[i] = Common.STORE_HEAD + codes[i] + ",";
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
            String result = userService.insert(user);
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
     * 用户管理
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_code1 = jsonObject.get("user_code").toString();
            String user_id = jsonObject.get("user_id").toString();
            User user = new User();
            user.setId(Integer.parseInt(jsonObject.get("id").toString()));
            user.setUser_code(user_code1);
            if (user_id.equals("")){
                user.setUser_id(user_code1);
            }else {
                user.setUser_id(user_id);
            }
            user.setUser_name(jsonObject.get("username").toString());
            user.setPosition(jsonObject.get("position").toString());
            user.setAvatar(jsonObject.get("avater").toString());
            user.setPhone(jsonObject.get("phone").toString());
            user.setEmail(jsonObject.get("email").toString());
            user.setSex(jsonObject.get("sex").toString());
            //       user.setBirthday(jsonObject.get("birthday").toString());
            user.setCorp_code(jsonObject.get("corp_code").toString());
            user.setGroup_code(jsonObject.get("group_code").toString());

            String role_code = jsonObject.get("role_code").toString();
            String area_code = jsonObject.get("area_code").toString();
            String store_code = jsonObject.get("store_code").toString();
            user.setArea_code(area_code);
            user.setStore_code(store_code);
            if (role_code.equals(Common.ROLE_AM)) {
                if (!area_code.equals("all") && !area_code.equals("")) {
                    String[] areas = area_code.split(",");
                    if (WebUtils.checkRepeat(areas)) {
                        area_code = "";
                        for (int i = 0; i < areas.length; i++) {
                            areas[i] = Common.STORE_HEAD + areas[i] + ",";
                            area_code = area_code + areas[i];
                        }
                    } else {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("请勿选择重复的区域");
                        return dataBean.getJsonStr();
                    }
                }
                user.setArea_code(area_code);
            }
            if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) {
                if (!store_code.equals("all") && !store_code.equals("")) {
                    String[] codes = store_code.split(",");
                    if (WebUtils.checkRepeat(codes)) {
                        store_code = "";
                        for (int i = 0; i < codes.length; i++) {
                            codes[i] = Common.STORE_HEAD + codes[i] + ",";
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
            Date now = new Date();
            user.setModified_date(Common.DATETIME_FORMAT.format(now));
            user.setModifier(user_code);
            user.setIsactive(jsonObject.get("isactive").toString());
            user.setCan_login(jsonObject.get("can_login").toString());
            logger.info("------update user" + user.toString());
            String result = userService.update(user);
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
                String user_code = "";
                String corp_code = "";
                if (user != null) {
                    user_code = user.getUser_code();
                    corp_code = user.getCorp_code();
                    List<UserAchvGoal> goal = userService.selectUserAchvCount(user.getCorp_code(), user.getUser_code());
                    count = goal.size();
                    if (count > 0) {
                        msg = "请先删除用户的业绩目标，再删除用户" + user.getUser_code();
                        break;
                    }
                    userService.deleteUserQrcode(corp_code, user_code);
                }
                userService.delete(Integer.valueOf(ids[i]), user_code, corp_code);
            }
            if (count > 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
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
                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = userService.selectBySearchPart(page_number, page_size, corp_code, search_value, store_code, "", role_code);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    list = userService.selectBySearchPart(page_number, page_size, corp_code, search_value, "", area_code, role_code);
                } else {
                    list = userService.selectBySearch(request, page_number, page_size, Common.STORE_HEAD + Common.STORE_HEAD + "###", "");
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
            String corp_code = jsonObject.get("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            List<Store> list;
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM)) {
                //登录用户为admin或企业管理员
                list = storeService.getCorpStore(corp_code);
            } else if (role_code.equals(Common.ROLE_AM)) {
                //登录用户为区经
                String area_code = request.getSession().getAttribute("area_code").toString();
                String corp_code1 = request.getSession().getAttribute("corp_code").toString();
                String[] areaCodes = area_code.split(",");
                for (int i = 0; i < areaCodes.length; i++) {
                    areaCodes[i] = areaCodes[i].substring(1, areaCodes[i].length());
                }
                list = storeService.selectByAreaCode(corp_code1, areaCodes, Common.IS_ACTIVE_Y);
            } else {
                //登录用户为店长或导购
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = storeService.selectAll(store_code, corp_code, Common.IS_ACTIVE_Y);
            }
            stores.put("stores", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(stores.toString());
//            }
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
    @RequestMapping(value = "/UserCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String UserCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String user_code = jsonObject.get("user_code").toString();

            String corp_code = jsonObject.get("corp_code").toString();
            String current_corp_code = request.getSession(false).getAttribute("corp_code").toString();
            corp_code = (corp_code == null || corp_code.isEmpty()) ? current_corp_code : corp_code;
            List<User> existInfo = userService.userCodeExist(user_code, corp_code, "");
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
                    list = userService.getScreenPart(page_number, page_size, corp_code, map, store_code, "", role_code);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    list = userService.getScreenPart(page_number, page_size, corp_code, map, "", area_code, role_code);
                } else {
                    list = userService.selectBySearch(request, page_number, page_size, Common.STORE_HEAD + Common.STORE_HEAD + "###", "");
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
     * 员工管理
     * 重置密码
     */
    @RequestMapping(value = "/change_passwd", method = RequestMethod.POST)
    @ResponseBody
    public String changePasswd(HttpServletRequest request) {
        String user_code = request.getSession().getAttribute("user_code").toString();

        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
            id = jsonObject1.getString("id");
            String message = jsonObject1.get("message").toString();
            JSONObject jsonObject2 = new JSONObject(message);
            String password = jsonObject2.get("password").toString();
            int user_id = Integer.parseInt(jsonObject2.get("user_id").toString());

            password = CheckUtils.encryptMD5Hash(password);
            User user = userService.getUserById(user_id);
            user.setPassword(password);
            Date now = new Date();
            user.setModified_date(Common.DATETIME_FORMAT.format(now));
            user.setModifier(user_code);

            userService.updateUser(user);
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
     * 导购根据user_code生成二维码
     */
//    @RequestMapping(value = "/creatQrcode", method = RequestMethod.POST)
//    @ResponseBody
//    public String creatQrcode(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String user_id = request.getSession().getAttribute("user_code").toString();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("------------UserController creatQrcode" + jsString);
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = new JSONObject(message);
//            String corp_code = jsonObject.get("corp_code").toString();
//            String user_code = jsonObject.get("user_code").toString();
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage() + ex.toString());
//            logger.info(ex.getMessage() + ex.toString() + "========ex==========");
//        }
//        return dataBean.getJsonStr();
//    }

}
