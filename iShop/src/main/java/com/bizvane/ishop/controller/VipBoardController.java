package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.sun.v1.common.DataBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yanyadong on 2017/9/15.
 */
@Controller
@RequestMapping("/board")
public class VipBoardController {

    String id="";
    @Autowired
    IceInterfaceService iceInterfaceService;

    @RequestMapping(value = "/allData",method = RequestMethod.POST)
    @ResponseBody
    public String getAllData(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String type=jsonObject.getString("type");
            String brand_code=jsonObject.getString("brand_code");
            String query_time=jsonObject.getString("query_time");
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            if(role_code.equals(Common.ROLE_SYS)){
                corp_code="C10000";
            }
            DataBox dataBox=iceInterfaceService.getAllDataByBoard(corp_code,type,brand_code,query_time);
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
        }catch (Exception e){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/vipAnaly",method = RequestMethod.POST)
    @ResponseBody
    public String getVipAnaly(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String start_time=jsonObject.getString("start_time");
            String end_time=jsonObject.getString("end_time");
            String type=jsonObject.getString("type");
            String query_type=jsonObject.getString("query_type");
            String brand_code=jsonObject.getString("brand_code");
            String source=jsonObject.getString("source");
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            if(role_code.equals(Common.ROLE_SYS)){
                corp_code="C10000";
            }
//            String time = "";
//            if(StringUtils.isNotBlank(time_type)) {
//                List<String> dateList = new ArrayList<String>();
//                //周月年筛选 默认显示当前月
//                if (time_type.equals("Y")) {  //年
//                    dateList = TimeUtils.getYearAllDays(time_value);
//                } else if (time_type.equals("M")) { //月
//                    dateList = TimeUtils.getMonthAllDays(time_value);
//                } else if (time_type.equals("W")) { //周
//                    String[] dates = TimeUtils.getWeek2Day(time_value).split(",");
//                    for (int i = 0; i < dates.length; i++) {
//                        dateList.add(dates[i]);
//                    }
//                }
//                for (int i = 0; i < dateList.size(); i++) {
//                    time += dateList.get(i) + ",";
//                }
//                if (time.endsWith(",")) {
//                    time = time.substring(0, time.length() - 1);
//                }
//            }
            DataBox dataBox=iceInterfaceService.getVipAnalyByBoard(corp_code,start_time,end_time,type,query_type,brand_code,source);
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
        }catch (Exception e){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/brandAnaly",method = RequestMethod.POST)
    @ResponseBody
    public String getBrandAnaly(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String time_type=jsonObject.getString("time_type");
            String time_value=jsonObject.getString("time_value").replace("-","");
            String type=jsonObject.getString("type");
            String brand_code=jsonObject.getString("brand_code");
            String source=jsonObject.getString("source");

            if(role_code.equals(Common.ROLE_SYS)){
                corp_code="C10000";
            }

            DataBox dataBox=iceInterfaceService.ACHVBrandDashBoard(corp_code,time_type,time_value,type,brand_code,source);
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
        }catch (Exception e){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/managerKpi",method = RequestMethod.POST)
    @ResponseBody
    public String managerKip(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            String start_time=jsonObject.getString("start_time");
            String end_time=jsonObject.getString("end_time");
            String query_type=jsonObject.getString("query_type");
            String brand_code=jsonObject.getString("brand_code");
            String vip_source=jsonObject.getString("vip_source");
            String achv_source=jsonObject.getString("achv_source");

            if(role_code.equals(Common.ROLE_SYS)){
                corp_code="C10000";
            }

            DataBox dataBox=iceInterfaceService.manageKpiReport(corp_code,brand_code,vip_source,achv_source,start_time,end_time,query_type);
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
        }catch (Exception e){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }
}
