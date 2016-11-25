package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by nanji on 2016/11/16.
 */
@Controller
@RequestMapping("/activity")
public class ActivityVipController {
    @Autowired
    private ActivityVipService activityVipService;

    private static final Logger logger = Logger.getLogger(ActivityVipController.class);

    String id;

    /**
     * 活动
     * 列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String activityVipList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONObject result = new JSONObject();
            PageInfo<ActivityVip> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.activityVipService.selectAllActivity(page_number, page_size, "", "");
            } else {
                list = activityVipService.selectAllActivity(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员活动
     * 添加
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addActivity(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);

            String result = this.activityVipService.insert(message, user_id, request);
            if (result.equals("新增失败")) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

//    /**
//     * 根据选择的目标会员
//     * 选择导购
//     */
//    @RequestMapping(value = "/selUserByVip", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String selUserByVip(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject obj = JSONObject.parseObject(message);
//            int page_number = Integer.valueOf(obj.get("pageNumber").toString());
//            int page_size = Integer.valueOf(obj.get("pageSize").toString());
//            String searchValue = obj.get("searchValue").toString();
//            String corp_code = obj.get("corp_code").toString();
//            JSONObject target_vips = obj.getJSONObject("target_vips");
//
//            PageInfo<User> userList = activityVipService.selUserByVip(page_number,page_size,corp_code,searchValue,target_vips);
//
//            JSONObject result = new JSONObject();
//            result.put("list", JSON.toJSONString(userList));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }



    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String edit(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            String result = activityVipService.update(message, user_id, request);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 活动搜索
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String role_code = request.getSession(false).getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<ActivityVip> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = activityVipService.selectAllActivity(page_number, page_size, "", search_value);
            } else {
                list = activityVipService.selectAllActivity(page_number, page_size, corp_code, search_value);
            }

            org.json.JSONObject result = new org.json.JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("search error");
        }
        return dataBean.getJsonStr();
    }
    

    /**
     * 删除活动
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String area_id = jsonObject.get("id").toString();
            String[] ids = area_id.split(",");
            String msg = "";
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                ActivityVip activityVip = activityVipService.getActivityById(Integer.valueOf(ids[i]));
                if (activityVip != null) {
                    String activity_state = activityVip.getActivity_state();
                    if (activity_state.equals("执行中")){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("执行中活动，不可删除");
                        break;
                    }
                }
                activityVipService.delete(Integer.parseInt(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }


    /**
     * 活动筛选
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<ActivityVip> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = activityVipService.selectActivityAllScreen(page_number, page_size, "", "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = activityVipService.selectActivityAllScreen(page_number, page_size, corp_code, "", map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 活动，点击执行
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/executeActivity", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public String executeActivity(HttpServletRequest request) throws Exception {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_code = request.getSession().getAttribute("user_code").toString();
        String jsString = request.getParameter("param");
        JSONObject jsonObj = JSONObject.parseObject(jsString);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = JSONObject.parseObject(message);
        try {
            int activity_id = Integer.parseInt(jsonObject.get("id").toString());

            ActivityVip activityVip = activityVipService.getActivityById(activity_id);
            String activity_state = activityVip.getActivity_state();
            if (!activity_state.equals("未执行")){
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("该任务非未执行状态");
            }else {
                String result = activityVipService.executeActivity(activityVip,user_code);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)){
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("execute success");
                }else {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("execute error");
        }
        return dataBean.getJsonStr();
    }

    /**
     *
     * 获取活动详细信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectActivity(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int activity_id = Integer.parseInt(jsonObject.getString("id"));
            ActivityVip activityVip = this.activityVipService.selectActivityById(activity_id);
            JSONObject result = new JSONObject();
            result.put("activityVip", JSON.toJSONString(activityVip));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 活动(执行中)
     * 获取活动执行情况
     * @param request
     * @return
     */
    @RequestMapping(value = "/executeDetail", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public String executeDetail(HttpServletRequest request) throws Exception {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int activity_id = Integer.parseInt(jsonObject.getString("id"));
            ActivityVip activityVip = activityVipService.selectActivityById(activity_id);
            String activity_state = activityVip.getActivity_state();
            String run_mode = activityVip.getRun_mode();
            String result = "";
            if (activity_state.equals("未执行")){
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("该活动未执行");
            }else {
                if (run_mode.contains("任务")){
                    JSONObject result_obj = activityVipService.executeDetail(activityVip);
                    result = result_obj.toString();
                }
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     *
     * 终止/恢复活动
     * @param request
     * @return
     */
    @RequestMapping(value = "/changeState", method = RequestMethod.POST)
    @ResponseBody
    public String changeState(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int activity_id = Integer.parseInt(jsonObject.getString("id"));
            ActivityVip activityVip = this.activityVipService.selectActivityById(activity_id);
            if (activityVip != null){
                String activity_state = activityVip.getActivity_state();
                if (activity_state.equals("执行中")){
                    activityVip.setActivity_state("已中止");
                    activityVipService.updateActivityVip(activityVip);
                }else if (activity_state.equals("已中止")){
                    activityVip.setActivity_state("执行中");
                    activityVipService.updateActivityVip(activityVip);
                }
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}

