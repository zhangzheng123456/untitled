package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.CorpInfo;
import com.bizvane.ishop.service.CorpService;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouying on 2016-04-20.
 */


/**
 * 企业管理
 */

@Controller
@RequestMapping("/crop")
public class CorpController {

    private static Logger logger = LoggerFactory.getLogger((CorpController.class));

    String id;

    @Autowired
    private CorpService corpService;
    /*
    * 列表
    * */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String cropManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_id = request.getSession().getAttribute("user_id").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject info = new JSONObject();
            String user_type;
            info.put("user_id",user_id);
            if(role_code.equals("R100000")) {
                //系统管理员
                List<CorpInfo> corpInfo = corpService.selectAllCorp("");
                user_type = "admin";
                info.put("user_type",user_type);
                info.put("corpInfo",corpInfo);
            }else{
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                CorpInfo corpInfo = corpService.selectByCorpId(0,corp_code);
                user_type = "user";
                info.put("user_type",user_type);
                info.put("corpInfo",corpInfo);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(info.toString());
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    public String addCrop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            CorpInfo corp = new CorpInfo();
            corp.setCorp_code(jsonObject.get("corp_code").toString());
            corp.setCorp_name(jsonObject.get("corp_name").toString());
            corp.setAddress(jsonObject.get("address").toString());
            corp.setContact(jsonObject.get("contact").toString());
            corp.setContact_phone(jsonObject.get("phone").toString());
            Date now = new Date();
            corp.setCreated_date(now);
            corp.setCreater(user_id);
            corp.setIsactive(jsonObject.get("isactive").toString());
            corpService.insertCorp(corp);
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
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editCrop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try{
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            CorpInfo corp = new CorpInfo();
            corp.setCorp_code(jsonObject.get("corp_code").toString());
            corp.setCorp_name(jsonObject.get("corp_name").toString());
            corp.setAddress(jsonObject.get("address").toString());
            corp.setContact(jsonObject.get("contact").toString());
            corp.setContact_phone(jsonObject.get("phone").toString());
            Date now = new Date();
            corp.setModified_date(now);
            corp.setModifier(user_id);
            corp.setIsactive(jsonObject.get("isactive").toString());
            corpService.updateByCorpId(corp);
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
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            String type = jsonObject.get("type").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                CorpInfo corp = new CorpInfo(Integer.valueOf(ids[i]));
                logger.info("inter---------------" + Integer.valueOf(ids[i]));
                corpService.deleteByCorpId(Integer.valueOf(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            //	return "Error deleting the user:" + ex.toString();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 查找
     */
    @RequestMapping(value = "/find",method = RequestMethod.GET)
    @ResponseBody
    public String findCrop(HttpServletRequest request) {
        return "";
    }

}
