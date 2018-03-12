package com.bizvane.ishop.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.*;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.common.service.redis.RedisClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by zhouying on 2016-04-20.
 */


@Controller
@RequestMapping("/api/external")
public class WebAPIController {

    private static long NETWORK_DELAY_SECONDS = 1000 * 60 * 10;// 10 mininutes

    private static String APP_KEY = "Fghz1Fhp6pM1XajBMjXM";

    private static String SIGN = "41bfa82252f31bef46ccffca4ec22b5e";

    String id;

    private static final Logger logger = Logger.getLogger(WebAPIController.class);

    @Autowired
    VipService vipService;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    BrandService brandService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    RedisClient redisClient;
    /**
     *
     */
    @RequestMapping(value = "/crm", method = RequestMethod.POST , produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String vipRelation(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(data);

            String corp_code = jsonObj.getString("corp_code");
            String sign = jsonObj.getString("sign");
            String method = jsonObj.getString("method");
            String param = jsonObj.getString("param");

            if (sign == null || sign.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("need sign");
            } else if (corp_code == null || corp_code.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("need corp_code");
            } else if (!sign.equals(SIGN)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("sign Invalid");
            } else if (method == null || method.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("need method");
            }else {
                if (method.equals("getVipAuthCode")){
                    JSONObject param_obj = JSONObject.parseObject(param);
                    String vip_id = param_obj.getString("vip_id");
                    String phone = param_obj.getString("phone");
                    String vip_name = param_obj.getString("vip_name");
                    String type = param_obj.getString("type");
                    JSONObject result = vipService.getVipAuthCode(corp_code,vip_id,phone,vip_name,type);

                    dataBean.setCode(result.getString("errorcode"));
                    dataBean.setMessage(result.getString("errormessage"));
                }
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 有数嵌入看板
     */
    @RequestMapping(value = "/youshuBoard", method = RequestMethod.POST , produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String youshuBoard(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String corp_code = jsonObject.getString("corp_code");
            String sign = jsonObject.getString("sign");
            String method = jsonObject.getString("method");

//            if (sign == null || sign.equals("")) {
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setMessage("need sign");
//                return dataBean.getJsonStr();
//            }
            if (corp_code == null || corp_code.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("need corp_code");
                return dataBean.getJsonStr();
            }
            if (method == null || method.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("need method");
                return dataBean.getJsonStr();
            }
            Calendar calendar = Calendar.getInstance();
            String sign_end = CheckUtils.encryptMD5Hash(APP_KEY+calendar.getTimeInMillis());
            int min = calendar.get(Calendar.MINUTE) - 5;
            calendar.set(Calendar.MINUTE, min);
            String sign_start = CheckUtils.encryptMD5Hash(APP_KEY+calendar.getTimeInMillis());

//            if (sign.compareTo(sign_start)>0 && sign.compareTo(sign_end) < 0){
               if (method.equals("brand")){
                   String search_value = "";
                   if (jsonObject.containsKey("searchValue")) {
                       search_value = jsonObject.get("searchValue").toString();
                   }
                   List<Brand> brand = brandService.getActiveBrand(corp_code, search_value, null);

                   JSONArray array = new JSONArray();
                   JSONObject brands = new JSONObject();
                   for (int i = 0; i < brand.size(); i++) {
                       Brand brand1 = brand.get(i);
                       String brand_code = brand1.getBrand_code();
                       String brand_name = brand1.getBrand_name();
                       JSONObject obj = new JSONObject();
                       obj.put("brand_code", brand_code);
                       obj.put("brand_name", brand_name);
                       array.add(obj);
                   }
                   brands.put("brands", array);
                   brands.put("total", array.size());

                   dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                   dataBean.setId(id);
                   dataBean.setMessage(brands.toString());
               }else{
                   DataBox dataBox = new DataBox();
                   if (method.equals("allData")){
                       String type=jsonObject.getString("type");
                       String brand_code=jsonObject.getString("brand_code");
                       String query_time=jsonObject.getString("query_time");

                       dataBox=iceInterfaceService.getAllDataByBoard(corp_code,type,brand_code,query_time);
                   }else if (method.equals("vipAnaly")){
                       String type=jsonObject.getString("type");
                       String query_type=jsonObject.getString("query_type");
                       String brand_code=jsonObject.getString("brand_code");
                       String source=jsonObject.getString("source");

                       dataBox=iceInterfaceService.getVipAnalyByBoard(corp_code,"","",type,query_type,brand_code,source);
                   }else if (method.equals("brandAnaly")){
                       String time_type=jsonObject.getString("time_type");
                       String time_value=jsonObject.getString("time_value").replace("-","");
                       String type=jsonObject.getString("type");
                       String brand_code=jsonObject.getString("brand_code");
                       String source=jsonObject.getString("source");

                       dataBox=iceInterfaceService.ACHVBrandDashBoard(corp_code,time_type,time_value,type,brand_code,source);
                   }
                   if (dataBox.status.toString().equals("SUCCESS")){
                       String value = dataBox.data.get("message").value;
                       JSONObject obj = JSON.parseObject(value);
                       dataBean.setId(id);
                       dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                       dataBean.setMessage(obj.toString());
                   }else {
                       dataBean.setId(id);
                       dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                       dataBean.setMessage("获取数据失败");
                   }
               }
//            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 参数控制
     */
    @RequestMapping(value = "/updateSchedule", method = RequestMethod.GET)
    @ResponseBody
    public String updateSchedule(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，操作失败";
        try {
            String job_name = request.getParameter("job_name").toString();
            String job_group = request.getParameter("job_group").toString();
            String time = request.getParameter("time").toString();

            ScheduleJob scheduleJob = scheduleJobService.selectScheduleByJob(job_name,job_group);
            if (scheduleJob != null){
                String corn = TimeUtils.getCron(Common.DATETIME_FORMAT.parse(time));

                scheduleJob.setCron_expression(corn);
                scheduleJobService.update(scheduleJob);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("update success");
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("job 错误");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /**
     * 参数控制
     */
    @RequestMapping(value = "/deleteSchedule", method = RequestMethod.GET)
    @ResponseBody
    public String deleteSchedule(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，操作失败";
        try {
            String job_name = request.getParameter("job_name").toString();
            String job_group = request.getParameter("job_group").toString();

            ScheduleJob scheduleJob = scheduleJobService.selectScheduleByJob(job_name,job_group);
            if (scheduleJob != null){
                scheduleJobService.delete(job_name,job_group);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("delete success");
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("job 错误");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 参数控制
     */
    @RequestMapping(value = "/createSchedule", method = RequestMethod.GET)
    @ResponseBody
    public String createSchedule(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，操作失败";
        try {
            String job_group = request.getParameter("job_group").toString();
            String method = request.getParameter("method").toString();
            String code = request.getParameter("code").toString();
            String corn = request.getParameter("corn").toString();

            JSONObject func = new JSONObject();
            func.put("method",method);
            func.put("code",code);
            func.put("corp_code","");
            func.put("user_code","");
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJob_name(job_group+code);
            scheduleJob.setJob_group(job_group);
            scheduleJob.setFunc(func.toString());
            scheduleJob.setCron_expression(corn);

            scheduleJobService.insert(scheduleJob);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("delete success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 参数控制
     */
    @RequestMapping(value = "/redis", method = RequestMethod.GET)
    @ResponseBody
    public String redis(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，操作失败";
        try {
            String key = request.getParameter("key").toString();
            String value = request.getParameter("value").toString();

            redisClient.set(key,value);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(redisClient.get(key).toString());

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

}
