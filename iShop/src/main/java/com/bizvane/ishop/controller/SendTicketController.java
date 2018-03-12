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
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *后台发券给导购
 * Created by PC on 2017/10/11.
 */
@Controller
@RequestMapping("/sendTicket")
public class SendTicketController {
    @Autowired
    private BaseService baseService;

    @Autowired
    SendCouponsService sendCouponsService;
    @Autowired
    private CorpService corpService;

    @Autowired
    IceInterfaceService iceInterfaceService;

    String id;
    private static final Logger logger = Logger.getLogger(SendTicketController.class);

    /**
     * 群发消息
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
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
            JSONObject result = new JSONObject();
            PageInfo<SendCoupons> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = sendCouponsService.getAllSendCouponsByPage(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.sendCouponsService.getAllSendCouponsByPage(page_number, page_size, "", search_value);
                } else {
                    list = sendCouponsService.getAllSendCouponsByPage(page_number, page_size, corp_code, search_value);
                }
            }
            //  logger.info("=====list===============" + list.getSize());

            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toJSONString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String add(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String send_coupon_users = jsonObject.get("send_coupon_user").toString().trim();
            String result = this.sendCouponsService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_ERROR)) {
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
                String function = "优惠券配额_新增";
                String action = Common.ACTION_DEL;
                String t_corp_code = operation_corp_code;
                String t_code = send_coupon_users;
                String t_name = "";
                String remark = "后台发券";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("insert error");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("");
            dataBean.setMessage("操作失败");
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/getAppIds", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String getAppIds(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String corp_code = jsonObject.getString("corp_code");

            String brand_code = jsonObject.getString("brand_code");
            JSONArray arr = new JSONArray();
            List<CorpWechat> corpWechats = corpService.selectWByCorpBrand(corp_code, brand_code);
            for (int i = 0; i < corpWechats.size(); i++) {
                JSONObject obj = new JSONObject();
                String auth_appid = corpWechats.get(i).getApp_id();
                String app_name = corpWechats.get(i).getApp_name();
                obj.put("app_id", auth_appid);
                obj.put("app_name", app_name);
                arr.add(obj);
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(arr.toJSONString());

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("");
            dataBean.setMessage("查询错误");
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/getCouponsByAppId", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String getCouponsByAppId(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String corp_code = jsonObject.getString("corp_code");

            String app_id = jsonObject.getString("app_id");
            Map datalist = new HashMap<String, Data>();
            Data data_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_app_id = new Data("app_id", app_id, ValueType.PARAM);

            datalist.put(data_code.key, data_code);
            datalist.put(data_app_id.key, data_app_id);

            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetCoupon", datalist);
            String result = dataBox.data.get("message").value;

            JSONArray array = JSON.parseArray(result);
//            logger.info("=======array_new==========="+array.size());
            JSONArray array_new = new JSONArray();
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                //有效天数
                if (obj.containsKey("effective_days")) {
                    obj.put("effective_days", obj.getString("effective_days"));
                } else {
                    obj.put("effective_days", "");
                }

                //面额
                if (obj.containsKey("parvalue")) {
                    obj.put("parvalue", obj.getString("parvalue"));
                } else {
                    obj.put("parvalue", "");
                }
                //券编号
                if (obj.containsKey("couponcode")) {
                    obj.put("couponcode", obj.getString("couponcode"));
                } else {
                    obj.put("couponcode", "");
                }
                //券名称
                if (obj.containsKey("name")) {
                    obj.put("name", obj.getString("name"));
                } else {
                    obj.put("name", "");
                }

                //券详情 富文本
                if (obj.containsKey("quan_details")) {
                    obj.put("quan_details", obj.getString("quan_details"));
                } else {
                    obj.put("quan_details", "");
                }
                // 开始时间
                if (obj.containsKey("start_time")) {
                    obj.put("start_time", obj.getString("start_time"));
                } else {
                    obj.put("start_time", "");
                }
                // 结束时间
                if (obj.containsKey("end_time")) {
                    obj.put("end_time", obj.getString("end_time"));
                } else {
                    obj.put("end_time", "");
                }
                //最低消费
                if (obj.containsKey("minimumcharge")) {
                    obj.put("minimumcharge", obj.getString("minimumcharge"));
                } else {
                    obj.put("minimumcharge", "");
                }
                if (obj.containsKey("iscrm_verify")) {
                    obj.put("iscrm_verify", obj.getString("iscrm_verify"));
                } else {
                    obj.put("iscrm_verify", "");
                }
                if (obj.containsKey("is_list_limit")) {
                    obj.put("is_list_limit", obj.getString("is_list_limit"));
                } else {
                    obj.put("is_list_limit", "");
                }
                if (obj.containsKey("use_type")) {
                    obj.put("use_type", obj.getString("use_type"));
                } else {
                    obj.put("use_type", "");
                }
                if (obj.containsKey("send_type")) {
                    obj.put("send_type", obj.getString("send_type"));
                } else {
                    obj.put("send_type", "");
                }


                if (obj.containsKey("shortname")) {
                    obj.put("shortname", obj.getString("shortname"));
                } else {
                    obj.put("shortname", "");
                }
                //券类型
                if (obj.containsKey("coupontype")) {
                    if (obj.getString("coupontype").equals("1")) {
                        obj.put("coupontype", obj.getString("coupontype"));
                        array_new.add(obj);
                    }

                }
//                logger.info("=======array_new==========="+array_new.size());
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(array_new.toJSONString());

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("");
            dataBean.setMessage("查询错误");
        }
        return dataBean.getJsonStr();
    }


    /**
     * 删除
     * 删除mysql
     * mongodb关联到人的记录全部删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_fsend_id = jsonObject.get("id").toString();
            String[] ids = vip_fsend_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                sendCouponsService.delete(Integer.valueOf(ids[i]), user_id);

            }
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
            String function = "优惠券配额_删除";
            String action = Common.ACTION_DEL;
            String t_corp_code = operation_corp_code;
            String t_code = vip_fsend_id;
            String t_name = "";
            String remark = "删除促销发券";
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
            //-------------------行为日志结束-----------------------------------------------------------------------------------

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");


        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }

        return dataBean.getJsonStr();
    }


    /**
     * 编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String edit(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            JSONObject jsonObject = JSONObject.parseObject(message);
            String info = sendCouponsService.updateSendCoupons(message, user_id);
            if (info.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("edit error");
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
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<SendCoupons> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = sendCouponsService.getAllSendCouponsScreen(page_number, page_size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = sendCouponsService.getAllSendCouponsScreen(page_number, page_size, corp_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }

        return dataBean.getJsonStr();
    }


    /**
     * 根据id查看详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String select(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_brand_code = request.getSession().getAttribute("brand_code").toString();
        String user_area_code = request.getSession().getAttribute("area_code").toString();
        String user_store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String data = null;
        try {
            String jsString = request.getParameter("param");

            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String ticket_id = jsonObject.get("id").toString().trim();

            SendCoupons info = sendCouponsService.getSendCouponsInfoById(Integer.valueOf(ticket_id));
            //  logger.info("========info============" + info.getCouponInfo() + "=====");
            data = JSON.toJSONString(info);

            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            e.printStackTrace();
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.toString());
        }

        return bean.getJsonStr();
    }


    @RequestMapping(value = "/userlist", method = RequestMethod.POST)
    @ResponseBody
    public String selectUserlists(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject object = new JSONObject();
        try {
            PageInfo<String> list = null;
            String role_code = request.getSession().getAttribute("role_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String ids = jsonObject.get("id").toString();

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            String search_value = jsonObject.get("search_value").toString();
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
           // logger.info("===================================================================");
            list = sendCouponsService.getInfo(page_number, page_size, ids, search_value);
            //logger.info("=======================================================list============" + list);
            JSONObject result = new JSONObject();

            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
}
