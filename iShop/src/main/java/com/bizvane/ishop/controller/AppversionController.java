package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AppversionMapper;
import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.service.AppversionService;
import com.bizvane.ishop.service.FeedbackService;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yin on 2016/6/21.
 */
public class AppversionController {
    @Autowired
    private AppversionService appversionService;

    String id;

    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);
    private static final Logger logger = Logger.getLogger(AppversionController.class);
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    //查询分页
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
            PageInfo<Appversion> list = appversionService.selectAllAppversion(page_number, page_size, search_value);
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
    public String addFeedback(HttpServletRequest request){
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
            Appversion appversion=new Appversion();
            appversion.setPlatform(jsonObject.get("platform").toString());
            appversion.setDownload_addr(jsonObject.get("download_addr").toString());
            appversion.setVersion_id(jsonObject.get("version_id").toString());
            appversion.setIs_force_update(jsonObject.get("is_force_update").toString());
            appversion.setVersion_describe(jsonObject.get("version_describe").toString());
            appversion.setCrop_code(jsonObject.get("crop_code").toString());
            appversion.setCreater(user_id);
            appversion.setModifier(jsonObject.get("modifier").toString());
            appversion.setIsactive(jsonObject.get("isactive").toString());
            //------------操作日期-------------
            Date date=new Date();
            appversion.setCreated_date(sdf.format(date));
            appversion.setModified_date(sdf.format(date));
            appversionService.addAppversion(appversion);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("add success");
        }catch (Exception ex){
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
            String app_id = jsonObject.get("id").toString();
            String[] ids = app_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                appversionService.delAppversionById(Integer.valueOf(ids[i]));
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
    public String selectById(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String app_id = jsonObject.get("id").toString();
            final Appversion appversion = appversionService.selAppversionById(Integer.parseInt(app_id));
            JSONObject result = new JSONObject();
            result.put("appversion", JSON.toJSONString(appversion));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
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
    public String editCrop(HttpServletRequest request) {
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
            Appversion appversion=new Appversion();
            appversion.setPlatform(jsonObject.get("platform").toString());
            appversion.setDownload_addr(jsonObject.get("download_addr").toString());
            appversion.setVersion_id(jsonObject.get("version_id").toString());
            appversion.setIs_force_update(jsonObject.get("is_force_update").toString());
            appversion.setVersion_describe(jsonObject.get("version_describe").toString());
            appversion.setCrop_code(jsonObject.get("crop_code").toString());
            appversion.setCreater(jsonObject.get("creater").toString());
            appversion.setModifier(user_id);
            appversion.setIsactive(jsonObject.get("isactive").toString());
            //------------操作日期-------------
            Date date=new Date();
            appversion.setCreated_date(sdf.format(date));
            appversion.setModified_date(sdf.format(date));
            appversion.setId(Integer.parseInt(jsonObject.get("id").toString()));
            appversionService.updAppversionById(appversion);
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
}
