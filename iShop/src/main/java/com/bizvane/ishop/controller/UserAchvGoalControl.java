package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.UserAchvGoalService;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
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

    @Autowired
    private UserAchvGoalService userAchvGoalService = null;
    @Autowired
    private FunctionService functionService = null;

    String id;

    /**
     * 用户管理
     * 用户业绩目标的列表展示
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
            String user_code = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String function_code = request.getParameter("funcCode").toString();
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);

            org.json.JSONObject result = new org.json.JSONObject();

            int page_number = Integer.parseInt(request.getParameter("pageNumber").toString());
            int page_size = Integer.parseInt(request.getParameter("pageSize").toString());
            PageInfo<UserAchvGoal> pages = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                pages = userAchvGoalService.selectBySearch(page_number, page_size, "", "");
            } else {
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


    /**
     * 用户业绩目标
     * 编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editUserAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getAttribute("user_id").toString();
        String id = request.getSession(false).getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);

            UserAchvGoal userAchvGoal = new UserAchvGoal();
            userAchvGoal.setId(Integer.parseInt(jsonObj.get("id").toString()));
            userAchvGoal.setCorp_code(jsonObj.get("corp_code").toString());
            userAchvGoal.setStore_code(jsonObj.get("store_code").toString());
            userAchvGoal.setUser_code(jsonObj.get("user_code").toString());
            userAchvGoal.setUser_name(jsonObj.get("user_name").toString());
            userAchvGoal.setAchv_goal(Double.parseDouble(jsonObj.get("achv_goal").toString()));
            String achv_type = jsonObj.get("achv_type").toString();
            userAchvGoal.setAchv_type(achv_type);

            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
                String time = jsonObj.get("end_time").toString();
                String week = TimeUtils.getWeek(time);
                userAchvGoal.setEnd_time(week);
            } else {
                userAchvGoal.setEnd_time(jsonObj.get("end_time").toString());
            }
            Date now = new Date();
            userAchvGoal.setModifier(user_id);
            userAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setCreater(user_id);
            userAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setIsactive(jsonObj.get("isactive").toString());

            userAchvGoalService.updateUserAchvGoal(userAchvGoal);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩目标
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
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
     * 用户业绩目标
     * 添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
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
            UserAchvGoal userAchvGoal = new UserAchvGoal();
            userAchvGoal.setId(Integer.parseInt(jsonObject.get("id").toString()));
            userAchvGoal.setCorp_code(jsonObject.get("corp_code").toString());
            userAchvGoal.setStore_code(jsonObject.get("store_code").toString());
            userAchvGoal.setUser_code(jsonObject.get("user_code").toString());
            userAchvGoal.setUser_name(jsonObject.get("user_name").toString());
            userAchvGoal.setAchv_goal(Double.parseDouble(jsonObject.get("achv_goal").toString()));
            String achv_type = jsonObject.get("achv_type").toString();
            userAchvGoal.setAchv_type(achv_type);

            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
                String time = jsonObject.get("end_time").toString();
                String week = TimeUtils.getWeek(time);
                userAchvGoal.setEnd_time(week);
            } else {
                userAchvGoal.setEnd_time(jsonObject.get("end_time").toString());
            }
            Date now = new Date();
            userAchvGoal.setModifier(user_id);
            userAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setCreater(user_id);
            userAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setIsactive(jsonObj.get("isactive").toString());

//            String existInfo = this.userAchvGoalService.userAchvGoalExist(userAchvGoal.getUser_code());
//            if (existInfo.equals(Common.DATABEAN_CODE_ERROR)) {
            userAchvGoalService.insert(userAchvGoal);
//            } else {
//                userAchvGoalService.updateUserAchvGoal(userAchvGoal);
//    }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("add SUCCESS！");

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
     * 用户业绩管理
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
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
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
