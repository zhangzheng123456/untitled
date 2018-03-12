package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.TaskAllocation;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.NumberUtil;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by yin on 2016/7/28.
 */
@Controller
@RequestMapping("/taskAnalysis")
public class TaskAnalysisController {

    @Autowired
    private StoreService storeService;
    @Autowired
    private TaskService taskService;
    String id;


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String searchTaskAll(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";
            PageInfo<Store> list = new PageInfo<Store>();
            list.setList(new ArrayList<Store>());
                String search_value = jsonObject.get("searchValue").toString();
                if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM)) {
                    //系统管理员
                    list = storeService.getAllStore(request, page_number, page_size, corp_code, search_value, Common.IS_ACTIVE_Y, "","","","","All");
                } else if(role_code.equals(Common.ROLE_CM)){
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>"+manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>"+corp_code);
                    list = storeService.getAllStore(request, page_number, page_size, corp_code, search_value, Common.IS_ACTIVE_Y, "","","","","All");

                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    if (brand_code == null || brand_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属品牌");
                        return dataBean.getJsonStr();
                    } else {
                        //加上特殊字符，进行查询
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        list = storeService.selectByAreaBrand(page_number, page_size, corp_code, null, null, brandCodes, search_value, Common.IS_ACTIVE_Y, "","","","","All");
                    }
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    if (area_code == null || area_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属区域");
                        return dataBean.getJsonStr();
                    } else {
                        //加上特殊字符，进行查询
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areaCodes = area_code.split(",");
                        String[] storeCodes = null;
                        for (int i = 0; i < areaCodes.length; i++) {
                            areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                        }
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }
                        if (!storeCodes.equals(""))
                            list = storeService.selectByAreaBrand(page_number, page_size, corp_code, areaCodes, storeCodes, null, search_value, Common.IS_ACTIVE_Y, "","","","","All");
                    }
                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    if (store_code == null || store_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属店铺");
                        return dataBean.getJsonStr();
                    } else {
                        list = storeService.selectByUserId(page_number, page_size, store_code, corp_code, search_value,Common.IS_ACTIVE_Y);
                    }
                } else {
                    List<Store> list1 = new ArrayList<Store>();
                    list.setList(list1);
                }

            long total = list.getTotal();
            int pages = list.getPages();
            List<Store> stores = list.getList();

            JSONArray store_array = new JSONArray();
            for (int i = 0; i < stores.size(); i++) {
                Store store = stores.get(i);
                JSONObject store_obj = WebUtils.bean2JSONObject(store);

                int all_task_count = taskService.selStoreComTaskCount(corp_code,store.getStore_code(),"").size();
                int uncom_task_count = taskService.selStoreComTaskCount(corp_code,store.getStore_code(),"4").size();
                store_obj.put("task_count",all_task_count);
                store_obj.put("task_complete_count",all_task_count-uncom_task_count);
                String complete_rate = "100%";
                if (all_task_count != 0){
                    complete_rate = NumberUtil.percent(Double.valueOf(all_task_count-uncom_task_count)/all_task_count);
                }
                store_obj.put("complete_rate",complete_rate);
                store_array.add(store_obj);
            }

            JSONObject obj = new JSONObject();
            obj.put("pageSize",page_size);
            obj.put("pageNum",page_number);
            obj.put("count",total);
            obj.put("pages",pages);
            obj.put("list",store_array);

            JSONObject result = new JSONObject();
            result.put("list", obj.toString());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String screenList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            PageInfo<Store> list = new PageInfo<Store>();
            list.setList(new ArrayList<Store>());
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM)) {
                list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, "", "", "", map, "", Common.IS_ACTIVE_Y);
            } else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, "", "", "", map, "", Common.IS_ACTIVE_Y);

            }else if (role_code.equals(Common.ROLE_BM)) {
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                String area_codes = request.getSession(false).getAttribute("area_code").toString();
                list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, area_codes, brand_code, "", map, "", Common.IS_ACTIVE_Y);
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_codes = request.getSession(false).getAttribute("area_code").toString();
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, area_codes, "", "", map, store_code, Common.IS_ACTIVE_Y);
            } else {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, "", "", store_code, map, "", Common.IS_ACTIVE_Y);
            }

            long total = list.getTotal();
            int pages = list.getPages();
            List<Store> stores = list.getList();

            JSONArray store_array = new JSONArray();
            for (int i = 0; i < stores.size(); i++) {
                Store store = stores.get(i);
                JSONObject store_obj = WebUtils.bean2JSONObject(store);

                int all_task_count = taskService.selStoreComTaskCount(corp_code,store.getStore_code(),"").size();
                int uncom_task_count = taskService.selStoreComTaskCount(corp_code,store.getStore_code(),"4").size();
                store_obj.put("task_count",all_task_count);
                store_obj.put("task_complete_count",all_task_count-uncom_task_count);
                String complete_rate = "100%";
                if (all_task_count != 0){
                    complete_rate = NumberUtil.percent(Double.valueOf(all_task_count-uncom_task_count)/all_task_count);
                }
                store_obj.put("complete_rate",complete_rate);
                store_array.add(store_obj);
            }

            JSONObject obj = new JSONObject();
            obj.put("pageSize",page_size);
            obj.put("pageNum",page_number);
            obj.put("count",total);
            obj.put("pages",pages);
            obj.put("list",store_array);

            JSONObject result = new JSONObject();
            result.put("list", obj.toString());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/userlist", method = RequestMethod.POST)
    @ResponseBody
    public String selectUserlist(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS)){
                corp_code = jsonObject.get("corp_code").toString();
            }
            String store_code = jsonObject.get("store_code").toString();
            //全部
            List<TaskAllocation> tasks = taskService.selUserComTaskCount(corp_code,store_code,"");
            //已完成
            List<TaskAllocation> tasks1 = taskService.selUserComTaskCount(corp_code,store_code,"4");

            JSONArray users_array = new JSONArray();
            for (int i = 0; i < tasks.size(); i++) {
                int count = Integer.parseInt(tasks.get(i).getCount());
                String user_code = tasks.get(i).getUser_code();
                JSONObject obj = WebUtils.bean2JSONObject(tasks.get(i));
                int com_count = 0;
                if (count == 0){
                    obj.put("complete_rate","100%");
                }else {
                    DecimalFormat df = new DecimalFormat("#.##");
                    for (int j = 0; j < tasks1.size(); j++) {
                        String user_code1 = tasks1.get(j).getUser_code();
                        if (user_code.equals(user_code1)){
                            com_count = Integer.parseInt(tasks1.get(j).getCount());
                            break;
                        }
                    }
                    String complete_rate = df.format(Double.valueOf(com_count) / count * 100d);
                    obj.put("complete_rate",complete_rate+"%");
                }
                obj.put("task_complete_count",com_count);
                users_array.add(obj);
            }
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(users_array));
            result.put("total",users_array.size());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
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
        try {

            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            int page_number = 1;
            int page_size = 10000;
            if (jsonObject.containsKey("pageNumber")){
                page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            }
            if (jsonObject.containsKey("pageSize")){
                page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            }
            String screen = jsonObject.get("list").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";
            PageInfo<Store> list = new PageInfo<Store>();
            list.setList(new ArrayList<Store>());
            if (screen.equals("")){
                String search_value = jsonObject.get("searchValue").toString();
                if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM)) {
                    //系统管理员
                    list = storeService.getAllStore1(request, page_number, page_size, corp_code, search_value, Common.IS_ACTIVE_Y, "","","","","All");
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    if (brand_code == null || brand_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属品牌");
                        return dataBean.getJsonStr();
                    } else {
                        //加上特殊字符，进行查询
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        list = storeService.selectByAreaBrand(page_number, page_size, corp_code, null, null, brandCodes, search_value, Common.IS_ACTIVE_Y, "","","","","All");
                    }
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    if (area_code == null || area_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属区域");
                        return dataBean.getJsonStr();
                    } else {
                        //加上特殊字符，进行查询
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areaCodes = area_code.split(",");
                        String[] storeCodes = null;
                        for (int i = 0; i < areaCodes.length; i++) {
                            areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                        }
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }
                        if (!storeCodes.equals(""))
                            list = storeService.selectByAreaBrand(page_number, page_size, corp_code, areaCodes, storeCodes, null, search_value, Common.IS_ACTIVE_Y, "","","","","All");
                    }
                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    if (store_code == null || store_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属店铺");
                        return dataBean.getJsonStr();
                    } else {
                        list = storeService.selectByUserId(page_number, page_size, store_code, corp_code, search_value,Common.IS_ACTIVE_Y);
                    }
                } else {
                    List<Store> list1 = new ArrayList<Store>();
                    list.setList(list1);
                }
            }else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, "", "", "", map, "", Common.IS_ACTIVE_Y);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, area_codes, brand_code, "", map, "", Common.IS_ACTIVE_Y);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, area_codes, "", "", map, store_code, Common.IS_ACTIVE_Y);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, "", "", store_code, map, "", Common.IS_ACTIVE_Y);
                }
            }

            List<Store> stores = list.getList();
            if (stores.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }

            JSONArray store_array = new JSONArray();
            for (int i = 0; i < stores.size(); i++) {
                Store store = stores.get(i);
                JSONObject store_obj = WebUtils.bean2JSONObject(store);

                int all_task_count = taskService.selStoreComTaskCount(corp_code,store.getStore_code(),"").size();
                int uncom_task_count = taskService.selStoreComTaskCount(corp_code,store.getStore_code(),"4").size();
                store_obj.put("task_count",all_task_count);
                store_obj.put("task_complete_count",all_task_count-uncom_task_count);
                String complete_rate = "100%";
                if (all_task_count != 0){
                    complete_rate = NumberUtil.percent(Double.valueOf(all_task_count-uncom_task_count)/all_task_count);
                }
                store_obj.put("complete_rate",complete_rate);
                store_array.add(store_obj);
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(store_array);
            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json,stores, map, response, request,"");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

}
