package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.AppIcon;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.AppIconCfg;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/5/4.
 */

@Controller
@RequestMapping("/appIconCfg")
public class AppIconCfgController {


    @Autowired
    AppIconCfgService appIconCfgService;
    @Autowired
    AppIconService appIconService;
    @Autowired
    private BaseService baseService;
    private static final Logger logger = Logger.getLogger(com.bizvane.ishop.controller.AppIconCfgController.class);

    String id;


        /**
         * 图标顺序配置
         * 添加
         */
        @RequestMapping(value = "/add", method = RequestMethod.POST)
        @ResponseBody
        @Transactional
        public String addAppIconCfg(HttpServletRequest request) {
            DataBean dataBean = new DataBean();
            String user_id = request.getSession(false).getAttribute("user_code").toString();
            String id = "";
            try {
                Date now=new Date();
                String jsString = request.getParameter("param");
                 JSONObject jsonObj = JSONObject.parseObject(jsString);
                id = jsonObj.get("id").toString();
                String message = jsonObj.get("message").toString();
                JSONObject jsonObject = JSONObject.parseObject(message);
                String corp_code = jsonObject.getString("corp_code");
                String isactive = jsonObject.get("isactive").toString().trim();
                AppIconCfg appIconCfg = WebUtils.JSON2Bean(jsonObject, AppIconCfg.class);
                String order = jsonObject.get("icon_order").toString();
                String result = "";
               AppIconCfg appIconCfg2= appIconCfgService.getIconCfgByCorp(corp_code,Common.IS_ACTIVE_Y);
                //若存在该企业的配置就编辑，不存在就新增
                if (appIconCfg2 == null ) {

                    appIconCfg.setCreater(user_id);
                    appIconCfg.setCorp_code(corp_code);
                    appIconCfg.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    appIconCfg.setModifier(user_id);
                    appIconCfg.setIcon_order(order);
                    appIconCfg.setCreater(user_id);
                    appIconCfg.setIsactive(isactive);
                    appIconCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
                    result = this.appIconCfgService.insert(appIconCfg);
                    if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        AppIconCfg appIconCfg1= appIconCfgService.getIconCfgByCorp(appIconCfg.getCorp_code(),Common.IS_ACTIVE_Y);

                        dataBean.setMessage(appIconCfg1.getId());
                    } else {
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage(result);
                    }
                } else{
                    appIconCfg.setCorp_code(corp_code);
                    appIconCfg.setIcon_order(order);
                    appIconCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
                    appIconCfg.setModifier(user_id);
                    appIconCfg.setId(appIconCfg2.getId());
                    result = this.appIconCfgService.update(appIconCfg);
                    if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setMessage(appIconCfg.getId());
                    } else {
                        dataBean.setId(id);
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage(result);
                    }
                }
                if (dataBean.getCode().equals(Common.DATABEAN_CODE_SUCCESS)){
                    //----------------行为日志------------------------------------------
                    /**
                     * mongodb插入用户操作记录
                     * @param operation_corp_code 操作者corp_code
                     * @param operation_user_code 操作者user_code
                     * @param function 功能
                     * @param action 动作
                     * @param corp_code 被操作corp_code
                     * @param code 被操作code
                     * @param name 被操作name
                     * @throws Exception
                     */
                    com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
                    String function = "会员管理_APP会员板块管理";
                    String action = Common.ACTION_ADD;
                    String t_corp_code = action_json.get("corp_code").toString();
                    String t_code = "";
                    String t_name = "";
                    String remark = "";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                    //-------------------行为日志结束-----------------------------------------------------------------------------------

                }

            } catch (Exception ex) {
                ex.printStackTrace();
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(ex.getMessage());
            }
            return dataBean.getJsonStr();
        }

    /**
     * 排序列表
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<AppIconCfg> list=null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = appIconCfgService.selectAllIconCfg(page_number, page_size,"", search_value);
            } else if(role_code.contains(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                list = appIconCfgService.selectAllIconCfg(page_number, page_size,"", search_value,manager_corp);
            }else if (role_code.equals(Common.ROLE_GM)) {
                list = appIconCfgService.selectAllIconCfg(page_number, page_size,corp_code, search_value);

            }
            JSONObject result = new JSONObject();

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


    @RequestMapping(value = "/icon/list", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String iconList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<AppIcon> list;

            list = appIconService.selectAllIcons(page_number, page_size, search_value);
            JSONObject result = new JSONObject();
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
                for (int i = 0; i < ids.length; i++) {
                    AppIconCfg appIconCfg=appIconCfgService.getIconCfgById(Integer.valueOf(ids[i]));
                   String corp= appIconCfg.getCorp_code();

                    logger.info("-------------delete--" + Integer.valueOf(ids[i]));

                    //----------------行为日志开始------------------------------------------
                    /**
                     * mongodb插入用户操作记录
                     * @param operation_corp_code 操作者corp_code
                     * @param operation_user_code 操作者user_code
                     * @param function 功能
                     * @param action 动作
                     * @param corp_code 被操作corp_code
                     * @param code 被操作code
                     * @param name 被操作name
                     * @throws Exception
                     */
                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
                    String function = "会员管理_APP会员板块管理";
                    String action = Common.ACTION_DEL;
                    String t_corp_code = appIconCfg.getCorp_code();
                    String t_code = "";
                    String t_name = "";
                    String remark = "";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                    //-------------------行为日志结束-----------------------------------------------------------------------------------

                    appIconCfgService.delete(Integer.valueOf(appIconCfg.getId()));
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(Common.DATABEAN_CODE_SUCCESS);
            } catch (Exception ex) {
                ex.printStackTrace();
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
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
        String user_code = request.getSession(false).getAttribute("user_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            JSONObject result = new JSONObject();
            PageInfo<AppIconCfg> list=null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = appIconCfgService.selectIconCfgAllScreen(page_number, page_size, "", map);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = appIconCfgService.selectIconCfgAllScreen(page_number, page_size, corp_code, map);
            }
            list = appIconCfgService.selectIconCfgAllScreen(page_number, page_size, corp_code, map);
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 获取详细信息
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
            String cfg_id = jsonObject.get("id").toString();
            AppIconCfg appIconCfg = appIconCfgService.getIconCfgById(Integer.valueOf(cfg_id));

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(JSON.toJSONString(appIconCfg));
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editVipRules(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Date now = new Date();
            String corp_code = jsonObject.get("corp_code").toString().trim();
            String isactive = jsonObject.get("isactive").toString().trim();
            String order = jsonObject.get("icon_order").toString().trim();
            String id = jsonObject.get("id").toString().trim();

              AppIconCfg appIconCfg=new AppIconCfg();
            appIconCfg.setCorp_code(corp_code);
            appIconCfg.setModifier(user_id);
            appIconCfg.setId(id);
            appIconCfg.setIcon_order(order);
            appIconCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
            appIconCfg.setIsactive(isactive);
                String result1=appIconCfgService.update(appIconCfg);
                if (result1.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage("edit success");
                    //----------------行为日志开始------------------------------------------
                    /**
                     * mongodb插入用户操作记录
                     * @param operation_corp_code 操作者corp_code
                     * @param operation_user_code 操作者user_code
                     * @param function 功能
                     * @param action 动作
                     * @param corp_code 被操作corp_code
                     * @param code 被操作code
                     * @param name 被操作name
                     * @throws Exception
                     */
                    com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
                    String function = "会员管理_APP会员板块管理 ";
                    String action = Common.ACTION_UPD;
                    String t_corp_code = action_json.get("corp_code").toString();
                    String t_code = "";
                    String t_name ="";
                    String remark = "";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                    //-------------------行为日志结束-----------------------------------------------------------------------------------

                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage(result1);
                }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/updateShowOrder", method = RequestMethod.POST)
    @ResponseBody
    public String updateShowOrder(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String params = jsonObject.get("icon_order").toString();

            JSONArray array = JSONArray.parseArray(params);
            for (int i = 0; i < array.size(); i++) {
                String object = array.get(i).toString();
                JSONObject obj = JSONObject.parseObject(object);
                String icon_name = obj.get("name").toString();
                String show_order = obj.get("show_order").toString();
                appIconService.updateShowOrder(icon_name,show_order);

            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        return dataBean.getJsonStr();
    }
}
