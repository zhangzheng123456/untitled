package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 店铺业绩目标管理
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Controller
@RequestMapping("/storeAchvGoal")
public class StoreAchvGoalController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    @Autowired
    StoreAchvGoalService storeAchvGoalService = null;
    @Autowired
    FunctionService functionService = null;
    @Autowired
    private TableManagerService managerService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private StoreService storeService;
    String id;

    /**
     * 用户管理
     * 列表展示
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String getStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            String function_code = request.getParameter("funcCode");
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code, user_code, group_code, role_code, function_code);

            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<StoreAchvGoal> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = storeAchvGoalService.selectBySearch(page_number, page_size, "", "", "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", "", "");
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession().getAttribute("area_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, area_code, "", "");
            } else {
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", store_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标修改或添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            StoreAchvGoal storeAchvGoal1 = new StoreAchvGoal();
            storeAchvGoal1.setCorp_code(jsonObject.get("corp_code").toString());
            storeAchvGoal1.setStore_name(jsonObject.get("store_name").toString());
            storeAchvGoal1.setStore_code(jsonObject.get("store_code").toString());
            storeAchvGoal1.setTarget_amount(jsonObject.get("achv_goal").toString());
            String achv_type = jsonObject.get("achv_type").toString();
            storeAchvGoal1.setTime_type(achv_type);

            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
                String time = jsonObject.get("end_time").toString();
                String week = TimeUtils.getWeek(time);
                storeAchvGoal1.setTarget_time(week);
            } else {
                storeAchvGoal1.setTarget_time(jsonObject.get("end_time").toString());
            }
            Date now = new Date();
            storeAchvGoal1.setModifier(user_id);
            storeAchvGoal1.setModified_date(Common.DATETIME_FORMAT.format(now));
            storeAchvGoal1.setCreater(user_id);
            storeAchvGoal1.setCreated_date(Common.DATETIME_FORMAT.format(now));
            storeAchvGoal1.setIsactive(jsonObject.get("isactive").toString());
            String result = storeAchvGoalService.insert(storeAchvGoal1);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("edit success");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("店铺"  +storeAchvGoal1.getStore_code()+ "业绩目标已经设定");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标
     * 选择业绩
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String editbefore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int store_id = Integer.parseInt(jsonObject.get("id").toString());
            StoreAchvGoal storeAchvGoal = storeAchvGoalService.selectlById(store_id);
//            JSONObject result = new JSONObject();
//            result.put("storeAchvGoal", storeAchvGoal);
            org.json.JSONObject result = new org.json.JSONObject();
            result.put("storeAchvGoal", JSON.toJSONString(storeAchvGoal));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(Common.DATABEAN_CODE_ERROR);
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_code");
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);

            StoreAchvGoal storeAchvGoal = new StoreAchvGoal();
            storeAchvGoal.setId(Integer.parseInt(jsonObject.get("id").toString()));
            storeAchvGoal.setCorp_code(jsonObject.get("corp_code").toString());
            storeAchvGoal.setStore_name(jsonObject.get("store_name").toString());
            storeAchvGoal.setStore_code(jsonObject.get("store_code").toString());
            storeAchvGoal.setTarget_amount(jsonObject.get("achv_goal").toString());
            String achv_type = jsonObject.get("achv_type").toString();
            storeAchvGoal.setTime_type(achv_type);
            String time = jsonObject.get("end_time").toString();
            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
                String week = TimeUtils.getWeek(time);
                storeAchvGoal.setTarget_time(week);
            } else {
                storeAchvGoal.setTarget_time(time);
            }
            Date now = new Date();
            storeAchvGoal.setModifier(user_id);
            storeAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));

            storeAchvGoal.setIsactive(jsonObject.get("isactive").toString());
            String result = storeAchvGoalService.update(storeAchvGoal);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("edit success");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("店铺" + storeAchvGoal.getStore_code()+ "的业绩目标已经设定");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String storeAchvGoal_id = jsonObject.get("id").toString();
            String[] ids = storeAchvGoal_id.split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                storeAchvGoalService.deleteById(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
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
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_code").toString());

            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<StoreAchvGoal> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = storeAchvGoalService.selectBySearch(page_number, page_size, "", "", "", search_value);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", "", search_value);
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession().getAttribute("area_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, area_code, "", search_value);
            } else {
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", store_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
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
        String errormessage="";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<StoreAchvGoal> list = null;
            if(screen.equals("")) {
                if (role_code.contains(Common.ROLE_SYS)) {
                    list = storeAchvGoalService.selectBySearch(1, 30000, "", "", "", search_value);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeAchvGoalService.selectBySearch(1, 30000, corp_code, "", "", search_value);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    list = storeAchvGoalService.selectBySearch(1, 30000, corp_code, area_code, "", search_value);
                } else {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = storeAchvGoalService.selectBySearch(1, 30000, corp_code, "", store_code, search_value);
                }
            }else{
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeAchvGoalService.getAllStoreAchvScreen(1, 30000, "", "", "", map);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeAchvGoalService.getAllStoreAchvScreen(1, 30000, corp_code, "", "", map);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    list = storeAchvGoalService.getAllStoreAchvScreen(1, 30000, corp_code, area_code, "", map);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeAchvGoalService.getAllStoreAchvScreen(1, 30000, corp_code, "", store_code, map);
                }
            }
            List<StoreAchvGoal> storeAchvGoals = list.getList();
            if(storeAchvGoals.size()>=29999){
                errormessage="导出数据过大";
                int i=9/0;
            }
            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(storeAchvGoals, map, response, request);
            JSONObject result = new JSONObject();
            if(pathname==null||pathname.equals("")){
                errormessage="数据异常，导出失败";
                int a=8/0;
            }
            result.put("path",JSON.toJSONString("lupload/"+pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /***
     * Execl增加
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String result = "";
        Workbook rwb=null;
        try {
          rwb= Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = rs.getColumns();//得到所有的列
            int rows = rs.getRows();//得到所有的行
            int actualRows = LuploadHelper.getRightRows(rs);
            if(actualRows != rows){
                if(rows-actualRows==1){
                    result = "：第"+rows+"行存在空白行,请删除";
                }else{
                    result = "：第"+(actualRows+1)+"行至第"+rows+"存在空白行,请删除";
                }
                int i = 5 / 0;
            }
            if(rows<4){
                result="：请从模板第4行开始插入正确数据";
                int i=5/0;
            }
            if(rows>9999){
                result="：数据量过大，导入失败";
                int i=5 /0;
            }
            Cell[] column3 = rs.getColumn(0);
            Pattern pattern1 = Pattern.compile("C\\d{5}");
            if(!role_code.equals(Common.ROLE_SYS)){
                for (int i=3;i<column3.length;i++){
                    if(!column3[i].getContents().toString().equals(corp_code)){
                        result = "：第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString());
                    if (matcher.matches() == false) {
                        result = "：第" + (i + 1) + "行企业编号格式有误";
                        int b = 5 / 0;
                        break;
                    }

                }
            }
            for (int i = 3; i < column3.length; i++) {
                Matcher matcher = pattern1.matcher(column3[i].getContents().toString());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行企业编号格式有误";
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
            Cell[] column2 = rs.getColumn(1);
            for (int i = 3; i < column2.length; i++) {
                Store store = storeService.getStoreByCode(column3[i].getContents().toString(), column2[i].getContents().toString(), "");
                if (store == null) {
                    result = "：第" + (i + 1) + "行店铺编号不存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column = rs.getColumn(3);
            for (int i = 3; i < column.length; i++) {
                if (!column[i].getContents().toString().equals("D") && !column[i].getContents().toString().equals("W") && !column[i].getContents().toString().equals("M") && !column[i].getContents().toString().equals("Y")) {
                    result = "：第" + (i + 1) + "行的业绩日期类型缩写有误";
                    int b = 5 / 0;
                    break;
                }
            }
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {

                    StoreAchvGoal storeAchvGoal = new StoreAchvGoal();
                    storeAchvGoal.setCorp_code(rs.getCell(j++, i).getContents());
                    storeAchvGoal.setStore_code(rs.getCell(j++, i).getContents());
                    storeAchvGoal.setTarget_amount(rs.getCell(j++, i).getContents());
                    String target_type = rs.getCell(j++, i).getContents().toString();
                    storeAchvGoal.setTime_type(target_type);
                    String cellTypeForDate = LuploadHelper.getCellTypeForDate(rs.getCell(j++, i),target_type);
                    storeAchvGoal.setTarget_time(cellTypeForDate);
                    if (rs.getCell(j++, i).getContents().toString().toUpperCase().equals("N")) {
                        storeAchvGoal.setIsactive("N");
                    } else {
                        storeAchvGoal.setIsactive("Y");
                    }
                    storeAchvGoal.setCreater(user_id);
                    Date now = new Date();
                    storeAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    storeAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
                    storeAchvGoal.setModifier(user_id);
                    result = String.valueOf(storeAchvGoalService.insert(storeAchvGoal));
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
        }finally {
            if(rwb!=null){
                rwb.close();
            }
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);
            id = jsonObject.getString("id");
            String message = jsonObject.get("message").toString();
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject1.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject1.get("pageSize").toString());
//            String screen = jsonObject.get("screen").toString();
//            JSONObject jsonScreen = new JSONObject(screen);
            Map<String, String> map = new WebUtils().Json2Map(jsonObject1);
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new org.json.JSONObject();
            PageInfo<StoreAchvGoal> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, "", "", "", map);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, corp_code, "", "", map);
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession(false).getAttribute("area_code").toString();
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, corp_code, area_code, "", map);
            } else {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, corp_code, "", store_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }
}
