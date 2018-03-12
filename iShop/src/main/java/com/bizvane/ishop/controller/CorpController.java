package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.Store;
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
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouying on 2016-04-20.
 */


/**
 * 企业管理
 */

@Controller
@RequestMapping("/corp")
public class CorpController {

    private static final Logger logger = Logger.getLogger(CorpController.class);

    String id;

    @Autowired
    private CorpService corpService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private TableManagerService managerService;
    @Autowired
    private BaseService baseService;

    /*
    * 列表
    * */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String cropManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            org.json.JSONObject info = new org.json.JSONObject();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员(官方画面)
                int page_number = Integer.parseInt(request.getParameter("pageNumber"));
                int page_size = Integer.parseInt(request.getParameter("pageSize"));
                PageInfo<Corp> corpInfo = corpService.selectAllCorp(page_number, page_size, "");
                info.put("list", JSON.toJSONString(corpInfo));
            } else {
                //用户画面
                Corp corp = corpService.selectByCorpId(0, corp_code, "");
                info.put("list", JSON.toJSONString(corp));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(info.toString());
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
    public String addCrop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = corpService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                String corp_code = jsonObject.get("corp_code").toString().trim();
                String isactive = jsonObject.get("isactive").toString();
                Corp corp = corpService.selectByCorpId(0, corp_code, isactive);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(corp.getId()));

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
                String function = "企业管理";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("corp_code").toString();
                String t_name = action_json.get("corp_name").toString();
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
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editCrop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = corpService.update(message, user_id);
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
                String function = "企业管理";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("corp_code").toString();
                String t_name = action_json.get("corp_name").toString();
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
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("info--------" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 删除
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_ids = jsonObject.get("id").toString();
            boolean flag = false;
            int corp_id = -1;
            String[] ids = corp_ids.split(",");
            String msg = null;
            for (int i = 0; i < ids.length; i++) {
                corp_id = Integer.valueOf(ids[i]);
                Corp corp = this.corpService.selectByCorpId(corp_id, "", "");
                if (corp != null) {
                    logger.info("inter---------------" + Integer.valueOf(ids[i]));
                    int count = 0;
                    count = corpService.getAreaCount(corp.getCorp_code());
                    if (count > 0) {
                        msg = "企业" + corp.getCorp_code() + "下有未处理的区域，请先处理区域";
                        break;
                    }
                    count = this.corpService.getBranCount(corp.getCorp_code());
                    if (count > 0) {
                        msg = "企业" + corp.getCorp_code() + "下有未处理的品牌，请先处理品牌";
                        break;
                    }
                    count = this.corpService.getGroupCount(corp.getCorp_code());
                    if (count > 0) {
                        msg = "企业" + corp.getCorp_code() + "下有未处理的群组，请先处理群组";
                        break;
                    }
                    count = this.corpService.getGoodsCount(corp.getCorp_code());
                    if (count > 0) {
                        msg = "企业" + corp.getCorp_code() + "下有未处理的商品，请先处理商品";
                        break;
                    }
                    corpService.deleteCorpWechat("", corp.getCorp_code());
                }
                corpService.deleteByCorpId(Integer.valueOf(ids[i]));
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
                String function = "企业管理";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp.getCorp_code();
                String t_code = corp.getCorp_code();
                String t_name = corp.getCorp_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
            if (msg != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(msg);
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            //	return "Error deleting the user:" + ex.toString();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 企业选择
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_id = jsonObject.get("id").toString();
            Corp corp = corpService.selectByCorpId(Integer.parseInt(corp_id), "", "");
            List<CorpWechat> corpWechat = corpService.getWByCorp(corp.getCorp_code());
            corp.setWechats(corpWechat);
            data = JSON.toJSONString(corp);
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("企业信息异常");
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
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
            }
            JSONObject result = new JSONObject();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员(官方画面)
                PageInfo<Corp> corpInfo = corpService.selectAllCorp(page_number, page_size, search_value);
                result.put("list", JSON.toJSONString(corpInfo));
            }
//            else if(role_code.equals((Common.ROLE_CM))){
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>"+manager_corp);
//                PageInfo<Corp> corpInfo = corpService.selectAllCorp(page_number, page_size, search_value,manager_corp);
//                result.put("list", JSON.toJSONString(corpInfo));
//            }
            else {
                //用户画面
                Corp corp = corpService.selectByCorpId(0, corp_code, "");
                result.put("list", JSON.toJSONString(corp));
            }
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

    /**
     * 查看企业所有店铺
     */
    @RequestMapping(value = "/store", method = RequestMethod.POST)
    @ResponseBody
    public String getStore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String corp_code = jsonObject.get("corp_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Store> list = storeService.getAllStore(request, page_number, page_size, corp_code, "", "", "","","","","All");
            result.put("stores", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 测试企业名是否被使用
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/corpNameExist", method = RequestMethod.POST)
    @ResponseBody
    public String CorpExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_name = jsonObject.get("corp_name").toString();
            String existInfo = corpService.getCorpByCorpName(corp_name, Common.IS_ACTIVE_Y);
            if (existInfo.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("企业名已被使用");
                //  dataBean.setCode();
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("企业名不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/corpCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String Corp_codeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();

            Corp corp = corpService.selectByCorpId(0, corp_code, Common.IS_ACTIVE_Y);
            if (corp != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("企业编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("企业编号不存在");
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
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            //系统管理员(官方画面)
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<Corp> corpInfo = null;
            if (!role_code.equals(Common.ROLE_SYS)){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage("无导出权限");
                return dataBean.getJsonStr();
            }
            if (screen.equals("")) {
                corpInfo = corpService.selectAllCorp(1, Common.EXPORTEXECLCOUNT, search_value);
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                corpInfo = corpService.selectAllCorpScreen(1, Common.EXPORTEXECLCOUNT, map);
            }
            List<Corp> corps = corpInfo.getList();
            for (Corp corp : corps) {
                String contact = corp.getContact();
                String replaceStr = WebUtils.StringFilter(contact);
                corp.setContact(replaceStr);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(corps);
            if (corps.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, corps, map, response, request,"企业列表");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
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
    @Transactional()
    @ResponseBody()
    public String addByExecl(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException, UnsupportedEncodingException {

        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String result = "";
        Workbook rwb = null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = 6;//得到所有的列
            int rows = rs.getRows();//得到所有的行
            //        int actualRows = LuploadHelper.getRightRows(rs);
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
            String onlyCell1 = LuploadHelper.CheckOnly(rs.getColumn(0));
            if (onlyCell1.equals("存在重复值")) {
                result = "：Execl中企业编号存在重复值";
                int b = 5 / 0;
            }
            String onlyCell2 = LuploadHelper.CheckOnly(rs.getColumn(1));
            if (onlyCell2.equals("存在重复值")) {
                result = "：Execl中企业名称存在重复值";
                int b = 5 / 0;
            }
            Cell[] column = rs.getColumn(0);
            Pattern pattern = Pattern.compile("C\\d{5}");
            for (int i = 3; i < column.length; i++) {
                if (column[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Matcher matcher = pattern.matcher(column[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行企业编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Corp corp = corpService.selectByCorpId(0, column[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (corp != null) {
                    result = "：第" + (i + 1) + "行企业编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column4 = rs.getColumn(4);
            Pattern pattern4 = Pattern.compile("(^(\\d{3,4}-)?\\d{7,8})$|(1[3,4,5,7,8]{1}\\d{9})");
            for (int i = 3; i < column4.length; i++) {
                if (column4[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Matcher matcher = pattern4.matcher(column4[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行电话格式有误";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column1 = rs.getColumn(1);
            for (int i = 3; i < column1.length; i++) {
                if (column1[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                String existInfo = corpService.getCorpByCorpName(column1[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (existInfo.contains(Common.DATABEAN_CODE_ERROR)) {
                    result = "：第" + (i + 1) + "行企业名称已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            ArrayList<Corp> corps = new ArrayList<Corp>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    String corp_code = rs.getCell(j++, i).getContents().toString().trim();
                    String corp_name = rs.getCell(j++, i).getContents().toString().trim();
                    String address = rs.getCell(j++, i).getContents().toString().trim();
                    String contact = rs.getCell(j++, i).getContents().toString().trim();
                    String contact_phone = rs.getCell(j++, i).getContents().toString().trim();
                    String isactive = rs.getCell(j++, i).getContents().toString().trim();
//                    if(corp_code.equals("")  && corp_name.equals("") && address.equals("") && contact.equals("") && contact_phone.equals("") && isactive.equals("")){
//                        result = "：第"+(i+1)+"行存在空白行,请删除";
//                        int a=5/0;
//                    }
                    if (corp_code.equals("") && corp_name.equals("") && address.equals("") && contact.equals("") && contact_phone.equals("")) {
                        continue;
                    }
                    if (corp_code.equals("") || corp_name.equals("") || address.equals("") || contact.equals("") || contact_phone.equals("")) {
                        result = "：第" + (i + 1) + "行信息不完整,请参照Execl中对应的批注";
                        int a = 5 / 0;
                    }
                    Corp corp = new Corp();
                    corp.setCorp_code(corp_code);
                    corp.setCorp_name(corp_name);
                    corp.setAddress(address);
                    corp.setContact(contact);
                    corp.setContact_phone(contact_phone);
                    if (isactive.toUpperCase().equals("N")) {
                        corp.setIsactive("N");
                    } else {
                        corp.setIsactive("Y");
                    }
                    corp.setCreater(user_id);
                    Date now = new Date();
                    corp.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    corp.setModified_date(Common.DATETIME_FORMAT.format(now));
                    corp.setModifier(user_id);
                    corps.add(corp);
                    //      result = corpService.insertExecl(corp);
                }
            }
            for (Corp corp : corps
                    ) {
                result = corpService.insertExecl(corp);
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

    @RequestMapping(value = "/is_authorize", method = RequestMethod.POST)
    @ResponseBody
    public String isAuthorize(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<CorpWechat> corpWechats = corpService.getWByCorp(corp_code);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(JSON.toJSONString(corpWechats));
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 企业管理
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String screen(HttpServletRequest request) {
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
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            JSONObject result = new JSONObject();
            if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
            }
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员(官方画面)
                PageInfo<Corp> list = corpService.selectAllCorpScreen(page_number, page_size, map);
                result.put("list", JSON.toJSONString(list));
            } else {
                //用户画面
                Corp corp = corpService.selectByCorpId(0, corp_code, "");
                result.put("list", JSON.toJSONString(corp));
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 选择公众号
     */
    @RequestMapping(value = "/selectWx", method = RequestMethod.POST)
    @ResponseBody
    public String selectWx(HttpServletRequest request) {
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
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            JSONObject result = new JSONObject();
            List<CorpWechat> wechatlist = corpService.getWAuthByCorp(corp_code);
            result.put("list", wechatlist);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 选择公众号
     */
    @RequestMapping(value = "/updateWechat", method = RequestMethod.POST)
    @ResponseBody
    public String updateWechat(HttpServletRequest request) {
        String user_code = request.getSession().getAttribute("user_code").toString();
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            JSONArray wechat = JSONArray.parseArray(jsonObject.get("wechat").toString());
            String result = corpService.updateCorpWechat(wechat, corp_code, user_code);
            if (!result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result.toString());
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
}
