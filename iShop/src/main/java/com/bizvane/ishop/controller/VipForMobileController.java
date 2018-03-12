package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.VipGroupService;
import com.bizvane.ishop.service.VipService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/10/26.
 */
@Controller
@RequestMapping("/mobileVip")
public class VipForMobileController {

    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    VipService vipService;


    Logger logger=Logger.getLogger(VipForMobileController.class);
    String id="";

    //会员列表（以人为单位）全部会员
    @RequestMapping(value = "/allMobile", method = RequestMethod.POST)
    @ResponseBody
    public String allMobile(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            JSONObject jsonObject = jsonObj.getJSONObject("message");
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            if (role_code.equals(Common.ROLE_SYS)){
                corp_code = jsonObject.get("corp_code").toString();
            }
            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            //  DataBox dataBox = vipGroupService.vipScreenBySolr(new 1JSONArray(),corp_2code,page_3num,page_4size,role_5code,brand6_code,area_7code,store_8code,user_9code,sort_10key,sort_11value);
            //  (JSONArray scre1en,String corp_2code,String pag3e_num,String pa4ge_size,String role_5code, String 6user_brand_code,String user7_area_code,String user_s8tore_code,String user_c9ode1, String sort_k10ey,String sort11_value)
            //  JSONArray post_array = vipScreen2Array(screen, corp_code, role_code, user_brand_code, user_area_code, user_store_code, user_code1);
            JSONArray post_array = vipGroupService.vipScreen2ArrayNew(new JSONArray(), corp_code, role_code, brand_code, area_code, store_code, user_code);
            DataBox dataBox = iceInterfaceService.vipScreenForMobile(page_num, page_size, corp_code, JSON.toJSONString(post_array),sort_key,sort_value);
            if (!dataBox.status.toString().equals("SUCCESS")){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("网络异常，请稍后重试");
                return dataBean.getJsonStr();
            }
            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage("获取数据失败");
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员列表(以人为单位)
     * 筛选
     */
    @RequestMapping(value = "/newScreenMobile", method = RequestMethod.POST)
    @ResponseBody
    public String newScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String screens = jsonObject.get("screen").toString();
            JSONArray screen = JSONArray.parseArray(screens);

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            //  DataBox dataBox = vipGroupService.vipScreenBySolrNew(scre1en,corp_2code,page_3num,page_4size,rol5e_code,brand_c6ode,area_7code,store8_code,user_c9ode,sort_10key,sort_val11ue);
           // (JSONArray scr1een,String corp2_code,String pa3ge_num,Strin4g page_size,String5 role_code, String 6user_brand_code,String7 user_area_code,String 8user_store_code,String 9user_code1, String10sort_key,String s11ort_value)
           // JSONArray post_array = vipScreen2ArrayNew(screen, corp_code, role_code, user_brand_code, user_area_code, user_store_code, user_code1);
            JSONArray post_array = vipGroupService.vipScreen2ArrayNew(screen, corp_code, role_code, brand_code, area_code, store_code, user_code);
            DataBox dataBox = iceInterfaceService.vipScreenForMobile(page_num, page_size, corp_code,JSON.toJSONString(post_array),sort_key,sort_value);
            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("筛选失败");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表（目前只支持姓名，手机号）
     * 搜索(以人为单位)
     */
    @RequestMapping(value = "/vipSearchMobile", method = RequestMethod.POST)
    @ResponseBody
    public String vipSearchMobile(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String search_value = jsonObject.get("searchValue").toString();
            Map datalist = iceInterfaceService.vipBasicMethod2(page_num, page_size,corp_code, request,sort_key,sort_value);
            Data data_search_value = new Data("phone_or_id", search_value, ValueType.PARAM);
            datalist.put(data_search_value.key, data_search_value);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("NewStyleMobileSearchByParam", datalist);
            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
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
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            JSONObject jsonObj2 = JSONObject.parseObject(param);
            String output_message = jsonObj2.get("message").toString();
            JSONObject output_message_object = JSONObject.parseObject(output_message);
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(output_message_object);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = "C10000";
            }
            logger.info("json--------------corp_code-" + corp_code);

            String jlist = jsonObject.get("tablemanager").toString();
            JSONArray array = JSONArray.parseArray(jlist);
            String cust_cols = "";
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                String column_name = json.getString("column_name");
                if (column_name.startsWith("CUST_"))
                    cust_cols = cust_cols + column_name + ",";
                if (column_name.equals("user_name"))
                    cust_cols = cust_cols + "user_code,";
                if (column_name.equals("store_name"))
                    cust_cols = cust_cols + "store_code,";
            }

            String pathname = "";
            JSONArray jsonArray;
            if (jsonObject.containsKey("ids") && !jsonObject.get("ids").equals("")){
                String vip_phone = jsonObject.get("ids").toString().trim();

                //根据手机号，获取会员资料(包括拓展信息)
                //[{"key":"MOBILE_VIP","value":[{"opera":"","logic":"equal","value":"1,2,3,4"}],"type":"text","groupName":"手机号"}]
                JSONArray jsonArray1=new JSONArray();
                JSONArray jsonArray2=new JSONArray();
                JSONObject jsonObject1=new JSONObject();
                JSONObject jsonObject2=new JSONObject();
                jsonObject1.put("opera","");
                jsonObject1.put("logic","equal");
                jsonObject1.put("value",vip_phone);
                jsonArray1.add(jsonObject1);
                jsonObject2.put("value",jsonArray1);
                jsonObject2.put("key","MOBILE_VIP");
                jsonObject2.put("type","text");
                jsonArray2.add(jsonObject2);

                DataBox dataBox = iceInterfaceService.MobileScreen2ExeclMethod("1","2000",corp_code,jsonArray2.toString(),sort_key,sort_value,cust_cols);

                String result = dataBox.data.get("message").value;
                JSONObject object = JSONObject.parseObject(result);
                jsonArray = JSONArray.parseArray(object.get("all_vip_list").toString());

                List list = WebUtils.Json2List2(jsonArray);
                pathname = OutExeclHelper.OutExecl_vip(jsonArray, list, map, response, request,"会员档案");

            }else {
                String page_num = jsonObject.getString("page_num");
                String page_size = jsonObject.getString("page_size");

                DataBox dataBox = null;
                String screen_message = jsonObject.get("screen_message").toString();
                String searchValue = jsonObject.get("searchValue").toString().trim();
                if (!searchValue.equals("")) {
                    Map datalist = iceInterfaceService.vipBasicMethod2(page_num, page_size,corp_code, request,sort_key,sort_value);
                    Data data_search_value = new Data("phone_or_id", searchValue, ValueType.PARAM);
                    datalist.put(data_search_value.key, data_search_value);
                    dataBox = iceInterfaceService.iceInterfaceV3("NewStyleMobileSearchByParam", datalist);
                }else if(screen_message.equals("") && searchValue.equals("")){
                    JSONArray post_array = vipGroupService.vipScreen2ArrayNew(new JSONArray(),corp_code,role_code,brand_code,area_code,store_code,user_code);
                    dataBox = iceInterfaceService.MobileScreen2ExeclMethod(page_num,page_size,corp_code,JSON.toJSONString(post_array),sort_key,sort_value,cust_cols);

                }else if(!screen_message.equals("")){
                    JSONArray screen = JSON.parseArray(screen_message);
                    JSONArray post_array = vipGroupService.vipScreen2ArrayNew(screen,corp_code,role_code,brand_code,area_code,store_code,user_code);
                    dataBox = iceInterfaceService.MobileScreen2ExeclMethod(page_num,page_size,corp_code,JSON.toJSONString(post_array),sort_key,sort_value,cust_cols);

                }
                String result = dataBox.data.get("message").value;
                JSONObject object = JSONObject.parseObject(result);
                jsonArray = JSONArray.parseArray(object.get("all_vip_list").toString());
                int count = object.getInteger("count");

                int pageNum = Integer.parseInt(page_num);
                int pageSize = Integer.parseInt(page_size);
                int start_line = (pageNum-1) * pageSize + 1;
                int end_line = pageNum*pageSize;
                if (count < pageNum*pageSize)
                    end_line = count;
                List list = WebUtils.Json2List2(jsonArray);
                pathname = OutExeclHelper.OutExecl_vip(jsonArray, list, map, response, request,"会员档案("+start_line+"-"+end_line+")");

            }
//            logger.info("=======cust_cols:"+cust_cols);

            JSONObject result2 = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result2.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result2.toString());
        }catch (Ice.MemoryLimitException im){
            System.out.println("===============ice异常========================================");
            errormessage = "导出数据过大,请筛选后导出";
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            im.printStackTrace();
        }catch (Exception ex) {
            System.out.println("===============总异常========================================");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

}
