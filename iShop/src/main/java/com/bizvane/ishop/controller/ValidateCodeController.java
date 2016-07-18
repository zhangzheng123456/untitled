package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.ValidateCode;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.ValidateCodeService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by yin on 2016/6/23.
 */
@Controller
@RequestMapping("/validatecode")
public class ValidateCodeController {
    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private FunctionService functionService;
    String id;

    private static final Logger logger = Logger.getLogger(ValidateCodeController.class);

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    //列表
    public String selectAll(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            // String search_value = jsonObject.get("searchValue").toString();
            JSONObject result = new JSONObject();
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
            PageInfo<ValidateCode> list = validateCodeService.selectAllValidateCode(page_number, page_size, "");
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
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

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    //条件查询
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            //-------------------------------------------------------
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            JSONObject result = new JSONObject();
            PageInfo<ValidateCode> list = validateCodeService.selectAllValidateCode(page_number, page_size, search_value);
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

    /**
     * 增加（用了事务）
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addValidateCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            ValidateCode validateCode = WebUtils.JSON2Bean(jsonObject, ValidateCode.class);
            //------------操作日期-------------
            Date date = new Date();
            validateCode.setCreated_date(Common.DATETIME_FORMAT.format(date));
            validateCode.setCreater(user_id);
            validateCode.setModified_date(Common.DATETIME_FORMAT.format(date));
            validateCode.setModifier(user_id);
            validateCodeService.insertValidateCode(validateCode);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("add success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 删除(用了事务)
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String inter_id = jsonObject.get("id").toString();
            String[] ids = inter_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                validateCodeService.deleteValidateCode(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
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
     * 根据ID查询
     */
    @RequestMapping(value = "/selectById", method = RequestMethod.POST)
    @ResponseBody
    public String selectById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String app_id = jsonObject.get("id").toString();
            final ValidateCode validateCode = validateCodeService.selValidateCodeById(Integer.parseInt(app_id));
            JSONObject result = new JSONObject();
            result.put("validateCode", JSON.toJSONString(validateCode));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("selectById-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 编辑(加了事务)
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editValidateCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            ValidateCode validateCode = WebUtils.JSON2Bean(jsonObject, ValidateCode.class);
            //------------操作日期-------------
            Date date = new Date();
            validateCode.setModified_date(Common.DATETIME_FORMAT.format(date));
            validateCode.setModifier(user_id);
            validateCodeService.updateValidateCode(validateCode);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("info--------" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/testword", method = RequestMethod.GET)
    @ResponseBody
    public String testword(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {
            List<ValidateCode> validateCodes = validateCodeService.selectAll();
            String column_name =request.getParameter("column_name");
            String[] cols = column_name.split(",");//前台传过来的字段
            OutExeclHelper.OutExecl(validateCodes,cols,response,request);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("word success");
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
        }
        return dataBean.getJsonStr();

    }
    @RequestMapping(value = "/addByExecl", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public String addByExecl(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
       // String file="F:/报表/Test2.xls";
        try {

            Workbook rwb=Workbook.getWorkbook(new File("F:/报表/Test2.xls"));
            Sheet rs=rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos=rs.getColumns();//得到所有的列
            int rows=rs.getRows();//得到所有的行
            for(int i=1;i<rows;i++){
                for(int j=0;j<clos;j++){
                    ValidateCode validateCode=new ValidateCode();
                    //第一个是列数，第二个是行数
                    String phone = rs.getCell(j++, i).getContents();//默认最左边编号也算一列 所以这里得j++
                    String platform = rs.getCell(j++,i).getContents();
                    String validate_code = rs.getCell(j++,i).getContents();
                    String isactive = rs.getCell(j++,i).getContents();
                    validateCode.setPhone(phone);
                    validateCode.setPlatform(platform);
                    validateCode.setValidate_code(validate_code);
                    validateCode.setIsactive(isactive);
                    Date date = new Date();
                    validateCode.setCreated_date(Common.DATETIME_FORMAT.format(date));
                    validateCode.setCreater(user_id);
                    validateCode.setModified_date(Common.DATETIME_FORMAT.format(date));
                    validateCode.setModifier(user_id);
                    validateCodeService.insertValidateCode(validateCode);
                }
            }
            System.out.println(clos+" rows:"+rows);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("word success");
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
        }
        return dataBean.getJsonStr();

    }

}

