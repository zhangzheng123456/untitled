package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 店铺业绩目标管理
 * Created by xiaohua on 2016/6/1.
 *
 * @@version
 */
@Controller
@RequestMapping("/storeAchvGoal")
public class StoreAchvGoalController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    @Autowired
    StoreAchvGoalService storeAchvGoalService = null;
    @Autowired
    FunctionService functionService = null;
    @Autowired
    private CorpService corpService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private BaseService baseService;
    String id;

    /**
     * 用户管理
     * 列表展示
     *
     * @param request
     * @return
     */
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String getStoreAchvGoal(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//
//            JSONObject result = new JSONObject();
//            PageInfo<StoreAchvGoal> list = null;
//            if (role_code.contains(Common.ROLE_SYS)) {
//                list = storeAchvGoalService.selectBySearch(page_number, page_size, "", "", "", "","");
//            } else if (role_code.equals(Common.ROLE_BM)) {
//                //品牌管理员
//                String brand_code = request.getSession().getAttribute("brand_code").toString();
//                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
//                String store_code = "";
//                for (int i = 0; i < stores.size(); i++) {
//                    store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
//                }
//                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", store_code, "","");
//            } else if (role_code.equals(Common.ROLE_GM)) {
//                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", "", "","");
//            } else if (role_code.equals(Common.ROLE_AM)) {
//                String area_code = request.getSession().getAttribute("area_code").toString();
//                String area_store_code = request.getSession().getAttribute("store_code").toString();
//                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, area_code, "", "",area_store_code);
//            } else {
//                String store_code = request.getSession().getAttribute("store_code").toString();
//                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", store_code, "","");
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception e) {
//            dataBean.setId("1");
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(e.getMessage());
//            e.printStackTrace();
//            logger.info(e.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 店铺业绩目标修改或添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
//        String corp_code = request.getSession(false).getAttribute("corp_code").toString();

        String id = "";
        String result = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            StoreAchvGoal storeAchvGoal1 = new StoreAchvGoal();
            String corp_code = jsonObject.get("corp_code").toString();
            String store_name = jsonObject.get("store_name").toString();
            String store_code = jsonObject.get("store_code").toString();
            String achv_goal = jsonObject.get("achv_goal").toString();
            String achv_type = jsonObject.get("achv_type").toString();

            storeAchvGoal1.setCorp_code(corp_code);
            storeAchvGoal1.setStore_name(store_name);
            storeAchvGoal1.setStore_code(store_code);
            storeAchvGoal1.setTarget_amount(achv_goal);
            storeAchvGoal1.setTime_type(achv_type);

            Date now = new Date();
            storeAchvGoal1.setModifier(user_id);
            storeAchvGoal1.setModified_date(Common.DATETIME_FORMAT.format(now));
            storeAchvGoal1.setCreater(user_id);
            storeAchvGoal1.setCreated_date(Common.DATETIME_FORMAT.format(now));
            storeAchvGoal1.setIsactive(jsonObject.get("isactive").toString());
            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
//                String time = jsonObject.get("end_time").toString();
//                String week = TimeUtils.getWeek(time);
//                storeAchvGoal1.setTarget_time(week);
//                result = storeAchvGoalService.insert(storeAchvGoal1);
            } else if (achv_type.equals(Common.TIME_TYPE_MONTH)) {
                String isaverage = jsonObject.get("isaverage").toString();
                storeAchvGoal1.setIsaverage(isaverage);
                if (isaverage.equals("Y")) {
                    //平均分配

                    String target_time = jsonObject.get("end_time").toString();
                    String per_amount = jsonObject.get("per_amount").toString();
                    StoreAchvGoal storeAchvGoal2 = storeAchvGoalService.getStoreAchvForID(corp_code, store_code, target_time);
                    if (storeAchvGoal2 != null) {
                        int m = storeAchvGoalService.deleteById(storeAchvGoal2.getId());
                    }

                    List<String> list = TimeUtils.getMonthAllDays(target_time + Common.DATE_FORMAT);
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        String new_time = list.get(i);
                        StoreAchvGoal storeAchvGoal3 = storeAchvGoalService.getStoreAchvForID(corp_code, store_code, new_time);
                        if (storeAchvGoal3 != null) {
                            storeAchvGoal1.setId(storeAchvGoal3.getId());
                            storeAchvGoal1.setTime_type(Common.TIME_TYPE_DAY);
                            storeAchvGoal1.setTarget_time(new_time);
                            storeAchvGoal1.setTarget_amount(per_amount);
                            storeAchvGoal1.setModifier(user_id);
                            storeAchvGoal1.setModified_date(Common.DATETIME_FORMAT.format(now));
                            storeAchvGoal1.setIsactive(jsonObject.get("isactive").toString());
                            String returnInfo = storeAchvGoalService.update(storeAchvGoal1);
                            if (returnInfo.equals(Common.DATABEAN_CODE_SUCCESS)) {
                                result = Common.DATABEAN_CODE_SUCCESS;
                            } else {
                                result = "该日目标已设置过，更新失败";
                            }

                        } else {
                            storeAchvGoal1.setTarget_time(new_time);
                            storeAchvGoal1.setTime_type(Common.TIME_TYPE_DAY);
                            storeAchvGoal1.setTarget_amount(per_amount);
                            storeAchvGoal1.setModifier(user_id);
                            storeAchvGoal1.setModified_date(Common.DATETIME_FORMAT.format(now));
                            storeAchvGoal1.setCreater(user_id);
                            storeAchvGoal1.setCreated_date(Common.DATETIME_FORMAT.format(now));
                            storeAchvGoal1.setIsactive(jsonObject.get("isactive").toString());
                            String resultInfo = storeAchvGoalService.insert(storeAchvGoal1);
                            if (resultInfo.equals(Common.DATABEAN_CODE_SUCCESS)) {
                                result = Common.DATABEAN_CODE_SUCCESS;
                            }
                        }

                    }


                } else {

                    //按比例分配

                    String target_time = jsonObject.get("end_time").toString();
                    String proportion = jsonObject.get("proportion").toString();
                    String targets_arr = jsonObject.get("targets_arr").toString();
                    String arr[] = targets_arr.split(",");

                    StoreAchvGoal storeAchvGoal2 = storeAchvGoalService.getStoreAchvForID(corp_code, store_code, target_time);
                    if (storeAchvGoal2 != null) {
                        int m = storeAchvGoalService.deleteById(storeAchvGoal2.getId());
                    }

                    List<String> list = TimeUtils.getMonthAllDays(target_time + Common.DATE_FORMAT);
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        String new_time = list.get(i);
                        //判断每天是周几  设置对应比例的业绩目标
                        String targets_per = "0";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date time = simpleDateFormat.parse(new_time);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(time);
                        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
                        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//                        if (1 == dayWeek) {
//                            cal.add(Calendar.DAY_OF_MONTH, -1);
//                        }
                        cal.setFirstDayOfWeek(Calendar.SATURDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
                        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
                        if (day == 1) {
                            //周日
                            targets_per = arr[0];

                        } else if (day == 2) {
                            //周一
                            targets_per = arr[1];
                        } else if (day == 3) {
                            //周二
                            targets_per = arr[2];

                        } else if (day == 4) {
                            //周三
                            targets_per = arr[3];

                        } else if (day == 5) {
                            //周四
                            targets_per = arr[4];

                        } else if (day == 6) {
                            //周五
                            targets_per = arr[5];

                        } else if (day == 7) {
                            //周六
                            targets_per = arr[6];

                        }


                        StoreAchvGoal storeAchvGoal3 = storeAchvGoalService.getStoreAchvForID(corp_code, store_code, new_time);
                        if (storeAchvGoal3 != null) {
                            storeAchvGoal1.setId(storeAchvGoal3.getId());
                            storeAchvGoal1.setTime_type(Common.TIME_TYPE_DAY);
                            storeAchvGoal1.setTarget_time(new_time);
                            storeAchvGoal1.setIsaverage(isaverage);

                            // storeAchvGoal1.setTarget_amount(per_amount);
                            storeAchvGoal1.setTarget_amount(targets_per);
                            storeAchvGoal1.setProportion(proportion);
                            storeAchvGoal1.setTargets_arr(targets_arr);
                            storeAchvGoal1.setModifier(user_id);
                            storeAchvGoal1.setModified_date(Common.DATETIME_FORMAT.format(now));
                            storeAchvGoal1.setIsactive(jsonObject.get("isactive").toString());
                            String returnInfo = storeAchvGoalService.update(storeAchvGoal1);
                            if (returnInfo.equals(Common.DATABEAN_CODE_SUCCESS)) {
                                result = Common.DATABEAN_CODE_SUCCESS;
                            } else {
                                result = "该日目标已设置过，更新失败";
                            }

                        } else {
                            storeAchvGoal1.setTarget_time(new_time);
                            storeAchvGoal1.setTime_type(Common.TIME_TYPE_DAY);
                            // storeAchvGoal1.setTarget_amount(per_amount);
                            storeAchvGoal1.setTarget_amount(targets_per);
                            storeAchvGoal1.setModifier(user_id);
                            storeAchvGoal1.setIsaverage(isaverage);
                            storeAchvGoal1.setProportion(proportion);
                            storeAchvGoal1.setTargets_arr(targets_arr);
                            storeAchvGoal1.setModified_date(Common.DATETIME_FORMAT.format(now));
                            storeAchvGoal1.setCreater(user_id);
                            storeAchvGoal1.setIsaverage(isaverage);

                            storeAchvGoal1.setCreated_date(Common.DATETIME_FORMAT.format(now));
                            storeAchvGoal1.setIsactive(jsonObject.get("isactive").toString());
                            String resultInfo = storeAchvGoalService.insert(storeAchvGoal1);
                            if (resultInfo.equals(Common.DATABEAN_CODE_SUCCESS)) {
                                result = Common.DATABEAN_CODE_SUCCESS;
                            }
                        }

                    }

                }

            } else if (achv_type.equals(Common.TIME_TYPE_DAY)) {

                storeAchvGoal1.setTarget_time(jsonObject.get("end_time").toString());

//                int count = -1;
//                count = storeAchvGoalService.getAchExist(storeAchvGoal1.getCorp_code(), storeAchvGoal1.getStore_code(), storeAchvGoal1.getTime_type(), storeAchvGoal1.getTarget_time(), storeAchvGoal1.getIsactive());

                String target_time = jsonObject.get("end_time").toString();
                StoreAchvGoal storeAchvGoal3 = storeAchvGoalService.getStoreAchvForID(corp_code, store_code, target_time);

                if (storeAchvGoal3 != null) {

                    storeAchvGoal1.setId(storeAchvGoal3.getId());


                    storeAchvGoal1.setTarget_time(target_time);
                    storeAchvGoal1.setTarget_amount(achv_goal);
                    storeAchvGoal1.setTime_type(Common.TIME_TYPE_DAY);
                    storeAchvGoal1.setModifier(user_id);
                    storeAchvGoal1.setModified_date(Common.DATETIME_FORMAT.format(now));
                    storeAchvGoal1.setIsactive(jsonObject.get("isactive").toString());
                    String resultInfo = storeAchvGoalService.update(storeAchvGoal1);
                    if (resultInfo.equals(Common.DATABEAN_CODE_SUCCESS)) {
                        result = Common.DATABEAN_CODE_SUCCESS;
                    } else {
                        result = "该日目标已设置，更新失败";
                    }
                } else {
                    String resultInfo = storeAchvGoalService.insert(storeAchvGoal1);
                    if (resultInfo.equals(Common.DATABEAN_CODE_SUCCESS)) {
                        result = Common.DATABEAN_CODE_SUCCESS;
                    }
                }
            }

            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                StoreAchvGoal storeAchvGoal = storeAchvGoalService.getStoreAchvForID(storeAchvGoal1.getCorp_code(), storeAchvGoal1.getStore_code(), storeAchvGoal1.getTarget_time());
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(String.valueOf(storeAchvGoal.getId()));

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
                String function = "业绩管理_店铺业绩目标";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("store_code").toString();
                Store store = storeService.getStoreByCode(t_corp_code, t_code, Common.IS_ACTIVE_Y);
                String t_name = store.getStore_name();
                String remark = action_json.get("end_time").toString() + "(" + action_json.get("achv_type").toString() + ")";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setMessage("店铺" + storeAchvGoal1.getStore_code() + "业绩目标已经设定");
                dataBean.setMessage(result);

            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标
     * 选择业绩
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String editbefore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            int store_id = Integer.parseInt(jsonObject.get("id").toString());
            StoreAchvGoal storeAchvGoal = storeAchvGoalService.selectlById(store_id);
//            JSONObject result = new JSONObject();
//            result.put("storeAchvGoal", storeAchvGoal);
            JSONObject result = new JSONObject();
            result.put("storeAchvGoal", JSON.toJSONString(storeAchvGoal));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(Common.DATABEAN_CODE_ERROR);
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_code");
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);

            StoreAchvGoal storeAchvGoal = new StoreAchvGoal();
            storeAchvGoal.setId(Integer.parseInt(jsonObject.get("id").toString()));
            storeAchvGoal.setCorp_code(jsonObject.get("corp_code").toString());
            storeAchvGoal.setStore_name(jsonObject.get("store_name").toString());
            storeAchvGoal.setStore_code(jsonObject.get("store_code").toString());
            storeAchvGoal.setTarget_amount(jsonObject.get("achv_goal").toString());
            String achv_type = jsonObject.get("achv_type").toString();
//            String isaverage = jsonObject.get("isaverage").toString();
//            String proportion = jsonObject.get("proportion").toString();
//            String targets_arr = jsonObject.get("targets_arr").toString();
//            String arr[] = targets_arr.split(",");
            storeAchvGoal.setTime_type(achv_type);
            String time = jsonObject.get("end_time").toString();
            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
                String week = TimeUtils.getWeek(time);
                storeAchvGoal.setTarget_time(week);
            } else {
                storeAchvGoal.setTarget_time(time);
            }
            Date now = new Date();
            storeAchvGoal.setModifier(user_id);
            storeAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));

            storeAchvGoal.setIsactive(jsonObject.get("isactive").toString());
            String result = storeAchvGoalService.update(storeAchvGoal);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
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
                String function = "业绩管理_店铺业绩目标";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("store_code").toString();
                Store store = storeService.getStoreByCode(t_corp_code, t_code, Common.IS_ACTIVE_Y);
                String t_name = store.getStore_name();
                String remark = action_json.get("end_time").toString() + "(" + action_json.get("achv_type").toString() + ")";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("店铺" + storeAchvGoal.getStore_code() + "的业绩目标已经设定");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标删除
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
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String storeAchvGoal_id = jsonObject.get("id").toString();
            String[] ids = storeAchvGoal_id.split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                StoreAchvGoal storeAchvGoal = storeAchvGoalService.selectlById(Integer.parseInt(ids[i]));
                storeAchvGoalService.deleteById(Integer.parseInt(ids[i]));

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
                String function = "业绩管理_店铺业绩目标";
                String action = Common.ACTION_DEL;
                String t_corp_code = storeAchvGoal.getCorp_code();
                String t_code = storeAchvGoal.getStore_code();
                Store store = storeService.getStoreByCode(t_corp_code, t_code, Common.IS_ACTIVE_Y);
                String t_name = store.getStore_name();
                String remark = storeAchvGoal.getTarget_time() + "(" + storeAchvGoal.getTime_type() + ")";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
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
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<StoreAchvGoal> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = storeAchvGoalService.selectBySearch(page_number, page_size, "", "", "", search_value, "");
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", "", search_value, "");

                //   list = storeAchvGoalService.selectBySearch(page_number, page_size, "", "", "", search_value, "", manager_corp);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", "", search_value, "");
            } else if (role_code.equals(Common.ROLE_BM)) {
                //品牌管理员
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                String store_code = "";
                for (int i = 0; i < stores.size(); i++) {
                    store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                }
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", store_code, search_value, "");
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession().getAttribute("area_code").toString();
                String area_store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, area_code, "", search_value, area_store_code);
            } else {
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code, "", store_code, search_value, "");
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<StoreAchvGoal> list = null;
            if (screen.equals("")) {
                if (role_code.contains(Common.ROLE_SYS)) {
                    list = storeAchvGoalService.selectBySearch(1, Common.EXPORTEXECLCOUNT, "", "", "", search_value, "");
                }else if(role_code.equals(Common.ROLE_CM)){
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>"+manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>"+corp_code);
                    list = storeAchvGoalService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", search_value, "");
                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeAchvGoalService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", search_value, "");
                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                    String store_code = "";
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    list = storeAchvGoalService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, "", store_code, search_value, "");
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String area_store_code = request.getSession(false).getAttribute("store_code").toString();

                    list = storeAchvGoalService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, area_code, "", search_value, area_store_code);
                } else {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = storeAchvGoalService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, "", store_code, search_value, "");
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeAchvGoalService.getAllStoreAchvScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", map, "");
                } else if(role_code.equals(Common.ROLE_CM)){
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>"+manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>"+corp_code);
                    list = storeAchvGoalService.getAllStoreAchvScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", map, "");
                }else if (role_code.equals(Common.ROLE_GM)) {
                    list = storeAchvGoalService.getAllStoreAchvScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", map, "");
                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                    String store_code = "";
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    list = storeAchvGoalService.getAllStoreAchvScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", store_code, map, "");
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    String area_store_code = request.getSession(false).getAttribute("store_code").toString();

                    list = storeAchvGoalService.getAllStoreAchvScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_code, "", map, area_store_code);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeAchvGoalService.getAllStoreAchvScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", store_code, map, "");
                }
            }
            List<StoreAchvGoal> storeAchvGoals = list.getList();
            if (storeAchvGoals.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(storeAchvGoals);
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json, storeAchvGoals, map, response, request, "店铺业绩目标");
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
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /***
     * Execl增加
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String result = "";
        Workbook rwb = null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = 6;//得到所有的列
            int rows = rs.getRows();//得到所有的行
//            int actualRows = LuploadHelper.getRightRows(rs);
//            if(actualRows != rows){
//                if(rows-actualRows==1){
//                    result = "：第"+rows+"行存在空白行,请删除";
//                }else{
//                    result = "：第"+(actualRows+1)+"行至第"+rows+"存在空白行,请删除";
//                }
//                int i = 5 / 0;
//            }
            if (rows < 4) {
                result = "：请从模板第4行开始插入正确数据";
                int i = 5 / 0;
            }
            if (rows > 9999) {
                result = "：数据量过大，导入失败";
                int i = 5 / 0;
            }
            Cell[] column3 = rs.getColumn(0);
            Pattern pattern1 = Pattern.compile("C\\d{5}");
            if (!role_code.equals(Common.ROLE_SYS)) {
                for (int i = 3; i < column3.length; i++) {
                    if (column3[i].getContents().toString().trim().equals("")) {
                        continue;
                    }
                    if (!column3[i].getContents().toString().trim().equals(corp_code)) {
                        result = "：第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                    if (matcher.matches() == false) {
                        result = "：第" + (i + 1) + "行企业编号格式有误";
                        int b = 5 / 0;
                        break;
                    }
                }
            }
            for (int i = 3; i < column3.length; i++) {
                if (column3[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行企业编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Corp corp = corpService.selectByCorpId(0, column3[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (corp == null) {
                    result = "：第" + (i + 1) + "行企业编号不存在";
                    int b = 5 / 0;
                    break;
                }

            }

            Cell[] column2 = rs.getColumn(1);
            for (int i = 3; i < column2.length; i++) {
                if (column2[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Store store = storeService.getStoreByCode(column3[i].getContents().toString().trim(), column2[i].getContents().toString().trim(), "");
                if (store == null) {
                    result = "：第" + (i + 1) + "行店铺编号不存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column1 = rs.getColumn(2);
            Pattern pattern2 = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
            for (int i = 3; i < column1.length; i++) {
                if (column1[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Matcher matcher = pattern2.matcher(column1[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行业绩目标输入有误";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column = rs.getColumn(3);
            for (int i = 3; i < column.length; i++) {
                if (column[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                if (!column[i].getContents().toString().trim().equals("D") && !column[i].getContents().toString().trim().equals("W") && !column[i].getContents().toString().trim().equals("M") && !column[i].getContents().toString().trim().equals("Y")) {
                    result = "：第" + (i + 1) + "行的业绩日期类型缩写有误";
                    int b = 5 / 0;
                    break;
                }
            }
            ArrayList<StoreAchvGoal> storeAchvGoals = new ArrayList<StoreAchvGoal>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    StoreAchvGoal storeAchvGoal = new StoreAchvGoal();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    String store_code = rs.getCell(j++, i).getContents().toString().trim();
                    String target_amount = rs.getCell(j++, i).getContents().toString().trim();
                    String target_type = rs.getCell(j++, i).getContents().toString().trim();
                    String cellTypeForDate = LuploadHelper.getCellTypeForDate(rs.getCell(j++, i), target_type);
                    String isactive = rs.getCell(j++, i).getContents().toString().trim();
                    if (cellCorp.equals("") && store_code.equals("") && target_amount.equals("") && target_type.equals("") && cellTypeForDate.equals("")) {
                        continue;
                    }
                    if (cellCorp.equals("") || store_code.equals("") || target_amount.equals("") || target_type.equals("") || cellTypeForDate.equals("")) {
                        result = "：第" + (i + 1) + "行信息不完整,请参照Execl中对应的批注";
                        int a = 5 / 0;
                    }
                    if (!role_code.equals(Common.ROLE_SYS)) {
                        storeAchvGoal.setCorp_code(corp_code);
                    } else {
                        storeAchvGoal.setCorp_code(cellCorp);
                    }
                    storeAchvGoal.setStore_code(store_code);
                    storeAchvGoal.setTarget_amount(target_amount);
                    storeAchvGoal.setTime_type(target_type);
                    if (target_type.equals(Common.TIME_TYPE_WEEK)) {
                        String week = "";
                        if (cellTypeForDate.equals("格式错误")) {
                            storeAchvGoal.setTarget_time(week);
                        } else {
                            week = TimeUtils.getWeek(cellTypeForDate);
                            storeAchvGoal.setTarget_time(week);
                        }
                        //    String week = TimeUtils.getWeek(cellTypeForDate);
                        //   storeAchvGoal.setTarget_time(week);
                    } else {
                        storeAchvGoal.setTarget_time(cellTypeForDate);
                    }
                    if (isactive.toUpperCase().equals("N")) {
                        storeAchvGoal.setIsactive("N");
                    } else {
                        storeAchvGoal.setIsactive("Y");
                    }
                    storeAchvGoal.setCreater(user_id);
                    Date now = new Date();
                    storeAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    storeAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
                    storeAchvGoal.setModifier(user_id);
                    storeAchvGoals.add(storeAchvGoal);
//                    result = String.valueOf(storeAchvGoalService.insert(storeAchvGoal));
//                    if (result.equals("店铺业绩重复")){
//                        result = "：第"+(i+1)+ "条店铺业绩目标已经设定";
//                        int b = 5 / 0;
//                    }
                }
            }
            for (int i = 0; i < storeAchvGoals.size(); i++) {
                result = String.valueOf(storeAchvGoalService.insert(storeAchvGoals.get(i)));
                if (result.equals("店铺业绩重复")) {
                    result = "：第" + (i + 1) + "条店铺业绩目标已经设定";
                    int b = 5 / 0;
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } finally {
            if (rwb != null) {
                rwb.close();
            }
            System.gc();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObject = JSONObject.parseObject(jsString);
            id = jsonObject.getString("id");
            String message = jsonObject.get("message").toString();
            JSONObject jsonObject1 = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject1.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject1.get("pageSize").toString());
//            String screen = jsonObject.get("screen").toString();
//            JSONObject jsonScreen = new JSONObject(screen);
            Map<String, String> map = new WebUtils().Json2Map(jsonObject1);
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<StoreAchvGoal> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, "", "", "", map, "");
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, corp_code, "", "", map, "");

                // list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, "", "", "", map, "", manager_corp);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, corp_code, "", "", map, "");
            } else if (role_code.equals(Common.ROLE_BM)) {
                //品牌管理员
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                String store_code = "";
                for (int i = 0; i < stores.size(); i++) {
                    store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                }
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, corp_code, "", store_code, map, "");
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession(false).getAttribute("area_code").toString();
                String area_store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, corp_code, area_code, "", map, area_store_code);
            } else {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                list = storeAchvGoalService.getAllStoreAchvScreen(page_number, page_size, corp_code, "", store_code, map, "");
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }
}
