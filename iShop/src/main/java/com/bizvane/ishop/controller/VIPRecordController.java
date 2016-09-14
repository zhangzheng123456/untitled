package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.entity.VipLabel;
import com.bizvane.ishop.entity.VipRecord;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
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
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/VIP")
public class VIPRecordController {
    @Autowired
    private VipRecordService vipRecordService;

    private static final Logger log = Logger.getLogger(VIPRecordController.class);

    String id;

    /***
     * 导出数据
     */
    @RequestMapping(value = "/callback/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExeclCallback(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<VipRecord> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipRecordService.selectBySearch(1, 30000, "", search_value);
                } else {

                    list = vipRecordService.selectBySearch(1, 30000, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.contains(Common.ROLE_SYS)) {
                    list = vipRecordService.selectAllVipRecordScreen(1, 30000, "", map);
                } else {
                    list = vipRecordService.selectAllVipRecordScreen(1, 30000, corp_code, map);
                }
            }
            List<VipRecord> vipRecords = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(vipRecords);
            if (vipRecords.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json,vipRecords, map, response, request);
            org.json.JSONObject result = new org.json.JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
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

    /**
     * 回访记录管理
     * 列表
     */
    @RequestMapping(value = "/callback/list", method = RequestMethod.GET)
    @ResponseBody
    public String callBackManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));

            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipRecord> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.vipRecordService.selectBySearch(page_number, page_size, "", "");
            } else {

                list = vipRecordService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

//    /**
//     * 回访记录管理
//     * 新增
//     */
//    @RequestMapping(value = "/callback/add", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String addCallBack(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String user_id = request.getSession(false).getAttribute("user_code").toString();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            VipRecord VipRecord = WebUtils.JSON2Bean(jsonObject, VipRecord.class);
//            Date now = new Date();
//            VipRecord.setModified_date(Common.DATETIME_FORMAT.format(now));
//            VipRecord.setModifier(user_id);
//            this.vipRecordService.insert(VipRecord);
//            dataBean.setId(id);
//            dataBean.setMessage("add successs");
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 回访记录管理
     * 编辑前获取数据
     */

    @RequestMapping(value = "/callback/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectCallBack(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).getAttribute("user_id").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            //  int VipRecord_id = Integer.parseInt(jsonObject.getString("id"));
            int VipRecord_id = jsonObject.getInt("id");
            VipRecord VipRecord = this.vipRecordService.getVipRecord(VipRecord_id);
            if (VipRecord != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(JSON.toJSONString(VipRecord));
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("not found VipRecord error");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

//
//    /**
//     * 回访记录管理
//     * 编辑
//     */
//    @RequestMapping(value = "/callback/edit", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String editCallBack(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String user_id = request.getSession(false).getAttribute("user_code").toString();
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            VipRecord VipRecord = WebUtils.JSON2Bean(jsonObject, VipRecord.class);
//            VipRecord.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
//            VipRecord.setModifier(user_id);
//            this.vipRecordService.update(VipRecord);
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage("edit success!!!s");
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 回访记录管理
     * 查找
     */
    @RequestMapping(value = "/callback/find", method = RequestMethod.POST)
    @ResponseBody
    public String findCallBack(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonobj = new org.json.JSONObject(jsString);
            String message = jsonobj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipRecord> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = vipRecordService.selectBySearch(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipRecordService.selectBySearch(page_number, page_size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 筛选
     */
    @RequestMapping(value = "/callback/screen", method = RequestMethod.POST)
    @ResponseBody
    public String findCallBackScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonobj = new org.json.JSONObject(jsString);
            String message = jsonobj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String screen = jsonObject.get("screen").toString();
//            org.json.JSONObject jsonScreen = new org.json.JSONObject(screen);
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipRecord> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = vipRecordService.selectAllVipRecordScreen(page_number, page_size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipRecordService.selectAllVipRecordScreen(page_number, page_size, corp_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 删除
     */
    @RequestMapping(value = "/callback/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String callbackDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String[] ids = jsonObject.get("id").toString().split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                this.vipRecordService.delete(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
