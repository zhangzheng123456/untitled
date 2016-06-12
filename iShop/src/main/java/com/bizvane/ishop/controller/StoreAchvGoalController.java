package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.StoreAchvGoalService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Controller
@RequestMapping("/storeAchvGoal")
public class StoreAchvGoalController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    private static SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    @Autowired
    StoreAchvGoalService storeAchvGoalService = null;
    @Autowired
    FunctionService functionService = null;

    /**
     * 用户管理
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String getStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();

            String function_code = request.getSession(false).getAttribute("funcCode").toString();
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            int page_number = Integer.parseInt(request.getParameter("pageNumber").toString());
            int page_size = Integer.parseInt(request.getParameter("pageSize"));

            JSONArray actions = functionService.selectActionByFun(user_id, role_code, function_code, group_code);
            JSONObject result = new JSONObject();
            PageInfo<StoreAchvGoal> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = storeAchvGoalService.selectBySearch(page_number, page_size, "", "");
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (SQLException e) {
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 员工最新业绩目标修改或添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addStoreAchvGoal(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_id");
        String corp_code = WebUtils.getValueForSession(request, "corp_code");

        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            //StoreAchvGoal storeAchvGoal = WebUtils.request2Bean(request, StoreAchvGoal.class);
            // StoreAchvGoal storeAchvGoal=WebUtils.JSON2Bean(jsonObject,StoreAchvGoal.class);
            StoreAchvGoal storeAchvGoal1 = new StoreAchvGoal();
            storeAchvGoal1.setStore_name(jsonObject.get("store_name").toString());
            storeAchvGoal1.setStore_name(jsonObject.get("store_code").toString());
            storeAchvGoal1.setAchv_goal(Double.parseDouble(jsonObject.get("achv_goal").toString()));
            storeAchvGoal1.setAchv_type(jsonObject.get("achv_type").toString());
            storeAchvGoal1.setStart_time(new Date());
            storeAchvGoal1.setEnd_time(sdf.parse(jsonObject.get("end_time").toString()));
            storeAchvGoal1.setModifier(user_id);
            storeAchvGoal1.setModified_date(new Date());
            storeAchvGoal1.setCreater(user_id);
            storeAchvGoal1.setCreated_date(new Date());
            storeAchvGoal1.setIsactive(jsonObject.get("isactive").toString());


            String exist = storeAchvGoalService.storeAchvExist(corp_code, storeAchvGoal1.getStore_code());
            if (exist.equals(Common.DATABEAN_CODE_ERROR)) {
                //storeAchvGoalService.update(storeAchvGoal1);
                storeAchvGoalService.insert(storeAchvGoal1);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add error ");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("商户已存在！！");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String editbefore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
          int store_id = Integer.parseInt(jsonObject.get("id").toString());
            StoreAchvGoal storeAchvGoal = storeAchvGoalService.selectlById(store_id);
//            JSONObject result = new JSONObject();
//            result.put("storeAchvGoal", storeAchvGoal);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(JSON.toJSONString(storeAchvGoal));
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.toString();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_id");
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            StoreAchvGoal storeAchvGoal = WebUtils.JSON2Bean(jsonObject, StoreAchvGoal.class);
            storeAchvGoalService.update(storeAchvGoal);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success ");

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "1";
        try {
            String jsString = WebUtils.getValueForSession(request, "param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String storeAchvGoal_id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String[] ids = storeAchvGoal_id.split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                storeAchvGoalService.deleteById(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


}
