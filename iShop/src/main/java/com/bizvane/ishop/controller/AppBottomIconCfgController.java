package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.AppBottomIConCfg;


import com.bizvane.ishop.service.AppBottomIconCfgService;

import com.bizvane.ishop.service.BaseService;

import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
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
 * Created by nanji on 2017/2/21.
 */
@Controller
@RequestMapping("/appBottomIconCfg")
public class AppBottomIconCfgController {
    String id;
    private static final Logger logger = Logger.getLogger(AppBottomIconCfgController.class);
    @Autowired
    AppBottomIconCfgService appBottomIconCfgService;
    @Autowired
    private BaseService baseService;

    /**
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addAppBottomIconCfg(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject obj=JSON.parseObject(jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
          //  logger.info("========================"+message+"============================="+obj.getString("message"));
            String result = this.appBottomIconCfgService.insert(message, user_id);
            logger.info("===============result============"+result);
         if(result.equals("该企业已存在模块配置")){
           dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(result);
        }else
            if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("error");
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
                JSONObject action_json = JSONObject.parseObject(message);
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "系统管理_模块管理";
                String action = Common.ACTION_ADD;
                String t_corp_code = "";
                String t_code = action_json.get("corp_code").toString();
                // String t_name = action_json.get("").toString();
                String remark = "";
                baseService.insertUserOperation("", operation_user_code, function, action, t_corp_code, t_code, "", remark);
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

            String result = appBottomIconCfgService.update(message, user_id);
            logger.info("===============result============"+result);
            if(result.equals("该企业已存在模块配置")){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }else if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
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
                JSONObject action_json = JSONObject.parseObject(message);
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "系统管理_爱秀模块管理";
                String action = Common.ACTION_UPD;
                String t_corp_code = "";
                //String t_code = action_json.get("app_help_code").toString();
                String t_name = action_json.get("corp_code").toString();
                String remark = "";
                baseService.insertUserOperation("", operation_user_code, function, action, t_corp_code, t_name, "", remark);
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
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
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
            String help_id = jsonObject.get("id").toString();
            String msg = null;
            String[] ids = help_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                AppBottomIConCfg appBottomIConCfg = appBottomIconCfgService.getAppBottomIConCfgById(Integer.parseInt(ids[i]));

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
                String function = "系统管理_爱秀模块管理";
                String action = Common.ACTION_DEL;
                String t_corp_code = "";
                String t_code = appBottomIConCfg.getCorp_code();
                String t_name = appBottomIConCfg.getCorp_name();

                String remark = "";
                baseService.insertUserOperation("", operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
                appBottomIconCfgService.delete(Integer.valueOf(ids[i]));
            }

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("删除成功");


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
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

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
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员(官方画面)

                PageInfo<AppBottomIConCfg> list = appBottomIconCfgService.getAppBottomIConCfgByPage(page_number, page_size, search_value);
                result.put("list", JSON.toJSONString(list));
            }

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
     * 页面查找
     */
    @RequestMapping(value = "/list_user", method = RequestMethod.GET)
    @ResponseBody
    public String searchBuUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            JSONObject result = new JSONObject();
            if (!role_code.equals(Common.ROLE_SYS)) {
                //用户画面
                AppBottomIConCfg appBottomIConCfg = appBottomIconCfgService.getAppBottomIConCfgByCorp(Common.IS_ACTIVE_Y, corp_code);
                if (appBottomIConCfg == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("vips", "Y");
                    obj.put("community", "Y");
                    obj.put("achievement", "Y");
                    obj.put("goods", "Y");
                    result.put("list", obj.toJSONString());
                    result.put("state", "add");

                }else{
                  //  logger.info("============appBottomIConCfg==============");
                   String info= appBottomIConCfg.getCfg_info();
                    JSONObject obj=JSON.parseObject(info);

                    result.put("list", info);
                    result.put("state", "edit");
                    result.put("id", appBottomIConCfg.getId());
                    }
                }

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
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
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


            PageInfo<AppBottomIConCfg> list = appBottomIconCfgService.getAppBottomIConCfgScreen(page_number, page_size, map);


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
    @RequestMapping(value = "/select", method = RequestMethod.POST)
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
            data = JSON.toJSONString(appBottomIconCfgService.getAppBottomIConCfgById(Integer.parseInt(vipRules_id)));
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
     * 验证企业
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/selectByCorp", method = RequestMethod.POST)
    @ResponseBody
    public String codeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString().trim();
            AppBottomIConCfg appBottomIConCfg = appBottomIconCfgService.getAppBottomIConCfgByCorp(corp_code, Common.IS_ACTIVE_Y);
            if (appBottomIConCfg != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("该企业已存在模板配置");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("该企业可以配置模板");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 图标列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/iconList", method = RequestMethod.POST)
    @ResponseBody
    public String iconList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("{\"vips\":" +   "\" 会员\""+ ","
                    + "\"community\":" +"\" 社区\""+ ","
                    + "\"achievement\":" +"\" 业绩\""+ ","
                    + "\"goods\":" + "\" 商品\""+ "}");
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(buffer.toString());

        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


}
