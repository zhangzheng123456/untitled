package com.bizvane.ishop.controller.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.entity.VipActivityDetail;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.service.TaskService;
import com.bizvane.ishop.service.VipActivityService;
import com.bizvane.ishop.service.VipFsendService;
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
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by nanji on 2016/11/16.
 */
@Controller
@RequestMapping("/vipActivity")
public class VipActivityController {
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    TaskService taskService;
    @Autowired
    VipFsendService vipFsendService;


    private static final Logger logger = Logger.getLogger(VipActivityController.class);

    String id;


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
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");
            String result = "";
            //根据活动编号判断新增活动或者编辑活动
            if (activity_code == null || activity_code.equals("")) {
                result = this.vipActivityService.insert(message, user_id);
                if (result.equals("新增失败")) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                } else if (result.equals("该企业已存在该活动标题")) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                } else {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage(result);
                }
            } else {
                //根据活动编号查询活动的状态，只有未执行的活动才可以编辑活动
                VipActivity vipActivity=vipActivityService.selActivityByCode(activity_code);
                if(vipActivity.getActivity_state().equals("未执行")){
                    result = this.vipActivityService.update(message, user_id);
                    if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setMessage("编辑成功");
                    } else {
                        dataBean.setId(id);
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage(result);
                    }
                }
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
        String user_code = request.getSession(false).getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<VipActivity> list;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = vipActivityService.selectAllActivity(page_number, page_size, "", "", search_value);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = vipActivityService.selectAllActivity(page_number, page_size, corp_code, "", search_value);
            } else {
                list = vipActivityService.selectAllActivity(page_number, page_size, corp_code, user_code, search_value);
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
                VipActivity activityVip = vipActivityService.getActivityById(Integer.valueOf(ids[i]));
                if (activityVip != null) {
                    String activity_state = activityVip.getActivity_state();
                    if (activity_state.equals("执行中")) {
                        msg = "执行中活动，不可删除";
                        break;
                    } else if (activity_state.equals("未执行")) {
                        String task_code = activityVip.getTask_code();
                        String sms_code = activityVip.getSms_code();
                        if (task_code != null && !task_code.equals("")) {
                            Task task = taskService.getTaskForId(activityVip.getCorp_code(), task_code);
                            if (task != null) {
                                //提示之后确定删除即删除对应任务
                                taskService.delTaskByActivityCode(activityVip.getCorp_code(), activityVip.getActivity_code());
                                break;
                            }
                        }
                        if (sms_code != null && !sms_code.equals("")) {

                            vipFsendService.delSendByActivityCode(activityVip.getCorp_code(), activityVip.getActivity_code());
                        }
                    }
                }
            }
            if (msg.equals("")) {
                for (int i = 0; i < ids.length; i++) {
                    vipActivityService.delete(Integer.parseInt(ids[i]));
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipActivity> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipActivityService.selectActivityAllScreen(page_number, page_size, "", "", map);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = vipActivityService.selectActivityAllScreen(page_number, page_size, corp_code, "", map);
            } else {
                list = vipActivityService.selectActivityAllScreen(page_number, page_size, corp_code, user_code, map);
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
    @RequestMapping(value = "/executeActivity", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
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
            String activity_code = jsonObject.get("activity_code").toString();

            VipActivity vipActivity = vipActivityService.selActivityByCode(activity_code);
            String activity_state = vipActivity.getActivity_state();
            if (!activity_state.equals("未执行")) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("该任务非未执行状态");
            } else {
                String result = vipActivityService.executeActivity(vipActivity, user_code);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("execute success");
                } else {
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
     * 获取活动详细信息
     *
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
            String activity_code = jsonObject.getString("activity_code");
            String corp_code = jsonObject.getString("corp_code");
            VipActivity activityVip = this.vipActivityService.selActivityByCode(activity_code);
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
     * 验证会员类型名称的唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/activityThemeExist", method = RequestMethod.POST)
    @ResponseBody
    public String themeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_theme = jsonObject.get("activity_theme").toString().trim();
            String corp_code = jsonObject.get("corp_code").toString();
            VipActivity vipActivity = vipActivityService.getVipActivityByTheme(corp_code, activity_theme, Common.IS_ACTIVE_Y);
            if (vipActivity != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("当前企业下该会员活动标题已存在");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("当前企业下该会员活动标题不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

//    /**
//     * 活动(执行中)
//     * 获取活动执行情况
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/executeDetail", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
//    @ResponseBody
//    @Transactional
//    public String executeDetail(HttpServletRequest request) throws Exception {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = JSONObject.parseObject(message);
//            int activity_id = Integer.parseInt(jsonObject.getString("id"));
//            VipActivity activityVip = vipActivityService.selectActivityById(activity_id);
//            String activity_state = activityVip.getActivity_state();
//            String run_mode = activityVip.getRun_mode();
//            String result = "";
//            if (activity_state.equals("未执行")) {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setMessage("该活动未执行");
//            } else {
//                if (run_mode.contains("任务")) {
//                    JSONObject result_obj = vipActivityService.executeDetail(activityVip);
//                    result = result_obj.toString();
//                }
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setMessage(result);
//            }
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

//    /**
//     * 终止活动
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/changeState", method = RequestMethod.POST)
//    @ResponseBody
//    public String changeState(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = JSONObject.parseObject(message);
//            int activity_id = Integer.parseInt(jsonObject.getString("id"));
//            VipActivity activityVip = this.vipActivityService.selectActivityById(activity_id);
//            if (activityVip != null) {
//                String activity_state = activityVip.getActivity_state();
//                if (activity_state.equals("执行中")) {
//                    activityVip.setActivity_state("已结束");
//                    vipActivityService.updateVipActivity(activityVip);
//                }
//            }
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage("success");
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

//    /**
//     * 获取导购执行详情
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/userExecuteDetail", method = RequestMethod.POST)
//    @ResponseBody
//    public String userExecuteDetail(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = JSONObject.parseObject(message);
//            String corp_code = jsonObject.getString("corp_code");
//            String user_code = jsonObject.getString("user_code");
//            String activity_vip_code = jsonObject.getString("activity_vip_code");
//
//            ArrayList list = vipActivityService.userExecuteDetail(corp_code,activity_vip_code,user_code);
//
//            JSONObject obj = new JSONObject();
//            obj.put("list",list);
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage(obj.toString());
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
}
