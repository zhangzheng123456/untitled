package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.StoreAchvGoalService;
import com.github.pagehelper.PageInfo;
import org.omg.CORBA.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * Created by zhouying on 2016-04-20.
 */

/**
 * 业绩管理
 */
@Controller
@RequestMapping("/achv")
public class AchvController {

    private static Logger logger = LoggerFactory.getLogger((CorpController.class));

    @Autowired
    private FunctionService functionService;

    @Autowired
    private StoreAchvGoalService storeAchvGoalService;


    /**
     * 店铺业绩目标
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @ResponseBody
    public String shopGoalManage(HttpServletRequest request) {
        return "shopGoalManage";
    }
//    @RequestMapping(value = "/shopgoal", method = RequestMethod.GET)
//    @ResponseBody
//    public String shopGoalManage(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String role_code = request.getSession(false).getAttribute("role_code").toString();
//        String group_code = request.getSession(false).getAttribute("group_code").toString();
//
//        String function_code = request.getSession(false).getAttribute("funcCode").toString();
//        int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
//        int page_number = Integer.parseInt(request.getParameter("pageNumber").toString());
//        int page_size = Integer.parseInt(request.getParameter("pageSize"));
//        try {
//            JSONArray actions = functionService.selectActionByFun(user_id, role_code, function_code, group_code);
//            JSONObject result = new JSONObject();
//            PageInfo<StoreAchvGoal> list = null;
//            if (role_code.contains(Common.ROLE_SYS)) {
//                list = storeAchvGoalService.selectBySearch(page_number, page_size, "", "");
//            } else {
//                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "");
//            }
//            result.put("list", JSON.toJSONString(list));
//            result.put("actions", actions);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (SQLException e) {
//            dataBean.setId("1");
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(e.getMessage());
//            e.printStackTrace();
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 店铺业绩目标
     * 新增
     */
    @RequestMapping(value = "/shopgoal/add", method = RequestMethod.POST)
    @ResponseBody
    public String addShopGoal(HttpServletRequest request) {
        return "shopgoal_add";
    }

    /**
     * 店铺业绩目标
     * 编辑
     */
    @RequestMapping(value = "/shopgoal/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editShopGoal(HttpServletRequest request) {
        return "shopgoal_edit";
    }

    /**
     * 店铺业绩目标
     * 查找
     */
    @RequestMapping(value = "/shopgoal/find", method = RequestMethod.POST)
    @ResponseBody
    public String findShopGoal(HttpServletRequest request) {
        return "";
    }


    /**
     * 员工业绩目标
     */
    @RequestMapping(value = "/staffgoal/list", method = RequestMethod.GET)
    @ResponseBody
    public String staffGoalManage(HttpServletRequest request) {
        return "staffgoal";
    }

    /**
     * 员工业绩目标
     * 新增
     */
    @RequestMapping(value = "/staffgoal/add", method = RequestMethod.POST)
    @ResponseBody
    public String addStaffGoal(HttpServletRequest request) {
        return "staffgoal_add";
    }


    /**
     * 员工业绩目标
     * 编辑
     */
    @RequestMapping(value = "/staffgoal/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editStaffGoal(HttpServletRequest request) {
        return "staffgoal_edit";
    }

    /**
     * 员工业绩目标
     * 查找
     */
    @RequestMapping(value = "/staffgoal/find", method = RequestMethod.POST)
    @ResponseBody
    public String findStaffGoal(HttpServletRequest request) {

        return "";
    }


}
