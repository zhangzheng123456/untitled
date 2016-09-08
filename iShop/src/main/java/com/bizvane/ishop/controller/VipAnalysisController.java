package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/vipAnalysis")
public class VipAnalysisController {
    @Autowired
    UserService userService;
    @Autowired
    CorpService corpService;
    @Autowired
    StoreService storeService;
    @Autowired
    FeedbackService feedbackService;
    @Autowired
    IceInterfaceService iceInterfaceService;


    private static final Logger logger = Logger.getLogger(VipAnalysisController.class);

    String id;

    //会员列表
    @RequestMapping(value = "/allVip", method = RequestMethod.POST)
    @ResponseBody
    public String allVip(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();

            String user_id = "";
            String area_code = "";
            String store_id = "";
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            } else if (role_code.equals(Common.ROLE_GM)){

            } else if (role_code.equals(Common.ROLE_AM)){
                area_code = request.getSession().getAttribute("area_code").toString();
                area_code = area_code.replace(Common.STORE_HEAD,"");
            } else if (role_code.equals(Common.ROLE_SM)){
                String store_code = request.getSession().getAttribute("store_code").toString();
                store_id = store_code.replace(Common.STORE_HEAD,"");
            } else if (role_code.equals(Common.ROLE_STAFF)){
                user_id = user_code;
            }

            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
            Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
            Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_id.key, data_store_id);
            datalist.put(data_area_code.key, data_area_code);
            datalist.put(data_role_code.key, data_role_code);
            datalist.put(data_page_num.key, data_page_num);
            datalist.put(data_page_size.key, data_page_size);

            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.AnalysisAllVip", datalist);
            logger.info("-------vip列表" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //新入会员
    @RequestMapping(value = "/vipNew", method = RequestMethod.POST)
    @ResponseBody
    public String vipNew(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();

            String user_id = "";
            String area_code = "";
            String store_id = "";
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            } else if (role_code.equals(Common.ROLE_GM)){
                if (jsonObject.has("area_code")){
                    area_code = jsonObject.get("area_code").toString();
                }
            } else if (role_code.equals(Common.ROLE_AM)){
                if (jsonObject.has("area_code")){
                    area_code = jsonObject.get("area_code").toString();
                }else {
                    area_code = request.getSession().getAttribute("area_code").toString().replace(Common.STORE_HEAD,"");
                    String[] area_codes = area_code.split(",");
                    area_code = area_codes[0];
                }
                if (jsonObject.has("store_code")){
                    store_id = jsonObject.get("store_code").toString();
                }
            } else if (role_code.equals(Common.ROLE_SM)){
                if (jsonObject.has("store_code")){
                    store_id = jsonObject.get("store_code").toString();
                }else {
                    String store_code = request.getSession().getAttribute("store_code").toString().replace(Common.STORE_HEAD, "");
                    String[] store_codes = store_code.split(",");
                    store_id = store_codes[0];
                }
            } else if (role_code.equals(Common.ROLE_STAFF)){
                user_id = user_code;
                if (jsonObject.has("store_code")){
                    store_id = jsonObject.get("store_code").toString();
                }
            }

            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
            Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
            Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_id.key, data_store_id);
            datalist.put(data_area_code.key, data_area_code);
            datalist.put(data_role_code.key, data_role_code);
            datalist.put(data_page_num.key, data_page_num);
            datalist.put(data_page_size.key, data_page_size);

            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.AnalysisNewVip", datalist);
            logger.info("-------vip列表" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    
}