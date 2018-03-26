package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.AppManager;
import com.bizvane.ishop.service.AppManagerService;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by PC on 2017/2/9.
 */
@Controller
@RequestMapping("/appManager")
public class AppManagerController {
    @Autowired
    private AppManagerService appManagerService;
    @Autowired
    private BaseService baseService;

    @RequestMapping(value = "/getFunctionList", method = RequestMethod.POST)
    @ResponseBody
    public String getFunctionList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);

            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            List<AppManager> result = new ArrayList<AppManager>();
            if (corp_code.equals("C10016")) {
                result = appManagerService.getFunctionList("C10016");
            } else {
                result = appManagerService.getFunctionList(null);
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(result));
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/getActionList", method = RequestMethod.POST)
    @ResponseBody
    public String getActionList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);

            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String app_function = jsonObject.get("app_function").toString();
            List<AppManager> result = new ArrayList<AppManager>();
            if (corp_code.equals("C10016")) {
                result = appManagerService.getActionList(app_function, "C10016", "");
            } else if (corp_code.equals("C10055")) {
                result = appManagerService.getActionList(app_function, null, "C10055");
            } else {
                result = appManagerService.getActionList(app_function, null, null);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(result));
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/getObtainEvents", method = RequestMethod.POST)
    @ResponseBody
    public String getObtainEvents(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        try {
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);

            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
//            if(role_code.equals(Common.ROLE_CM)){
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                //  System.out.println("manager_corp=====>"+manager_corp);
//                String[] manager_corp_arr = null;
//                if (!manager_corp.equals("")) {
//                    manager_corp_arr = manager_corp.split(",");
//                }
//                corp_code=manager_corp_arr[0];
//                if(jsonObject.containsKey("corp_code") && !String.valueOf(jsonObject.get("corp_code")).equals("")){
//                    corp_code=jsonObject.get("corp_code").toString();
//                }
//                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
//            }
            if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();

                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
            }
            String date_type = jsonObject.get("date_type").toString();
            String date_value = jsonObject.get("date_value").toString().trim();
            String app_action_code = jsonObject.get("app_action_code").toString();
            String user_code_json = jsonObject.get("user_code").toString();

            object.put("corp_code", corp_code);
            object.put("role_code", role_code);
            object.put("app_action_code", app_action_code);
            object.put("dev_type", "");
            object.put("date_type", date_type);
            object.put("date_value", date_value);
            object.put("page_number", "");
            object.put("page_size", "");
            if (user_code_json.equals("")) {
                String storeByScreen = baseService.getStoreByScreen(jsonObject, request,corp_code);
                object.put("store_code", storeByScreen);
                object.put("user_code", "");
            } else if (!user_code_json.equals("")) {
                object.put("store_code", "");
                object.put("user_code", user_code_json);
            }
            String obtainEvents = appManagerService.getObtainEvents(object);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(obtainEvents.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("拉取图表失败");
        }
        return dataBean.getJsonStr();

    }

    @RequestMapping(value = "/getObtainEventTable", method = RequestMethod.POST)
    @ResponseBody
    public String getObtainEventTable(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        try {
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);

            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
//            if(role_code.equals(Common.ROLE_CM)){
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                //  System.out.println("manager_corp=====>"+manager_corp);
//                String[] manager_corp_arr = null;
//                if (!manager_corp.equals("")) {
//                    manager_corp_arr = manager_corp.split(",");
//                }
//                corp_code=manager_corp_arr[0];
//                if(jsonObject.containsKey("corp_code") && !String.valueOf(jsonObject.get("corp_code")).equals("")){
//                    corp_code=jsonObject.get("corp_code").toString();
//                }
//                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
//            }
            if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();

                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
            }
            String date_type = jsonObject.get("date_type").toString();
            String date_value = jsonObject.get("date_value").toString().trim();
            String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String app_action_code = jsonObject.get("app_action_code").toString();
            String user_code_json = jsonObject.get("user_code").toString();

            object.put("corp_code", corp_code);
            object.put("role_code", role_code);
            object.put("app_action_code", app_action_code);
            object.put("dev_type", "");
            object.put("date_type", date_type);
            object.put("date_value", date_value);
            object.put("page_number", page_number);
            object.put("page_size", page_size);
            if (user_code_json.equals("")) {
                String storeByScreen = baseService.getStoreByScreen(jsonObject, request,corp_code);
                object.put("store_code", storeByScreen);
                object.put("user_code", "");
            } else if (!user_code_json.equals("")) {
                object.put("store_code", "");
                object.put("user_code", user_code_json);
            }
            String obtainEvents = appManagerService.getObtainEventTable(object);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(obtainEvents.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("拉取列表失败");
        }
        return dataBean.getJsonStr();

    }


    /**
     * 功能使用分析导出
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String getexportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        String errormessage = "数据异常，导出失败";
        try {
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);

            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();

                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
            }
            String date_type = jsonObject.get("date_type").toString();
            String date_value = jsonObject.get("date_value").toString();
            String page_number = "1";
            String page_size = Common.EXPORTEXECLCOUNT+"";
            String app_action_code = jsonObject.get("app_action_code").toString();
            String user_code_json = jsonObject.get("user_code").toString();

            object.put("corp_code", corp_code);
            object.put("role_code", role_code);
            object.put("app_action_code", app_action_code);
            object.put("dev_type", "");
            object.put("date_type", date_type);
            object.put("date_value", date_value);
            object.put("page_number", page_number);
            object.put("page_size", page_size);
            if (user_code_json.equals("")) {
                String storeByScreen = baseService.getStoreByScreen(jsonObject, request,corp_code);
                object.put("store_code", storeByScreen);
                object.put("user_code", "");
            } else if (!user_code_json.equals("")) {
                object.put("store_code", "");
                object.put("user_code", user_code_json);
            }
            String obtainEvents = appManagerService.getObtainEventTable(object);
            com.alibaba.fastjson.JSONObject object1 = JSON.parseObject(obtainEvents);

            JSONArray jsonArray = JSON.parseArray(object1.get("message").toString());
            List list = WebUtils.Json2List2(jsonArray);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("app_function","功能");
            map.put("app_action_name","事件");
            map.put("user_name","人员");
            map.put("event_time","时间段");
            map.put("event_count","事件次数");
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request,"功能使用分析");
            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();

    }



    @RequestMapping(value = "/getCommodityAttention", method = RequestMethod.POST)
    @ResponseBody
    public String getCommodityAttention(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        try {
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);

            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();

                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
            }
            String date_type = jsonObject.get("date_type").toString();
            String date_value = jsonObject.get("date_value").toString();
            String goods_type = jsonObject.get("goods_type").toString();
            String user_code_json = jsonObject.get("user_code").toString();

            object.put("corp_code", corp_code);
            object.put("role_code", role_code);
            if(goods_type.equals("fab")) {
                object.put("goods_type", "FAB");
            }else if(goods_type.equals("wx")){
                object.put("goods_type", "MALL");
            }else if(goods_type.equals("")){
                object.put("goods_type", "");
            }
            object.put("dev_type", "");
            object.put("date_type", date_type);
            object.put("date_value", date_value);
            object.put("page_number", "");
            object.put("page_size", "");
            if (user_code_json.equals("")) {
                String storeByScreen = baseService.getStoreByScreen(jsonObject, request,corp_code);
                object.put("store_code", storeByScreen);
                object.put("user_code", "");
            } else if (!user_code_json.equals("")) {
                object.put("store_code", "");
                object.put("user_code", user_code_json);
            }
            String obtainEvents = appManagerService.getCommodityAttention(object);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(obtainEvents.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("拉取图表失败");
        }
        return dataBean.getJsonStr();

    }

    @RequestMapping(value = "/getCommodityAttentionTable", method = RequestMethod.POST)
    @ResponseBody
    public String getCommodityAttentionTable(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        try {
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);

            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();

                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
            }
            String date_type = jsonObject.get("date_type").toString();
            String date_value = jsonObject.get("date_value").toString();
            String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String goods_type = jsonObject.get("goods_type").toString();
            String user_code_json = jsonObject.get("user_code").toString();

            object.put("corp_code", corp_code);
            object.put("role_code", role_code);
            if(goods_type.equals("fab")) {
                object.put("goods_type", "FAB");
            }else if(goods_type.equals("wx")){
                object.put("goods_type", "MALL");
            }else if(goods_type.equals("")){
                object.put("goods_type", "");
            }
            object.put("dev_type", "");
            object.put("date_type", date_type);
            object.put("date_value", date_value);
            object.put("page_number", page_number);
            object.put("page_size", page_size);
            if (user_code_json.equals("")) {
                String storeByScreen = baseService.getStoreByScreen(jsonObject, request,corp_code);
                object.put("store_code", storeByScreen);
                object.put("user_code", "");
            } else if (!user_code_json.equals("")) {
                object.put("store_code", "");
                object.put("user_code", user_code_json);
            }
            String obtainEvents = appManagerService.getCommodityAttentionTable(object);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(obtainEvents.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("拉取列表失败");
        }
        return dataBean.getJsonStr();

    }

    /**
     * 商品关注度分析
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/exportGoodsExecl", method = RequestMethod.POST)
    @ResponseBody
    public String getexportGoodsExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        String errormessage = "数据异常，导出失败";
        try {
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);

            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();

                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));

                System.out.println(corp_code+"<======manager_corp=====>"+manager_corp);
            }
            String date_type = jsonObject.get("date_type").toString();
            String date_value = jsonObject.get("date_value").toString();
            String page_number = "1";
            String page_size = Common.EXPORTEXECLCOUNT+"";
            String goods_type = jsonObject.get("goods_type").toString();
            String user_code_json = jsonObject.get("user_code").toString();

            object.put("corp_code", corp_code);
            object.put("role_code", role_code);
            object.put("goods_type", goods_type);
            object.put("dev_type", "");
            object.put("date_type", date_type);
            object.put("date_value", date_value);
            object.put("page_number", page_number);
            object.put("page_size", page_size);
            if (user_code_json.equals("")) {
                String storeByScreen = baseService.getStoreByScreen(jsonObject, request,corp_code);
                object.put("store_code", storeByScreen);
                object.put("user_code", "");
            } else if (!user_code_json.equals("")) {
                object.put("store_code", "");
                object.put("user_code", user_code_json);
            }

            object.put("corp_code", corp_code);
            object.put("role_code", role_code);
            if(goods_type.equals("fab")) {
                object.put("goods_type", "FAB");
            }else if(goods_type.equals("wx")){
                object.put("goods_type", "MALL");
            }else if(goods_type.equals("")){
                object.put("goods_type", "");
            }
            String obtainEvents = appManagerService.getCommodityAttentionTable(object);
            com.alibaba.fastjson.JSONObject object1 = JSON.parseObject(obtainEvents);

            JSONArray jsonArray = JSON.parseArray(object1.get("message").toString());
            List list = WebUtils.Json2List2(jsonArray);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("user_name","人员");
            map.put("product_url","内容");
            map.put("share_count","分享次数");
            map.put("look_count","查看次数");
            map.put("end_look_time","最后一次查看时间");
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request,"商品关注度分析");
            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();

    }

}
