package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.service.*;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import org.apache.log4j.Logger;
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
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Map datalist = iceInterfaceService.vipBasicMethod(jsonObject,request);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisAllVip", datalist);
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
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            Map datalist = iceInterfaceService.vipAnalysisBasicMethod(jsonObject,request);
            DataBox dataBox = iceInterfaceService.iceInterface("AnalysisVipNew", datalist);
            logger.info("-------AnalysisNewVip:" + dataBox.data.get("message").value);
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

    //生日会员
    @RequestMapping(value = "/vipBirth", method = RequestMethod.POST)
    @ResponseBody
    public String vipBirth(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String query_type = "today";
            if (jsonObject.containsKey("query_type")){
                query_type = jsonObject.get("query_type").toString();
            }

            Map datalist = iceInterfaceService.vipAnalysisBasicMethod(jsonObject,request);
            Data data_query_type = new Data("query_type", query_type, ValueType.PARAM);
            datalist.put(data_query_type.key, data_query_type);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipBirth", datalist);
            logger.info("-------AnalysisBirthVip:" + dataBox.data.get("message").value);
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

    //活跃会员
    @RequestMapping(value = "/vipSleep", method = RequestMethod.POST)
    @ResponseBody
    public String vipSleep(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String query_type = jsonObject.get("query_type").toString();

            Map datalist = iceInterfaceService.vipAnalysisBasicMethod(jsonObject,request);
            Data data_query_type = new Data("query_type", query_type, ValueType.PARAM);
            datalist.put(data_query_type.key, data_query_type);

            DataBox dataBox = iceInterfaceService.iceInterface("AnalysisSleep", datalist);
            String result = dataBox.data.get("message").value;
            logger.info("----query_type: "+query_type+"---vipConsume:" + dataBox.data.get("message").value);

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

    //消费排行
    @RequestMapping(value = "/vipConsume", method = RequestMethod.POST)
    @ResponseBody
    public String vipConsume(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String query_type = jsonObject.get("query_type").toString();

            DataBox dataBox = null;
            Map datalist = iceInterfaceService.vipAnalysisBasicMethod(jsonObject,request);
            if (query_type.equals("recent")){
                //最近消费
                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipRecent", datalist);
            }else if (query_type.equals("freq")){
                //消费频率
                dataBox = iceInterfaceService.iceInterface("AnalysisVipFreq", datalist);
            }else if (query_type.equals("month")){
                //本月消费
                Data data_query_type = new Data("query_type", "1", ValueType.PARAM);
                datalist.put(data_query_type.key, data_query_type);
                dataBox = iceInterfaceService.iceInterface("AnlysisVipAmount", datalist);
            }else if (query_type.equals("three_month")){
                //前三月消费
                Data data_query_type = new Data("query_type", "2", ValueType.PARAM);
                datalist.put(data_query_type.key, data_query_type);
                dataBox = iceInterfaceService.iceInterface("AnlysisVipAmount", datalist);
            }else if (query_type.equals("history")){
                //历史总额
                Data data_query_type = new Data("query_type", "3", ValueType.PARAM);
                datalist.put(data_query_type.key, data_query_type);
                dataBox = iceInterfaceService.iceInterface("AnlysisVipAmount", datalist);
            }
            logger.info("----query_type: "+query_type+"---vipConsume:" + dataBox.data.get("message").value);
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