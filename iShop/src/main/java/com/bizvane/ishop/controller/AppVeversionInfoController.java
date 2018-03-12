package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.AppHelp;
import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.RelAppHelp;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/23.
 */
@Controller
public class AppVeversionInfoController {

    private static final Logger logger = Logger.getLogger(AppVeversionInfoController.class);
    @Autowired
    private AppversionService appversionService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private TableManagerService managerService;
    @Autowired
    private BaseService baseService;
    String id;


    @RequestMapping(value = "/api/appverion/list", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //条件查询
    public String versionList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            //-------------------------------------------------------
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String platform = jsonObject.get("platform").toString();
            JSONObject result = new JSONObject();
            PageInfo<Appversion> list = appversionService.selectAllAppversion1(page_number, page_size, platform);
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

}
