package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.entity.VipActivityDetail;
import com.bizvane.ishop.entity.VipCardType;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.service.imp.VipActivityServiceImpl;
import org.apache.avro.data.Json;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nanji on 2017/1/6.
 */
@Controller
@RequestMapping("/vipActivity/detail")
public class VipActivityDetailController {
    @Autowired
    private VipActivityDetailService vipActivityDetailService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    private VipCardTypeService vipCardTypeService;

    private static final Logger logger = Logger.getLogger(VipActivityDetailController.class);

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
            String activity_code = jsonObject.get("activity_code").toString().trim();
            VipActivityDetail vipActivityd = vipActivityDetailService.selActivityDetailByCode(activity_code);
            String result = "";
            //根据活动类型判断新增或者编辑
            if (vipActivityd == null) {
                result = this.vipActivityDetailService.insert(message, user_id);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("新增成功");
                } else {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                }
            } else {
                result = this.vipActivityDetailService.update(message, user_id);
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
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
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
            VipActivityDetail vipActivityDetail = vipActivityDetailService.selActivityDetailByCode(activity_code);
           String  recruit= vipActivityDetail.getRecruit();
            JSONObject recruitInfo= JSON.parseObject(recruit);
            String vip_card_type_code="";
            if(recruitInfo.containsKey("vip_card_type_code")) {
                 vip_card_type_code = recruitInfo.getString("vip_card_type_code");
            }
            JSONObject result = new JSONObject();
            JSONObject vip_cardInfo=JSON.parseObject( JSON.toJSONString(vipActivityDetail));
            //根据会员卡编号查询会员卡类型
            if(vip_card_type_code!=null||!vip_card_type_code.equals("")){
                VipCardType vipCardType= vipCardTypeService.getVipCardTypeByCode(vipActivityDetail.getCorp_code(),vip_card_type_code,Common.IS_ACTIVE_Y);
                String vip_card_type_name=vipCardType.getVip_card_type_name();
                vip_cardInfo.put("vip_card_type_name",vip_card_type_name);
            }
            else{
                System.out.print("======");
            }
            result.put("activityVip",vip_cardInfo );
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
     * 根据活动编号生成二维码
     */
    @RequestMapping(value = "/creatQrcode", method = RequestMethod.POST)
    @ResponseBody
    public String creatQrcode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("------------VipActivity---------- creatQrcode" + jsString);
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String activity_code = jsonObject.get("activity_code").toString();
            String app_id = jsonObject.get("app_id").toString();
            CorpWechat corpWechat = corpService.getCorpByAppId(app_id);
            if (corpWechat != null && corpWechat.getApp_id() != null && corpWechat.getApp_id() != "") {
                String auth_appid = corpWechat.getApp_id();
                String is_authorize = corpWechat.getIs_authorize();
                if (is_authorize.equals("Y")) {
                    String result = vipActivityDetailService.creatActivityInviteQrcode(corp_code, activity_code, auth_appid, user_id);
                    if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                        dataBean.setId(id);
                        dataBean.setMessage(result);
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        return dataBean.getJsonStr();
                    } else if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                        dataBean.setId(id);
                        dataBean.setMessage("生成二维码失败");
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        return dataBean.getJsonStr();
                    } else {
                        dataBean.setId(id);
                        dataBean.setMessage(result);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        return dataBean.getJsonStr();
                    }
                }
            }
            dataBean.setId(id);
            dataBean.setMessage("所属企业未授权");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


}
