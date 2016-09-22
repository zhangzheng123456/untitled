package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/vip")
public class VIPController {

    private static final Logger logger = Logger.getLogger(VIPController.class);

    String id;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipAlbumService vipAlbumService;
    @Autowired
    VipLabelService vipLabelService;

    /**
     * 会员列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String VIPManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject obj = new JSONObject();

            JSONArray array = new JSONArray();
            JSONObject vip = new JSONObject();
            vip.put("corp_code","C10000");
            vip.put("store_id","1570");
            vip.put("user_id","无");
            vip.put("vip_id","774205");
            vip.put("vip_avatar","");
            vip.put("vip_name","罗晓珊");
            vip.put("vip_phone","15915655912");
            vip.put("vip_card_type","直营合作会员卡");
            vip.put("amount","1000");
            vip.put("consume_times","5");
            vip.put("join_date","2016-04-11");
            vip.put("cardno","4444444444444444444");
            vip.put("vip_birthday","2016-04-11");
            array.add(vip);

            obj.put("pages",10);
            obj.put("pageSize",10);
            obj.put("pageNum",1);
            obj.put("all_vip_list",array);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息
     * 近一年消费和累计消费的接口+相册+标签
     */
    @RequestMapping(value = "/vipConsumCount", method = RequestMethod.POST)
    @ResponseBody
    public String vipConsumCount(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
//            String store_code = jsonObject.get("store_id").toString();

            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_corp_code.key, data_corp_code);

            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.AnalysisVipMonetary", datalist);
            String result = dataBox.data.get("message").value;
            logger.info("----vip_id: "+vip_id+"---vipConsumCount:" + dataBox.data.get("message").value);

            List<VipAlbum> vipAlbumList = vipAlbumService.selectAlbumByVip(corp_code,vip_id);
            List<VipLabel> vipLabelList = vipLabelService.selectLabelByVip(corp_code,vip_id);

            JSONObject obj = new JSONObject();
            obj.put("Consum",result);
            obj.put("Album",JSON.toJSONString(vipAlbumList));
            obj.put("Label",JSON.toJSONString(vipLabelList));

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员信息
     * 会员详细资料
     */
    @RequestMapping(value = "/vipInfo", method = RequestMethod.POST)
    @ResponseBody
    public String vipInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            JSONObject vip = new JSONObject();
            vip.put("corp_code","C10000");
            vip.put("store_id","1570");
            vip.put("user_id","无");
            vip.put("vip_id","774205");
            vip.put("vip_avatar","");
            vip.put("vip_name","罗晓珊");
            vip.put("vip_phone","15915655912");
            vip.put("vip_card_type","直营合作会员卡");
//            vip.put("amount","1000");
            vip.put("consume_times","5");
            vip.put("join_date","2016-04-11");
            vip.put("cardno","4444444444444444444");
            vip.put("vip_birthday","2016-04-11");
            vip.put("age","23");
            vip.put("sex","female");
            vip.put("user_name","10000");
            vip.put("total_amount","2003");
            vip.put("dormant_time","2016-04-11");
            vip.put("store_name","第三家");
            vip.put("store_code","ABC02");
//            vip.put("vip_card_no","774205");

            JSONArray extend = new JSONArray();
            JSONArray info = new JSONArray();

            JSONObject extend_job = new JSONObject();
            extend_job.put("name","职业");
            extend_job.put("key","working");
            extend_job.put("type",Common.DATE_SHOW_TYPE_TEXT);
            extend_job.put("is_must","Y");
            extend.add(extend_job);
            JSONObject extend_mail = new JSONObject();
            extend_mail.put("name","邮件");
            extend_mail.put("key","mail");
            extend_mail.put("type",Common.DATE_SHOW_TYPE_TEXT);
            extend_mail.put("is_must","Y");
            extend.add(extend_mail);
            JSONObject extend_birth = new JSONObject();
            extend_birth.put("name","生日");
            extend_birth.put("key","birthday");
            extend_birth.put("type",Common.DATE_SHOW_TYPE_DATE);
            extend_birth.put("is_must","Y");
            extend.add(extend_birth);
            JSONObject extend_idno = new JSONObject();
            extend_idno.put("name","身份证号码");
            extend_idno.put("key","idno");
            extend_idno.put("type",Common.DATE_SHOW_TYPE_TEXT);
            extend_idno.put("is_must","Y");
            extend.add(extend_idno);
            JSONObject extend_height = new JSONObject();
            extend_height.put("name","身高");
            extend_height.put("key","height");
            extend_height.put("type",Common.DATE_SHOW_TYPE_TEXT);
            extend_height.put("is_must","Y");
            extend.add(extend_height);
            JSONObject extend_bust = new JSONObject();
            extend_bust.put("name","胸围(cm)");
            extend_bust.put("key","bust");
            extend_bust.put("type",Common.DATE_SHOW_TYPE_TEXT);
            extend_bust.put("is_must","Y");
            extend.add(extend_bust);
            JSONObject extend_waist = new JSONObject();
            extend_waist.put("name","腰围(cm)");
            extend_waist.put("key","waist");
            extend_waist.put("type",Common.DATE_SHOW_TYPE_TEXT);
            extend_waist.put("is_must","Y");
            extend.add(extend_waist);
            JSONObject extend_hip = new JSONObject();
            extend_hip.put("name","臀围(cm)");
            extend_hip.put("key","hip");
            extend_hip.put("type",Common.DATE_SHOW_TYPE_TEXT);
            extend_hip.put("is_must","Y");
            extend.add(extend_hip);
            JSONObject extend_constellation = new JSONObject();
            extend_constellation.put("name","星座");
            extend_constellation.put("key","constellation");
            extend_constellation.put("type",Common.DATE_SHOW_TYPE_SELECT);
            extend_constellation.put("values", CommonValue.VALUE_CONSTELLATION);
            extend_constellation.put("is_must","Y");
            extend.add(extend_constellation);
            JSONObject extend_address = new JSONObject();
            extend_address.put("name","通讯地址");
            extend_address.put("key","address");
            extend_address.put("type",Common.DATE_SHOW_TYPE_LONGTEXT);
            extend_address.put("is_must","Y");
            extend.add(extend_address);

            JSONObject extend_info_job = new JSONObject();
            extend_info_job.put("key","working");
            extend_info_job.put("value","攻城狮");
            info.add(extend_info_job);
            JSONObject extend_info_mail = new JSONObject();
            extend_info_mail.put("key","mail");
            extend_info_mail.put("value","123@2.com");
            info.add(extend_info_mail);
            JSONObject extend_info_birth = new JSONObject();
            extend_info_birth.put("key","birthday");
            extend_info_birth.put("value","1993-07-07");
            info.add(extend_info_birth);
            JSONObject extend_info_height = new JSONObject();
            extend_info_height.put("key","height");
            extend_info_height.put("value","170");
            info.add(extend_info_height);
            JSONObject extend_info_constellation = new JSONObject();
            extend_info_constellation.put("key","constellation");
            extend_info_constellation.put("value","处女座");
            info.add(extend_info_constellation);
            JSONObject extend_info_address = new JSONObject();
            extend_info_address.put("key","address");
            extend_info_address.put("value","江苏省南京市软件大道118号");
            info.add(extend_info_address);
            JSONObject extend_info_idno = new JSONObject();
            extend_info_idno.put("key","idno");
            extend_info_idno.put("value","320811198810102245");
            info.add(extend_info_idno);
            JSONObject extend_info_bust = new JSONObject();
            extend_info_bust.put("key","bust");
            extend_info_bust.put("value","80");
            info.add(extend_info_bust);
            JSONObject extend_info_waist = new JSONObject();
            extend_info_waist.put("key","waist");
            extend_info_waist.put("value","65");
            info.add(extend_info_waist);
            JSONObject extend_info_hip = new JSONObject();
            extend_info_hip.put("key","hip");
            extend_info_hip.put("value","90");
            info.add(extend_info_hip);

            org.json.JSONObject result = new org.json.JSONObject();
            result.put("list",vip);
            result.put("extend",extend);
            result.put("extend_info",info);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息
     * 会员积分记录
     */
    @RequestMapping(value = "/vipPoints", method = RequestMethod.POST)
    @ResponseBody
    public String vipPoints(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();


            JSONArray pointLists = new JSONArray();
            for (int i = 0; i < 18; i++) {
                JSONObject record1 = new JSONObject();
                record1.put("points",i+10);
                record1.put("date","2016-01-02");
                pointLists.add(record1);
            }
            JSONObject result_points = new JSONObject();
            result_points.put("list",JSON.toJSONString(pointLists));
            result_points.put("vip_card_type","直营合作会员卡");
            result_points.put("cardno","4444444444444444444");
            result_points.put("vip_name","罗晓珊");
            result_points.put("points","1022");

/*
            JSONArray consumeLists = new JSONArray();
            for (int i = 0; i < 18; i++) {
                JSONObject record1 = new JSONObject();
                record1.put("points",i+10);
                record1.put("date","2016-01-02");
                pointLists.add(record1);
            }
            JSONObject result_points = new JSONObject();
            result_points.put("list",JSON.toJSONString(pointLists));
            result_points.put("vip_card_type","直营合作会员卡");
            result_points.put("cardno","4444444444444444444");
            result_points.put("vip_name","罗晓珊");
            result_points.put("points","1022");
*/

            JSONObject wardrobe = new JSONObject();
            wardrobe.put("goods_id", "367A0103");
            wardrobe.put("goods_name", "无");
            wardrobe.put("goods_img", "null");
            wardrobe.put("goods_price", "889");
            wardrobe.put("goods_num", "3");

            JSONObject wardrobe2 = new JSONObject();
            wardrobe2.put("goods_id", "26632146");
            wardrobe2.put("goods_name", "无");
            wardrobe2.put("goods_img", "null");
            wardrobe2.put("goods_price", "1050");
            wardrobe2.put("goods_num", "5");

            JSONArray war=new JSONArray();
            war.add(wardrobe);
            war.add(wardrobe2);
            JSONObject orders = new JSONObject();
            orders.put("buy_time","2016-07-07");
            orders.put("order_no","1665467899992");
            orders.put("order_discount","9.5折");
            orders.put("order_count","2件商品");
            orders.put("order_total","￥1939");
            orders.put("emp_name","陆之昂");

            JSONArray wardrobes=new JSONArray();
            wardrobes.add(orders);
            wardrobes.add(war);
            JSONObject result = new JSONObject();
            result.put("result_points",result_points);
            result.put("result_consumn",wardrobe);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    //会员积分列表
    @RequestMapping(value = "/allVipPointsRecord", method = RequestMethod.POST)
    @ResponseBody
    public String allVipPointsRecord(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Map datalist = iceInterfaceService.vipBasicMethod(jsonObject,request);
            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.VipDetailQuery", datalist);
            logger.info("-------会员积分" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
