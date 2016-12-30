package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipRulesMapper;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.VipRules;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.VipRulesService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

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
    CorpService corpService;
    @Autowired
    StoreService storeService;

    @Override
    public VipRules getVipRulesById(int id) throws Exception {
        VipRules rules = vipRulesMapper.selectById(id);
        if (rules != null){
            String present_coupon = rules.getPresent_coupon();
            String store_codes = rules.getStore_code();
            String corp_code = rules.getCorp_code();
            List<Store> stores = storeService.selectStore(corp_code,store_codes);
            JSONArray store_array = new JSONArray();
            for (int i = 0; i < stores.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("store_code",stores.get(i).getStore_code());
                obj.put("store_name",stores.get(i).getStore_name());
                store_array.add(obj);
            }
            rules.setStores(store_array);
            if (present_coupon != null && !present_coupon.equals("") && !present_coupon.equals("[]") ){
                JSONArray coupons = JSONArray.parseArray(present_coupon);
                String coupon_info = getCouponInfo1(rules.getCorp_code());
                if (!coupon_info.equals(Common.DATABEAN_CODE_ERROR)){
                    JSONArray array = JSONArray.parseArray(coupon_info);
                    String app_name = "";
                    String coupon_name = "";
                    for (int i = 0; i <coupons.size() ; i++) {
                        JSONObject obj = coupons.getJSONObject(i);
                        String app_id = obj.getString("appid");
                        String coupon_code = obj.getString("couponcode");
                        for (int j = 0; j < array.size(); j++) {
                            JSONObject coupon = array.getJSONObject(j);
                            if (app_id.equals(coupon.getString("appid")) && coupon_code.equals(coupon.getString("couponcode"))){
                                app_name = coupon.getString("appname");
                                coupon_name = coupon.getString("name");
                                obj.put("appname",app_name);
                                obj.put("name",coupon_name);
                            }
                        }
                    }
                }
                rules.setPresent_coupon(coupons.toJSONString());
            }
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
    public String insert(String message, String user_id) throws Exception {
        String status = "";
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String present_coupon = jsonObject.get("present_coupon").toString().trim();

        VipRules vipRules = WebUtils.JSON2Bean(jsonObject, VipRules.class);
        VipRules vipRules1 = this.getVipRulesByType(vipRules.getCorp_code(), vipRules.getVip_type(), vipRules.getIsactive());
        int num = 0;
        if (vipRules1 != null) {
            status = "该企业已存在该会员类型";
        } else {
            String upgrade_amount = vipRules.getUpgrade_amount();
            if (upgrade_amount.equals("")) {
                vipRules.setUpgrade_time("");
            }
            vipRules.setCorp_code(corp_code);
            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipRules.setPresent_coupon(present_coupon);
            vipRules.setCreater(user_id);
            vipRules.setModifier(user_id);
            vipRules.setCreated_date(Common.DATETIME_FORMAT.format(now));
            num = vipRulesMapper.insertVipRules(vipRules);
            if (num > 0) {
                VipRules vipRules2 = this.getVipRulesByType(vipRules.getCorp_code(), vipRules.getVip_type(), vipRules.getIsactive());
                status = String.valueOf(vipRules2.getId());
                System.out.print(String.valueOf(vipRules2.getId()));
                return status;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        }
        return status;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        JSONObject jsonObject = JSONObject.parseObject(message);

        int id = Integer.parseInt(jsonObject.get("id").toString());
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String vip_type = jsonObject.get("vip_type").toString().trim();
        String high_vip_type = jsonObject.get("high_vip_type").toString().trim();
        String discount = jsonObject.get("discount").toString().trim();
        String join_threshold = jsonObject.get("join_threshold").toString().trim();
        String upgrade_time = jsonObject.get("upgrade_time").toString().trim();
        String upgrade_amount = jsonObject.get("upgrade_amount").toString().trim();
        String points_value = jsonObject.get("points_value").toString().trim();
        String present_point = jsonObject.get("present_point").toString().trim();
        String present_coupon = jsonObject.get("present_coupon").toString().trim();
        String vip_card_type_code = jsonObject.get("vip_card_type_code").toString().trim();
        String degree = jsonObject.get("degree").toString().trim();
        String isactive = jsonObject.get("isactive").toString().trim();
        String store_code = jsonObject.get("store_code").toString().trim();

        VipRules vipRules1 = this.getVipRulesByType(corp_code, vip_type, Common.IS_ACTIVE_Y);
        VipRules vipRules = getVipRulesById(id);

        if (vipRules1 == null || vipRules1.getId() == id) {
            Date now = new Date();
            if (upgrade_amount.equals("")){
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
            vipRules.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipRules.setCreater(user_id);
            vipRules.setModifier(user_id);
            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipRules.setIsactive(isactive);
            vipRules.setVip_card_type_code(vip_card_type_code);
            vipRules.setDegree(degree);
            int num = vipRulesMapper.updateVipRules(vipRules);
            if (num > 0) {
                return status;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        } else {
            status = "该企业已存在该会员类型";
        }


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
    public VipRules getVipRulesByType(String corp_code, String vip_type, String isactive) throws Exception {
        return vipRulesMapper.selectByVipType(corp_code, vip_type, isactive);
    }

    public String getCouponInfo(String corp_code) throws Exception {
        List<CorpWechat> corpWechats = corpService.getWByCorp(corp_code);
        JSONObject coupon = new JSONObject();
        String timestemp = System.currentTimeMillis() + "";//时间戳
        JSONObject param = new JSONObject();//业务参数
        String sign = "";//签名方式MD5(appid+ts+secretkey)
        String appid = "";//公众号
        String secretkey = "sf0001";//secretkey为密钥，圆周率，会员通、erp三方一致，测试（sf0001）
        String method = "o2ocoupontype";//业务方法
        String str = "";
        coupon.put("ts", timestemp);
        coupon.put("method", method);
        coupon.put("params", param);
        String appname = "";
        JSONObject info = null;
        JSONArray array = new JSONArray();
        for (int i = 0; i < corpWechats.size(); i++) {
            appid = corpWechats.get(i).getApp_id();
            coupon.put("appid", appid);
            str = appid + timestemp + secretkey;
            sign = CheckUtils.encryptMD5Hash(str);//MD5加密
            coupon.put("sig", sign);
            appname = corpWechats.get(i).getApp_name();
            //post请求获取券类型接口
            String couponInfo = IshowHttpClient.post(Common.COUPON_TYPE_URL, coupon);
            info = JSON.parseObject(couponInfo);

            if (info.get("code").equals("0")) {
                JSONArray result = info.getJSONArray("result");
                for (int j = 0; j < result.size(); j++) {
                    JSONObject obj = result.getJSONObject(i);
                    obj.put("appname", appname);
                    obj.put("appid", appid);
                    array.add(obj);
                }
            } else if (info.get("code").toString().equals("-1")) {
                return Common.DATABEAN_CODE_ERROR;
            }
        }
        return array.toJSONString();
    }

    public String getCouponInfo1(String corp_code) throws Exception {

        JSONObject coupon = new JSONObject();
        JSONObject param = new JSONObject();//业务参数
        //String secretkey = "sf0001";//secretkey为密钥，圆周率，会员通、erp三方一致，测试（sf0001）
        coupon.put("ts", "1482129612509");
        coupon.put("method", "o2ocoupontype");
        coupon.put("params",param );
        coupon.put("appid", "wxc9c9111020955324");
        coupon.put("sig", "4E733E69DC02AA26DC21D938A4A4CA5E");

        //post请求获取券类型接口
        String couponInfo = IshowHttpClient.post(Common.COUPON_TYPE_URL, coupon);
        JSONObject info = JSON.parseObject(couponInfo);
        JSONArray result = info.getJSONArray("result");
        JSONArray array = new JSONArray();
        for (int j = 0; j < result.size(); j++) {
            JSONObject obj = result.getJSONObject(j);
            obj.put("appname", "爱秀");
            obj.put("appid", "wxc9c9111020955324");
            array.add(obj);
        }
        return array.toJSONString();
    }


}
