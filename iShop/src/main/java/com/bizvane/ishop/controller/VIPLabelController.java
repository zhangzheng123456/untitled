package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;

import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;

import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.VipLabel;

import com.bizvane.ishop.service.*;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
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
@Controller
@RequestMapping("/VIP/label")
public class VIPLabelController {

    @Autowired
    private VipLabelService vipLabelService;
    @Autowired
    private ViplableGroupService viplableGroupService;
    @Autowired
    private CorpService corpService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    private BaseService baseService;
    private static final Logger log = Logger.getLogger(VIPLabelController.class);

    String id;

//    /**
//     * 会员标签管理
//     * 列表
//     */
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String VIPLabelManage(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<VipLabel> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = vipLabelService.selectBySearch(page_number, page_size, "", "");
//            } else {
//                list = vipLabelService.selectBySearch(page_number, page_size, corp_code, "");
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//            ex.printStackTrace();
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 列表展示
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String userActionList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            System.out.println("======会员标签mongeDB===== ");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
           DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find();

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "modified_date", -1);
            } else {
                Map keyMap = new HashMap();
                keyMap.put("corp_code", corp_code);
                BasicDBObject ref = new BasicDBObject();
                ref.putAll(keyMap);
                DBCursor dbCursor1 = cursor.find(ref);

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "modified_date", -1);
            }

            ArrayList list = MongoHelperServiceImpl.dbCursorToList_labelType(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            //logger.info(ex.getMessage());
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
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<VipLabel> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = vipLabelService.selectBySearch(1, Common.EXPORTEXECLCOUNT, "", search_value);
                } else {
                    list = vipLabelService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipLabelService.selectAllVipScreen(1, Common.EXPORTEXECLCOUNT, "", map);
                } else {
                    list = this.vipLabelService.selectAllVipScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map);
                }
            }
            List<VipLabel> vipLabels = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(vipLabels);
            if (vipLabels.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json,vipLabels, map, response, request);
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
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }



    /**
     * 会员标签管理
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addVIPLabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
        String role_code = request.getSession(false).getAttribute("role_code").toString();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String messsage = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(messsage);
            VipLabel vipLabel = WebUtils.JSON2Bean(jsonObject, VipLabel.class);
            Date now = new Date();
            vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipLabel.setModifier(user_id);
            vipLabel.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipLabel.setCreater(user_id);
            if (Common.ROLE_SYS.equals(role_code) && corp_code.equals(vipLabel.getCorp_code())) {
                vipLabel.setLabel_type("sys");
            } else if(role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_SYS)){
                vipLabel.setLabel_type("org");
            }else {
                vipLabel.setLabel_type("user");
            }
            String existInfo = vipLabelService.insert(vipLabel);
            if (existInfo.contains(Common.DATABEAN_CODE_SUCCESS)) {
                List<VipLabel> vipLabels = vipLabelService.selectViplabelByName(vipLabel.getCorp_code(),vipLabel.getLabel_name(),vipLabel.getIsactive());
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(String.valueOf(vipLabels.get(0).getId()));

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
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(messsage);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_会员标签";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("label_group_code").toString();
                String t_name = action_json.get("label_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("标签名称已被使用");
            }

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员标签管理
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editVIPLabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String role_code = request.getSession(false).getAttribute("role_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonobj = new org.json.JSONObject(jsString);
            id = jsonobj.get("id").toString();
            String message = jsonobj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            VipLabel vipLabel = WebUtils.JSON2Bean(jsonObject, VipLabel.class);
            int label_id = vipLabel.getId();
            String label_type = vipLabelService.getVipLabelById(label_id).getLabel_type();
            if (!role_code.equals(Common.ROLE_SYS) && label_type.equals(Common.VIP_LABEL_TYPE_SYS)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("对不起，您不可以修改系统标签");
            } else {
                Date now = new Date();
                vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipLabel.setModifier(user_id);

                String result = vipLabelService.update(vipLabel);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("更改成功");

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
                    String function = "会员管理_会员标签";
                    String action = Common.ACTION_UPD;
                    String t_corp_code = action_json.get("corp_code").toString();
                    String t_code = action_json.get("label_group_code").toString();
                    String t_name = action_json.get("label_name").toString();
                    String remark = "";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                    //-------------------行为日志结束-----------------------------------------------------------------------------------
                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                }
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("会员标签更新失败");
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
//
//    /**
//     * 编辑标签前，获取数据
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/select", method = RequestMethod.POST)
//    @ResponseBody
//    public String findVipLabelById(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String data = null;
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.getString("id");
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            int vip_tag_id = Integer.parseInt(jsonObject.getString("id"));
//            VipLabel vipLabel = vipLabelService.getVipLabelById(vip_tag_id);
//            data = JSON.toJSONString(vipLabel);
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage(data);
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
    /**
     * 编辑标签前，获取数据
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String getfindById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");

            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String errorLog_id = jsonObject.get("id").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);
            DBObject deleteRecord = new BasicDBObject();
            deleteRecord.put("_id",errorLog_id);
            DBCursor dbObjects = cursor.find(deleteRecord);
            DBObject vip_label=null;
            while (dbObjects.hasNext()) {
                vip_label  = dbObjects.next();
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(vip_label.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }
        return dataBean.getJsonStr();
    }
//    /**
//     * 会员标签类型管理
//     * 删除
//     */
//    @RequestMapping(value = "/delete", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String findVIPLabelDelete(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String messageType_id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            String[] ids = jsonObject.get("id").toString().split(",");
//            for (int i = 0; ids != null && i < ids.length; i++) {
//                VipLabel vipLabelById = vipLabelService.getVipLabelById(Integer.parseInt(ids[i]));
//                this.vipLabelService.delete(Integer.parseInt(ids[i]));
//                //----------------行为日志开始------------------------------------------
//                /**
//                 * mongodb插入用户操作记录
//                 * @param operation_corp_code 操作者corp_code
//                 * @param operation_user_code 操作者user_code
//                 * @param function 功能
//                 * @param action 动作
//                 * @param corp_code 被操作corp_code
//                 * @param code 被操作code
//                 * @param name 被操作name
//                 * @throws Exception
//                 */
//                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
//                String operation_user_code = request.getSession().getAttribute("user_code").toString();
//                String function = "会员管理_会员标签";
//                String action = Common.ACTION_DEL;
//                String t_corp_code = vipLabelById.getCorp_code();
//                String t_code = vipLabelById.getLabel_group_code();
//                String t_name = vipLabelById.getLabel_name();
//                String remark = "";
//                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
//                //-------------------行为日志结束-----------------------------------------------------------------------------------
//
//            }
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage("scuccess");
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }


    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
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

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);

            for (int i = 0; i < ids.length; i++) {
                DBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", ids[i]);

                DBCursor dbObjects = cursor.find(deleteRecord);
                String corp_code = "";

                String label_name = "";
                while (dbObjects.hasNext()) {
                    DBObject errorlog = dbObjects.next();
                    if (errorlog.containsField("corp_code")) {
                        corp_code = errorlog.get("corp_code").toString();
                    }
                    if (errorlog.containsField("label_name")) {
                        label_name = errorlog.get("label_name").toString();
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
                String function = "会员管理_会员标签";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp_code;
                String t_code = ids[i];
                String t_name = label_name;
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
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



    /**
     * 会员标签管理
     * 判断企业内标签名称是否唯一
     */
    @RequestMapping(value = "/VipLabelNameExist")
    @ResponseBody
    public String VipLabelNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObject1 = JSONObject.parseObject(jsString);
            String message = jsonObject1.get("message").toString();
            JSONObject jsonObject2 = JSONObject.parseObject(message);
            String label_name = jsonObject2.getString("label_name");
            String corp_code = jsonObject2.getString("corp_code");
            List<VipLabel> existInfo = this.vipLabelService.VipLabelNameExist(corp_code, label_name);

            if (existInfo.size() == 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("标签名称未被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("标签名称已被使用");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     *
     * 页面查找
     */
    @RequestMapping(value = "/find", method = RequestMethod.POST)
    @ResponseBody
    public String getsearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);

            String[] column_names = new String[]{"corp_name","label_name"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);

            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"modified_date",-1);
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_labelType(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());

        }
        return dataBean.getJsonStr();
    }
//    /**
//     * 会员标签管理
//     * 查找
//     */
//    @RequestMapping(value = "/find", method = RequestMethod.POST)
//    @ResponseBody
//    public String findVIPLabelFind(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.getString("id");
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            int page_Number = jsonObject.getInt("pageNumber");
//            int page_Size = jsonObject.getInt("pageSize");
//            String search_value = jsonObject.getString("searchValue").toString();
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<VipLabel> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = this.vipLabelService.selectBySearch(page_Number, page_Size, "", search_value);
//            } else {
//                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//                list = this.vipLabelService.selectBySearch(page_Number, page_Size, corp_code, search_value);
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }


    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
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
            BasicDBObject queryCondition = MongoHelperServiceImpl.andUserOperScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_labelType(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
           // logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
//    /**
//     * 会员标签管理
//     * 筛选
//     */
//    @RequestMapping(value = "/screen", method = RequestMethod.POST)
//    @ResponseBody
//    public String selectAllVipScreen(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.getString("id");
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            int page_Number = jsonObject.getInt("pageNumber");
//            int page_Size = jsonObject.getInt("pageSize");
////            String screen = jsonObject.get("screen").toString();
////            org.json.JSONObject jsonScreen = new org.json.JSONObject(screen);
//            Map<String, String> map = WebUtils.Json2Map(jsonObject);
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<VipLabel> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = this.vipLabelService.selectAllVipScreen(page_Number, page_Size, "", map);
//            } else {
//                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//                list = this.vipLabelService.selectAllVipScreen(page_Number, page_Size, corp_code, map);
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    //热门标签
    @RequestMapping(value = "/findHotViplabel", method = RequestMethod.POST)
    @ResponseBody
    public String findHotViplabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String corp_code = jsonObject.getString("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            List<VipLabel> hotViplabel = vipLabelService.findHotViplabel(corp_code);
            List<VipLabel> vipLabelList = vipLabelService.selectLabelByVip(corp_code,vip_id);
            for (VipLabel vipLabel:hotViplabel) {
              vipLabel.setLabel_sign("N");
            }
            for(int i=0;i<vipLabelList.size();i++){
                for (int j=0;j<hotViplabel.size();j++){
                    if(vipLabelList.get(i).getLabel_name().equals(hotViplabel.get(j).getLabel_name())){
                        hotViplabel.get(j).setLabel_sign("Y");
                    }
                }
            }
            result.put("list", JSON.toJSONString(hotViplabel));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/findViplabelByType", method = RequestMethod.POST)
    @ResponseBody
    public String findViplabelByType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_Number = jsonObject.getInt("pageNumber");
            int page_Size = 50;
            String search_value = jsonObject.getString("searchValue").toString();
            String type = jsonObject.getString("type").toString();
            String corp_code = jsonObject.getString("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            List<VipLabel> vipLabelList = vipLabelService.selectLabelByVip(corp_code,vip_id);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipLabel> list=null;

          if(type.equals("1")){
              list= vipLabelService.findViplabelByType(page_Number,page_Size,corp_code,"",search_value);
          }else if(type.equals("2")){
              list= vipLabelService.findViplabelByType(page_Number,page_Size,corp_code,"org","");
              for (VipLabel vipLabel:list.getList()) {
                  vipLabel.setLabel_sign("N");
              }
              for(int i=0;i<vipLabelList.size();i++){
                  for (int j=0;j<list.getList().size();j++){
                      if(vipLabelList.get(i).getLabel_name().equals(list.getList().get(j).getLabel_name())){
                          list.getList().get(j).setLabel_sign("Y");
                      }
                  }
              }
          }else if(type.equals("3")){
              list= vipLabelService.findViplabelByType(page_Number,page_Size,corp_code,"user","");
              for (VipLabel vipLabel:list.getList()) {
                  vipLabel.setLabel_sign("N");
              }
              for(int i=0;i<vipLabelList.size();i++){
                  for (int j=0;j<list.getList().size();j++){
                      if(vipLabelList.get(i).getLabel_name().equals(list.getList().get(j).getLabel_name())){
                          list.getList().get(j).setLabel_sign("Y");
                      }
                  }
              }
          }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/addRelViplabel", method = RequestMethod.POST)
    @ResponseBody
    public String checkRelViplablel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code1 = request.getSession().getAttribute("corp_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String corp_code = jsonObject.getString("corp_code").toString();
            String vip_code = jsonObject.getString("vip_code").toString();
//            String label_id = jsonObject.getString("label_id").toString();
            String label_name = jsonObject.getString("label_name").toString();
            String store_code = jsonObject.getString("store_code").toString();
            String user_id = request.getSession().getAttribute("user_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            String result_add="";
            List<VipLabel> vipLabelList = vipLabelService.VipLabelNameExist(corp_code, label_name);
            if (vipLabelList.size() == 0) {
                List<ViplableGroup> viplableGroups1 = viplableGroupService.checkNameOnly(corp_code, "默认分组", Common.IS_ACTIVE_Y);
                List<ViplableGroup> viplableGroups2 = viplableGroupService.checkCodeOnly(corp_code, "0001", Common.IS_ACTIVE_Y);
                if(viplableGroups1.size()==0 && viplableGroups2.size()==0){
                    ViplableGroup viplableGroup=new ViplableGroup();
                    viplableGroup.setCorp_code(corp_code);
                    viplableGroup.setLabel_group_code("0001");
                    viplableGroup.setLabel_group_name("默认分组");
                    viplableGroup.setRemark("默认分组");
                    viplableGroup.setIsactive("Y");
                    Date date = new Date();
                    viplableGroup.setCreated_date(Common.DATETIME_FORMAT.format(date));
                    viplableGroup.setCreater(user_id);
                    viplableGroup.setModified_date(Common.DATETIME_FORMAT.format(date));
                    viplableGroup.setModifier(user_id);
                    viplableGroupService.addViplableGroup(viplableGroup);
                }
                VipLabel vipLabel = WebUtils.JSON2Bean(jsonObject, VipLabel.class);
                vipLabel.setLabel_group_code("0001");
                Date now = new Date();
                vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipLabel.setModifier(user_id);
                vipLabel.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipLabel.setCreater(user_id);

                String role_code = request.getSession(false).getAttribute("role_code").toString();
                if (Common.ROLE_SYS.equals(role_code) && corp_code1.equals(corp_code)) {
                    vipLabel.setLabel_type("sys");
                } else if (role_code.equals(Common.ROLE_GM)){
                    vipLabel.setLabel_type("org");
                }else {
                    vipLabel.setLabel_type("user");
                }
                String existInfo2 = vipLabelService.insert(vipLabel);
                if (existInfo2.contains(Common.DATABEAN_CODE_SUCCESS)) {
                    String label_name2 = vipLabel.getLabel_name();
                    String corp_code2 = vipLabel.getCorp_code();
                    List<VipLabel> viplabelID = vipLabelService.findViplabelID(corp_code2, label_name2);
                    String label_id2=viplabelID.get(0).getId()+"";
                    RelViplabel relViplabel = WebUtils.JSON2Bean(jsonObject, RelViplabel.class);
                    relViplabel.setLabel_id(label_id2);
                    Date date = new Date();
                    relViplabel.setCreated_date(Common.DATETIME_FORMAT.format(date));
                    relViplabel.setCreater(user_id);
                    relViplabel.setModified_date(Common.DATETIME_FORMAT.format(date));
                    relViplabel.setModifier(user_id);
                    int i = vipLabelService.addRelViplabel(relViplabel);
                    if(i>0){
                        String vip_code2 = relViplabel.getVip_code();
                        List<RelViplabel> relViplabels = vipLabelService.checkRelViplablel(corp_code,vip_code2,label_id2);
                        int id1 = relViplabels.get(0).getId();
                        result_add = id1+"";
                    }
                    result.put("list", JSON.toJSONString(result_add));
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage(result.toString());
                }
            }else {
                String label_id = String.valueOf(vipLabelList.get(0).getId());
                List<RelViplabel> relViplabels = vipLabelService.checkRelViplablel(corp_code, vip_code, label_id);
                if (relViplabels.size() > 0) {
                    result_add = "该会员标签已存在";
                    result.put("list", JSON.toJSONString(result_add));
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage(result.toString());
                } else {
                    RelViplabel relViplabel = new RelViplabel();
                    relViplabel.setLabel_id(label_id);
                    relViplabel.setCorp_code(corp_code);
                    relViplabel.setVip_code(vip_code);
                    relViplabel.setStore_code(store_code);
                    //------------操作日期-------------
                    Date date = new Date();
                    relViplabel.setCreated_date(Common.DATETIME_FORMAT.format(date));
                    relViplabel.setCreater(user_id);
                    relViplabel.setModified_date(Common.DATETIME_FORMAT.format(date));
                    relViplabel.setModifier(user_id);
                    int i = vipLabelService.addRelViplabel(relViplabel);
                    String id1 = "";
                    if (i > 0) {
                        List<RelViplabel> relViplabels1 = vipLabelService.checkRelViplablel(corp_code, vip_code, label_id);
                        id1 = String.valueOf(relViplabels1.get(0).getId());
                        result_add=id1;
                    }
                    result.put("list", JSON.toJSONString(result_add));
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id1);
                    dataBean.setMessage(result.toString());
                }
            }

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/delRelViplabel", method = RequestMethod.POST)
    @ResponseBody
    public String delRelViplabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String rid = jsonObject.getString("rid").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            String result_add="";
            int i = vipLabelService.delRelViplabel(rid);
            if(i>0){
                result_add="删除成功";
            }
            result.put("list", JSON.toJSONString(result_add));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
