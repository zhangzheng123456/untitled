package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
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
            vip.put("amount","1000");
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
            vip.put("vip_card_no","774205");
            
            org.json.JSONObject result = new org.json.JSONObject();
            result.put("list", JSON.toJSONString(vip));
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


    //会员积分
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
