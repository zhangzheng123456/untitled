package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.UserAchvGoalService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
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
@RequestMapping("/userAchvGoal")
public class UserAchvGoalControl {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(UserAchvGoalControl.class);

    private SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);
    @Autowired
    private UserAchvGoalService userAchvGoalService = null;
    @Autowired
    private FunctionService functionService = null;

    /**
     * 用户管理
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String userAchvGoalManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            //int user_id = Integer.parseInt(request.getParameter("user_id").toString());
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            //String role_code = request.getParameter("role_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();

            String function_code = request.getParameter("funcCode").toString();
            JSONArray actions = functionService.selectActionByFun(user_id, role_code, function_code, group_code);
            org.json.JSONObject result = new org.json.JSONObject();

            int page_number = Integer.parseInt(request.getParameter("pageNumber").toString());
            int page_size = Integer.parseInt(request.getParameter("pageSize").toString());
            PageInfo<UserAchvGoal> pages = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                pages = userAchvGoalService.selectBySearch(page_number, page_size, "", "");
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                pages = this.userAchvGoalService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(pages));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editUserAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getAttribute("user_id").toString();
        String id = request.getSession(false).getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            UserAchvGoal userAchvGoal = WebUtils.request2Bean(request, UserAchvGoal.class);
            userAchvGoalService.updateUserAchvGoal(userAchvGoal);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");
        } catch (SQLException e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteUserAchvGoalById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String user_id = jsonObj.get("id").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                userAchvGoalService.deleteUserAchvGoalById(ids[i]);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (SQLException e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String insertUserAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user add-------------" + jsString);
            System.out.println("json---------------" + jsString);

            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            //   UserAchvGoal userAchvGoal = WebUtils.request2Bean(request, UserAchvGoal.class);
            UserAchvGoal userAchvGoal = new UserAchvGoal();
            Date now = new Date();
            userAchvGoal.setCreater(user_id);
            userAchvGoal.setStore_code(jsonObject.get("store_code").toString());

            userAchvGoal.setUser_code(jsonObject.getString("user_code"));
            userAchvGoal.setCorp_code(jsonObject.getString("corp_code"));
            //   userAchvGoal.setUser_name();
            userAchvGoal.setAchv_goal(jsonObject.getDouble("achv_goal"));
            userAchvGoal.setAchv_type(jsonObject.getString("achv_type"));
            userAchvGoal.setStart_time(now);
            userAchvGoal.setEnd_time(sdf.parse(jsonObject.getString("end_time")));
            userAchvGoal.setModified_date(now);
            userAchvGoal.setCreater(user_id);
            userAchvGoal.setIsactive(jsonObject.getString("isactive"));
            userAchvGoal.setCorp_code(jsonObject.getString("corp_code"));
            userAchvGoal.setCreated_date(now);
            userAchvGoal.setCreater(user_id);


            String existInfo = this.userAchvGoalService.userAchvGoalExist(userAchvGoal.getUser_code());
            if (existInfo.equals(Common.DATABEAN_CODE_ERROR)) {
                userAchvGoalService.insert(userAchvGoal);
            } else {
                userAchvGoalService.updateUserAchvGoal(userAchvGoal);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("修改SUCCESS   ！！！！");

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩目标管理
     * 选择用户业绩目标
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();


            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String userAchvGoalId = jsonObject.get("id").toString();
            data = JSON.toJSONString(userAchvGoalService.getUserAchvGoalById(Integer.parseInt(userAchvGoalId)));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        String id = "";
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNum").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<UserAchvGoal> list;
            if (role_code.contains(Common.ROLE_SYS)) {
                //系统管理员
                list = userAchvGoalService.selectBySearch(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = userAchvGoalService.selectBySearch(page_number, page_size, corp_code, search_value);
            }
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
