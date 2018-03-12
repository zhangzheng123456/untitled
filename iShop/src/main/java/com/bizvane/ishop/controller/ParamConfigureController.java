package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.entity.ParamConfigure;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yan on 2016/8/10.
 */


@Controller
@RequestMapping("/param")

public class ParamConfigureController {

    String id;
    @Autowired
    private CorpParamService corpParamService;
    @Autowired
    ParamConfigureService paramConfigureService;
    @Autowired
    private BaseService baseService;
    private static final Logger logger = Logger.getLogger(ParamConfigureController.class);

//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String paramManage(HttpServletRequest request) {
//
//        DataBean dataBean = new DataBean();
//        try {
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            JSONObject result = new JSONObject();
//            PageInfo<ParamConfigure> list = null;
//            list = paramConfigureService.getAllParamByPage(page_number, page_size, "");
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 添加参数配置
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addParam(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--area add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String user_id = request.getSession().getAttribute("user_code").toString();
            String result = paramConfigureService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                String param_name = jsonObject.get("param_name").toString();
                String isactive = jsonObject.get("isactive").toString();
                ParamConfigure paramConfigure = paramConfigureService.getParamByKey(param_name,Common.IS_ACTIVE_Y);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(paramConfigure.getId()));


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
                String function = "系统管理_参数定义";
                String action = Common.ACTION_ADD;
                String t_corp_code = "";
                String t_code = action_json.get("param_name").toString();
                String t_name = action_json.get("param_type").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
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
     * 编辑参数
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editParam(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--param ---- edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String user_id = request.getSession().getAttribute("user_code").toString();
            String result = paramConfigureService.update(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("param edit success");

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
                String function = "系统管理_参数定义";
                String action = Common.ACTION_UPD;
                String t_corp_code ="";
                String t_code = action_json.get("param_name").toString();
                String t_name = action_json.get("param_type").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
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
     * 删除参数
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) throws SQLException {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            logger.info("-------------delete--" + id);
            String param_id = jsonObject.get("id").toString();
            String[] ids = param_id.split(",");
            String msg = null;
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                ParamConfigure paramConfigure = paramConfigureService.getParamById(Integer.valueOf(ids[i]));
                if (paramConfigure != null) {
                    List<CorpParam> corpParam = corpParamService.selectByParamId(ids[i]);
                    if (corpParam.size() > 0) {
                        msg = "有使用参数" + paramConfigure.getParam_name() + "的企业参数，请先删除企业参数";
                        break;
                    }
                }

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
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "系统管理_参数定义";
                String action = Common.ACTION_DEL;
                String t_corp_code = "";
                String t_code = paramConfigure.getParam_name();
                String t_name = paramConfigure.getParam_type();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
            if (msg == null) {
                for (int i = 0; i < ids.length; i++) {
                    paramConfigureService.delete(Integer.valueOf(ids[i]));
                }
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("删除成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }

        return dataBean.getJsonStr();
    }

    /**
     * 选择参数
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findParamById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");

            logger.info("json-select-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String user_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(paramConfigureService.getParamById(Integer.parseInt(user_id)));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("参数信息异常");
        }

        return dataBean.getJsonStr();
    }

    /**
     * 搜索参数
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            JSONObject result = new JSONObject();
            PageInfo<ParamConfigure> list = null;
            list = paramConfigureService.selectByParamSearch(page_number, page_size, search_value);
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
     * 根据用户获取
     * 参数列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getParamInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getParamInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject params = new JSONObject();
            JSONArray array = new JSONArray();
            List<ParamConfigure> list = paramConfigureService.getAllParams();
            for (int i = 0; i < list.size(); i++) {
                ParamConfigure paramConfigure = list.get(i);
                String param_key = paramConfigure.getParam_name();
                String param_type = paramConfigure.getParam_type();
                String param_values = paramConfigure.getParam_values();
                int getParam_id = paramConfigure.getId();
                JSONObject obj = new JSONObject();
                obj.put("param_key", param_key);
                obj.put("param_id", getParam_id);
                obj.put("param_type", param_type);
                obj.put("param_values", param_values);
                obj.put("param_desc", paramConfigure.getParam_desc());
                array.add(obj);
            }
            params.put("params", array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(params.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }

        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-------screen--------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            JSONObject result = new JSONObject();
            PageInfo<ParamConfigure> list = null;
            list = paramConfigureService.selectParamScreen(page_number, page_size, map);
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
}
