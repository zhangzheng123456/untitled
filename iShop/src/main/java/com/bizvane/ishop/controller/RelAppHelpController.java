package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.AppHelp;
import com.bizvane.ishop.entity.RelAppHelp;
import com.bizvane.ishop.service.AppHelpService;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.RelAppHelpService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.avro.data.Json;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/23.
 */
@Controller
public class RelAppHelpController {
    String id;
    private static final Logger logger = Logger.getLogger(AppHelpController.class);
    @Autowired
    RelAppHelpService relAppHelpService;
    @Autowired
    AppHelpService appHelpService;
    @Autowired
    private BaseService baseService;

    /**
     * 新增
     */
    @RequestMapping(value = "/relAppHelp/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addAppHelp(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = this.relAppHelpService.insert(message, user_id,request);

            if (result.equals("该编号已存在")) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            } else if (result.equals("该名称已存在")) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            } else if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(result);
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
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "系统管理_帮助文章";
                String action = Common.ACTION_ADD;
                String t_corp_code = "";
               // String t_code = action_json.get("rel_help_code").toString();
                String t_name = action_json.get("app_help_title").toString();
                String remark = "";
                baseService.insertUserOperation("", operation_user_code, function, action, t_corp_code, "", t_name,remark);
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
     * 编辑
     */
    @RequestMapping(value = "/relAppHelp/edit", method = RequestMethod.POST)
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

            String result = relAppHelpService.update(message, user_id,request);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
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
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "系统管理_帮助文章";
                String action = Common.ACTION_UPD;
                String t_corp_code = "";
               // String t_code = action_json.get("rel_help_code").toString();
                String t_name = action_json.get("app_help_title").toString();
                String remark = "";
                baseService.insertUserOperation("", operation_user_code, function, action, t_corp_code, "", t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
    /**
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/relAppHelp/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {

            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String rel_help_id = jsonObject.get("id").toString();

            String[] ids = rel_help_id.split(",");

            for (int i = 0; i < ids.length; i++) {
                RelAppHelp relAppHelp=relAppHelpService.getRelAppHelpById(Integer.parseInt(ids[i]));
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
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "系统管理_帮助文章";
                String action = Common.ACTION_DEL;
                String t_corp_code = "";
              //  String t_code = relAppHelp.getRel_help_code();
                String t_name = relAppHelp.getApp_help_title();
                String remark = "";
                baseService.insertUserOperation("", operation_user_code, function, action, t_corp_code, "", t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

                relAppHelpService.delete(Integer.parseInt(ids[i]));

            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);

            dataBean.setId(id);
            dataBean.setMessage("delete success");


        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }

        return dataBean.getJsonStr();
    }


    /**
     * 页面查找
     */
    @RequestMapping(value = "/relAppHelp/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();

        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            JSONObject result = new JSONObject();
            PageInfo<RelAppHelp> list = relAppHelpService.getAllRelAppHelpByPage(page_number, page_size,  search_value);

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

    /**
     * 筛选
     */
    @RequestMapping(value = "/relAppHelp/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();

            Map<String, String> map = WebUtils.Json2Map(jsonObject);

                    PageInfo<RelAppHelp> list =relAppHelpService.getAllRelAppHelpsScreen(page_number, page_size,  map);

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
     * 根据id查看
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/relAppHelp/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vipRules_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(relAppHelpService.getRelAppHelpById(Integer.parseInt(vipRules_id)));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }


        return dataBean.getJsonStr();
    }


    /**
     * 验证标题的唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/relAppHelp/appHelpTitleExist", method = RequestMethod.POST)
    @ResponseBody
    public String titleExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String app_help_code = jsonObject.get("app_help_code").toString().trim();
            String app_help_title = jsonObject.get("app_help_title").toString().trim();

            RelAppHelp relAppHelp = relAppHelpService.getByHelpTitle( app_help_code,app_help_title ,Common.IS_ACTIVE_Y);
            if (relAppHelp != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("当前企业下该标题已存在");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("当前企业下该标题不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/relAppHelp/getRelAppHelps", method = RequestMethod.POST)
    @ResponseBody
    public String getRelAppHelpsForApp(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSON.parseObject(message);
            String app_help_code = jsonObject.get("app_help_code").toString().trim();
            JSONObject result = new JSONObject();
            List<RelAppHelp> list = relAppHelpService.getByAppHelpCode(app_help_code );
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
    //手机列表
    @RequestMapping(value = "/api/getRelAppHelps", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getRelAppHelps(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            JSONObject result = new JSONObject();
           List<AppHelp> list_help= appHelpService.getAppHelps(Common.IS_ACTIVE_Y);
            JSONArray arr = new JSONArray();
            for (int i = 0; i < list_help.size(); i++) {
                String app_help_code = list_help.get(i).getApp_help_code();
                String app_help_name = list_help.get(i).getApp_help_name();
                JSONObject obj1 = new JSONObject();
                obj1.put("app_help_code", app_help_code);
                obj1.put("app_help_name", app_help_name);
                System.out.println("===app_help_code===" + app_help_code);
                List<RelAppHelp> list = relAppHelpService.getByAppHelpCode(app_help_code);
                System.out.println(list.size() + "======");
                JSONArray array1 = new JSONArray();
                for (int j = 0; j < list.size(); j++) {
                    String app_help_title = list.get(j).getApp_help_title();
                    array1.add(app_help_title);

                }

                JSONObject obj = new JSONObject();
                obj.put("rel_app_help", obj1);
                obj.put("app_help", array1);
                arr.add(obj);
            }
                result.put("list", arr.toJSONString());
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

    //手机列表

    @RequestMapping(value = "/api/getRelDetail", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getRelDetail(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSON.parseObject(message);
            String app_help_code = jsonObject.get("app_help_code").toString().trim();
            String app_help_title = jsonObject.get("app_help_title").toString().trim();

            JSONObject result=new JSONObject();
            RelAppHelp rel_app_help=relAppHelpService.getByHelpTitle(app_help_code,app_help_title,Common.IS_ACTIVE_Y);
            if (null!=rel_app_help){
              String content=  rel_app_help.getApp_help_content();
                String title=rel_app_help.getApp_help_title();
                result.put("content",content);
                result.put("title",title);
            }
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


}
