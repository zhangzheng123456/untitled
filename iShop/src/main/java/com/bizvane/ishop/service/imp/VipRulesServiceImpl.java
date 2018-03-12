package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.VipRulesMapper;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.VipRules;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.http.HttpClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/12/19.
 */
@Service
public class VipRulesServiceImpl implements VipRulesService {

    @Autowired
    VipRulesMapper vipRulesMapper;
    @Autowired
    VipCardTypeService vipCardTypeService;
    @Autowired
    CorpService corpService;
    @Autowired
    StoreService storeService;
    @Autowired
    IceInterfaceService iceInterfaceService;

    @Override
    public VipRules getVipRulesById(int id) throws Exception {
        VipRules rules = vipRulesMapper.selectById(id);
        if (rules != null) {
            String present_coupon = rules.getPresent_coupon();
            String store_codes = rules.getStore_code();
            String corp_code = rules.getCorp_code();
            List<Store> stores = storeService.selectStore(corp_code, store_codes);
            JSONArray store_array = new JSONArray();
            for (int i = 0; i < stores.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("store_code", stores.get(i).getStore_code());
                obj.put("store_name", stores.get(i).getStore_name());
                store_array.add(obj);
            }
            rules.setStores(store_array);
            String app_id = rules.getApp_id();
            CorpWechat corpWechat = corpService.getCorpByAppId(corp_code,app_id);
            String app_name = "";
            if (corpWechat != null)
                app_name = corpWechat.getApp_name();
            rules.setApp_name(app_name);
//            if (present_coupon != null && !present_coupon.equals("") && !present_coupon.equals("[]")) {
//                JSONArray coupons = JSONArray.parseArray(present_coupon);
//                String coupon_info = iceInterfaceService.getCouponInfo(rules.getCorp_code(),app_id);
//                if (!coupon_info.equals(Common.DATABEAN_CODE_ERROR)){
//                    JSONArray array = JSONArray.parseArray(coupon_info);
//                    String coupon_name = "";
//                    for (int i = 0; i < coupons.size(); i++) {
//                        JSONObject obj = coupons.getJSONObject(i);
//                        String coupon_code = obj.getString("couponcode");
//                        for (int j = 0; j < array.size(); j++) {
//                            JSONObject coupon = array.getJSONObject(j);
//                            if (coupon_code.equals(coupon.getString("couponcode"))) {
//                                coupon_name = coupon.getString("name");
//                            }
//                            obj.put("appname", app_name);
//                            obj.put("name", coupon_name);
//                        }
//                    }
//                }
//                rules.setPresent_coupon(coupons.toJSONString());
//            }
        }
        return rules;
    }

    @Override
    public PageInfo<VipRules> getAllVipRulesByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<VipRules> vipRules;
        PageHelper.startPage(page_number, page_size);
        vipRules = vipRulesMapper.selectAllVipRules(corp_code, search_value);
        for (VipRules vipRules1 : vipRules) {
            vipRules1.setIsactive(CheckUtils.CheckIsactive(vipRules1.getIsactive()));

        }
        PageInfo<VipRules> page = new PageInfo<VipRules>(vipRules);

        return page;
    }

    @Override
    public List<VipRules> getViprulesList(String corp_code, String isactive) throws Exception {
        return vipRulesMapper.selectByCorp(corp_code, isactive);
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String present_coupon = jsonObject.get("present_coupon").toString().trim();
        VipRules vipRules = WebUtils.JSON2Bean(jsonObject, VipRules.class);
       // List<VipRules> vipRules1 = this.getVipRulesByType(vipRules.getCorp_code(), vipRules.getVip_type(),null, vipRules.getIsactive());
          List<VipRules> vipRules1 = this.selectByVipCardTypeCode(vipRules.getCorp_code(),vipRules.getVip_card_type_code(),null,null);
        int num = 0;
        if (vipRules1.size()>0) {
                status = "该企业已存在该会员类型";
        } else {
            String upgrade_amount = vipRules.getUpgrade_amount();
            if (upgrade_amount.equals("")) {
                vipRules.setUpgrade_time("");
            }
            String valid_date=vipRules.getValid_date();
            if(valid_date.equals("")||valid_date.equals("0")){
                vipRules.setValid_date("");
            }
            vipRules.setCorp_code(corp_code);
            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipRules.setPresent_coupon(present_coupon);
            vipRules.setCreater(user_id);
            vipRules.setModifier(user_id);
            vipRules.setCreated_date(Common.DATETIME_FORMAT.format(now));
            num = vipRulesMapper.insertVipRules(vipRules);
            if (num > 0) {
               // List<VipRules> vipRules2 = this.getVipRulesByType(vipRules.getCorp_code(), vipRules.getVip_type(), vipRules.getHigh_vip_type(), vipRules.getIsactive());
                  List<VipRules> vipRules2 = this.selectByVipCardTypeCode(vipRules.getCorp_code(),vipRules.getVip_card_type_code(),vipRules.getHigh_vip_card_type_code(),null);
                status = String.valueOf(vipRules2.get(0).getId());
                return status;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        }
        return status;
    }

    @Override
    public int insert(VipRules vipRules) throws Exception {
        int vale = vipRulesMapper.insertVipRules(vipRules);
        return vale;
    }


    @Override
    public String update(String message, String user_id) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        JSONObject jsonObject = JSONObject.parseObject(message);

        int id = Integer.parseInt(jsonObject.get("id").toString());
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String discount = jsonObject.get("discount").toString().trim();
        String join_threshold = jsonObject.get("join_threshold").toString().trim();
        String upgrade_time = jsonObject.get("upgrade_time").toString().trim();
        String upgrade_amount = jsonObject.get("upgrade_amount").toString().trim();
        String points_value = jsonObject.get("points_value").toString().trim();
        String present_point = jsonObject.get("present_point").toString().trim();
        String present_coupon = jsonObject.get("present_coupon").toString().trim();
        String vip_card_type_code = jsonObject.get("vip_card_type_code").toString().trim();
        String degree = jsonObject.get("degree").toString().trim();
        String high_vip_card_type_code = jsonObject.get("high_vip_card_type_code").toString().trim();
        String high_degree = jsonObject.get("high_degree").toString().trim();
        String isactive = jsonObject.get("isactive").toString().trim();
        String store_code = jsonObject.get("store_code").toString().trim();
        String vip_type = jsonObject.get("vip_type").toString().trim();
        String high_vip_type = jsonObject.get("high_vip_type").toString().trim();
        String app_id = jsonObject.get("app_id").toString().trim();
        //更新新增加的字段
        String valid_date = jsonObject.get("valid_date").toString().trim();
        String keep_grade_condition = jsonObject.get("keep_grade_condition").toString().trim();
        String keep_present_coupon = jsonObject.get("keep_present_coupon").toString().trim();
        String keep_present_point = jsonObject.get("keep_present_point").toString().trim();
        String degrade_vip_code = jsonObject.get("degrade_vip_code").toString().trim();
        String degrade_degree = jsonObject.get("degrade_degree").toString().trim();
        String degrade_vip_name = jsonObject.get("degrade_vip_name").toString().trim();

        VipRules vipRules= vipRulesMapper.selectById(id);

//        if(!vipRules.getVip_type().equals(vip_type)||!vipRules.getCorp_code().equals(corp_code)){
//            List<VipRules> vipRules3 = this.getVipRulesByType(corp_code,vip_type,null, isactive);
//            if(vipRules3.size()>0){
//                status = "该企业会员类型已存在";
//                return  status;
//            }
//            List<VipRules> vipRules1 = this.getVipRulesByType(corp_code, vip_type, high_vip_type, Common.IS_ACTIVE_Y);
//            List<VipRules> vipRules2=vipRulesMapper.selectDegradeByVipType(corp_code,vip_type,degrade_vip_name,Common.IS_ACTIVE_Y);
//             if(vipRules1.size()>0) {
//                 status = "该企业会员类型对应的高级会员类型已存在";
//                 return  status;
//             }else if(vipRules2.size()>0){
//                 status = "该企业会员类型对应的下级会员类型已存在";
//                 return  status;
//             }
//       }
        if(!vipRules.getVip_card_type_code().equals(vip_card_type_code)||!vipRules.getCorp_code().equals(corp_code)) {
           // List<VipRules> vipRules3 = this.getVipRulesByType(corp_code, vip_type, null, isactive);
              List<VipRules> vipRules3 = this.selectByVipCardTypeCode(corp_code,vip_card_type_code,null,null);
            if (vipRules3.size() > 0) {
                status = "该企业会员类型已存在";
                return status;
            }
        }

            Date now = new Date();
            if (upgrade_amount.equals("")) {
                vipRules.setUpgrade_time("");
            }
            vipRules.setCorp_code(corp_code);
            vipRules.setVip_type(vip_type);
            vipRules.setHigh_vip_type(high_vip_type);
            vipRules.setDiscount(discount);
            vipRules.setJoin_threshold(join_threshold);
            vipRules.setUpgrade_amount(upgrade_amount);
            vipRules.setPoints_value(points_value);
            vipRules.setStore_code(store_code);
            vipRules.setPresent_coupon(present_coupon);
            vipRules.setPresent_point(present_point);
            vipRules.setModifier(user_id);
            vipRules.setUpgrade_time(upgrade_time);
            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipRules.setIsactive(isactive);
            vipRules.setVip_card_type_code(vip_card_type_code);
            vipRules.setDegree(degree);
            vipRules.setHigh_vip_card_type_code(high_vip_card_type_code);
            vipRules.setHigh_degree(high_degree);
            vipRules.setApp_id(app_id);

            vipRules.setValid_date(valid_date);
            vipRules.setKeep_grade_condition(keep_grade_condition);
            vipRules.setKeep_present_coupon(keep_present_coupon);
            vipRules.setKeep_present_point(keep_present_point);
            vipRules.setDegrade_degree(degrade_degree);
            vipRules.setDegrade_vip_code(degrade_vip_code);
            vipRules.setDegrade_vip_name(degrade_vip_name);

            int num = vipRulesMapper.updateVipRules(vipRules);
            if (num > 0) {
                return status;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }


//        List<VipRules> vipRules1 = this.getVipRulesByType(corp_code, vip_type, high_vip_type, Common.IS_ACTIVE_Y);
//        VipRules vipRules = getVipRulesById(id);
//        if (vipRules1.size()>0 || vipRules1.get(0).getId() == id) {
//            Date now = new Date();
//            if (upgrade_amount.equals("")) {
//                vipRules.setUpgrade_time("");
//            }
//            vipRules.setCorp_code(corp_code);
//            vipRules.setVip_type(vip_type);
//            vipRules.setHigh_vip_type(high_vip_type);
//            vipRules.setDiscount(discount);
//            vipRules.setJoin_threshold(join_threshold);
//            vipRules.setUpgrade_amount(upgrade_amount);
//            vipRules.setPoints_value(points_value);
//            vipRules.setStore_code(store_code);
//            vipRules.setPresent_coupon(present_coupon);
//            vipRules.setPresent_point(present_point);
//            vipRules.setModifier(user_id);
//            vipRules.setUpgrade_time(upgrade_time);
//            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
//            vipRules.setIsactive(isactive);
//            vipRules.setVip_card_type_code(vip_card_type_code);
//            vipRules.setDegree(degree);
//            vipRules.setHigh_vip_card_type_code(high_vip_card_type_code);
//            vipRules.setHigh_degree(high_degree);
//            vipRules.setApp_id(app_id);
//
//            vipRules.setValid_date(valid_date);
//            vipRules.setKeep_grade_condition(keep_grade_condition);
//            vipRules.setKeep_present_coupon(keep_present_coupon);
//            vipRules.setKeep_present_point(keep_present_point);
//            vipRules.setDegrade_degree(degrade_degree);
//            vipRules.setDegrade_vip_code(degrade_vip_code);
//            vipRules.setDegrade_vip_name(degrade_vip_name);
//
//            int num = vipRulesMapper.updateVipRules(vipRules);
//            if (num > 0) {
//                return status;
//            } else {
//                status = Common.DATABEAN_CODE_ERROR;
//            }
//        } else {
//            status = "该企业会员类型对应的高级会员类型已存在";
//
//        }
        return status;
    }

    @Override
    public int delete(int id) throws Exception {
        return vipRulesMapper.deleteById(id);
    }

    @Override
    public PageInfo<VipRules> getAllVipRulesScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<VipRules> list1 = vipRulesMapper.selectVipRulesScreen(params);
        for (VipRules vipRules1 : list1) {
            vipRules1.setIsactive(CheckUtils.CheckIsactive(vipRules1.getIsactive()));
        }
        PageInfo<VipRules> page = new PageInfo<VipRules>(list1);
        return page;
    }

    @Override
    public List<VipRules> getVipRulesByType(String corp_code, String vip_type, String high_vip_type, String isactive) throws Exception {
        return vipRulesMapper.selectByVipType(corp_code, vip_type, high_vip_type, isactive);
    }

    @Override
    public List<VipRules> getViprulesByCardTypeCode(String corp_code, String vip_card_type_code) throws Exception {
        return vipRulesMapper.selectByCardTypeCode(corp_code,vip_card_type_code);
    }

    @Override
    public List<VipRules> selectByCardHighCode(String corp_code, String high_vip_card_type_code) throws Exception {
        return vipRulesMapper.selectByCardHighCode(corp_code,high_vip_card_type_code);
    }

    @Override
    public int deleteActivity(String activity_code) throws Exception {
        return vipRulesMapper.deleteByActivity(activity_code);
    }


    public JSONArray getAllCoupon(String appid,String access_key) throws Exception {

        String time = System.currentTimeMillis()+"";
        JSONObject threeobj = new JSONObject();
        threeobj.put("method", "com.bizvane.sun.wx.method.GetJSONArray");
        threeobj.put("sign", (CheckUtils.encryptMD5Hash(appid + time + access_key)).toUpperCase());
        threeobj.put("customcode", appid);
        threeobj.put("ts", time);

        JSONObject twoobj = new JSONObject();
        twoobj.put("condition", "isactive==Y");
        twoobj.put("signinfo", threeobj);
        twoobj.put("columns", "quan_code,name,use_type,start_time,end_time");
        twoobj.put("ltconfig", "wx_coupon");
        twoobj.put("pagesize", 1000);
        twoobj.put("pagenow", 0);

        JSONObject oneobj = new JSONObject();
        oneobj.put("data", twoobj);

        System.out.println( oneobj.toJSONString());
        String url = CommonValue.baiyou_url;
        RequestBody body = RequestBody.create(Common.JSON, oneobj.toJSONString());
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = new HttpClient().post(request);
        String result = response.body().string();

        JSONObject result_obj = JSONObject.parseObject(result);
        JSONArray array = new JSONArray();
        if (result_obj.containsKey("code") && result_obj.get("code").toString().equals("0")){
            if (result_obj.getString("data") != null && !result_obj.getString("data").equals("")){
                JSONObject data = JSONObject.parseObject(result_obj.getString("data"));
                array = data.getJSONArray("list");
            }
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            obj.put("couponcode",obj.getString("quan_code"));
        }
        return array;
    }

    @Override
    public List<VipRules> selectByVipCardTypeCode(String corp_code, String vip_card_type_code, String high_vip_card_type_code, String isactive) throws Exception {
        List<VipRules> vipRules1=vipRulesMapper.selectByVipCardTypeCode(corp_code,vip_card_type_code,high_vip_card_type_code,isactive);
        return vipRules1;
    }

}
