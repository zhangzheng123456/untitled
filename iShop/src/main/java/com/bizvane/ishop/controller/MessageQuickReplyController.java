package com.bizvane.ishop.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.MessageQuickReply;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.MessageQuickReplyService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nanji on 2016/9/10.
 */
@Controller
@RequestMapping("/quickReply")
public class MessageQuickReplyController {

    private static final Logger logger = Logger.getLogger(MessageQuickReplyController.class);
    @Autowired
    private MessageQuickReplyService messageQuickReplyService;
    @Autowired
    private BaseService baseService;
    String id;

    /**
     * 消息快捷回复列表
     *
     * @param request
     * @return
     */
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String quickReplyList(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("json---------------" + jsString);
//            JSONObject jsonObj = new JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            PageInfo<MessageQuickReply> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                //系统管理员
//                list = messageQuickReplyService.getAllQuickReplyByPage(page_number, page_size, "", "");
//            } else {
//                list = messageQuickReplyService.getAllQuickReplyByPage(page_number, page_size, corp_code, "");
//
//            }
//            JSONObject result = new JSONObject();
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage() + ex.toString());
//            logger.info(ex.getMessage() + ex.toString());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 根据id查询
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        try {
            JSONObject result = new JSONObject();
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String id = jsonObject.get("id").toString();
            data = JSON.toJSONString(messageQuickReplyService.getQuickReplyById(Integer.parseInt(id)));
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
     * 消息快捷回复管理-新增
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addQuickReply(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--addQuickReply add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String user_id = request.getSession().getAttribute("user_code").toString();
            String result = messageQuickReplyService.insert(message, user_id);
            if (result.equals("该消息快捷回复模板已存在")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);

                dataBean.setId(id);
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
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "消息管理_快捷回复";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("corp_code").toString();
                String t_name = action_json.get("content").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }

        return dataBean.getJsonStr();
    }

    /**
     * 消息快捷回复管理-编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String updateQuickReply(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json------updateQuickReply---------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = messageQuickReplyService.update(message, user_id);
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
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "消息管理_快捷回复";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("corp_code").toString();
                String t_name = action_json.get("content").toString();
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
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 消息快捷回复管理-删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteQuickReply(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--------deleteQuickReply-------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String quickReplyId = jsonObject.get("id").toString();
            String[] ids = quickReplyId.split(",");
            String msg = null;
            int count = 0;
            for (int i = 0; i < ids.length; i++) {
                logger.info("inter---------------" + Integer.valueOf(ids[i]));
                MessageQuickReply quickReplyById = messageQuickReplyService.getQuickReplyById(Integer.valueOf(ids[i]));
                messageQuickReplyService.delete(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");

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
                String function = "消息管理_快捷回复";
                String action = Common.ACTION_DEL;
                String t_corp_code = quickReplyById.getCorp_code();
                String t_code = quickReplyById.getCorp_code();
                String t_name = quickReplyById.getContent();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 消息快捷回复管理
     * 验证回复内容的唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/quickReplyCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String quickReplyCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String content = jsonObject.get("content").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            MessageQuickReply messageQuickReply = messageQuickReplyService.getQuickReplyByCode(corp_code, content, Common.IS_ACTIVE_Y);
            if (messageQuickReply != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("消息快捷回复内容已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("消息快捷回复内容不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 消息快捷回复管理-搜索
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String searchQuickReply(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<MessageQuickReply> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = messageQuickReplyService.getAllQuickReplyByPage(page_number, page_size, "", search_value);
            } else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = messageQuickReplyService.getAllQuickReplyByPage(page_number, page_size, corp_code, search_value);
                //  list = messageQuickReplyService.getAllQuickReplyByPage(page_number, page_size, "", search_value,manager_corp);
            }else {
                list = messageQuickReplyService.getAllQuickReplyByPage(page_number, page_size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 消息快捷回复管理
     * 筛选
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String quickReplyScreen(HttpServletRequest request) {

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

            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<MessageQuickReply> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = messageQuickReplyService.getAllQuickReplyScreen(page_number, page_size, "","", map);
            }  else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = messageQuickReplyService.getAllQuickReplyScreen(page_number, page_size, corp_code, "",map);
                // list = messageQuickReplyService.getAllQuickReplyScreen(page_number, page_size, "", map,manager_corp);
            }else {
                list = messageQuickReplyService.getAllQuickReplyScreen(page_number, page_size, corp_code, "",map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 消息快捷回复管理
     * 导出数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<MessageQuickReply> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = messageQuickReplyService.getAllQuickReplyByPage(1, Common.EXPORTEXECLCOUNT, "", search_value);
                } else {
                    list = messageQuickReplyService.getAllQuickReplyByPage(1, Common.EXPORTEXECLCOUNT, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = messageQuickReplyService.getAllQuickReplyScreen(1, Common.EXPORTEXECLCOUNT, "", "",map);
                } else {
                    list = messageQuickReplyService.getAllQuickReplyScreen(1, Common.EXPORTEXECLCOUNT, corp_code,"", map);
                }
            }
            List<MessageQuickReply> messageQuickReplies = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(messageQuickReplies);
            if (messageQuickReplies.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json, messageQuickReplies, map, response, request,"快捷回复");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

}
