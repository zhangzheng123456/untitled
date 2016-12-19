package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipRulesMapper;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.VipRules;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.VipRulesService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.http.HttpClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.avro.data.Json;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
    private static HttpClient httpClient = new HttpClient();
    private static final Logger logger = Logger.getLogger(VipRulesServiceImpl.class);


    @Override
    public VipRules getVipRulesById(int id) throws Exception {
        return vipRulesMapper.selectById(id);
    }

    @Override
    public PageInfo<VipRules> getAllVipRulesByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<VipRules> vipRules;
        PageHelper.startPage(page_number, page_size);
        vipRules = vipRulesMapper.selectAllVipRules(corp_code, search_value);
        for (VipRules vipRules1 : vipRules) {
            vipRules1.setIsactive (CheckUtils.CheckIsactive(vipRules1.getIsactive()));

        }
        PageInfo<VipRules> page = new PageInfo<VipRules>(vipRules);

        return page;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String present_coupon = jsonObject.get("present_coupon").toString().trim();

        VipRules vipRules = WebUtils.JSON2Bean(jsonObject, VipRules.class);
        VipRules vipRules1=this.getVipRulesByType(vipRules.getCorp_code(),vipRules.getVip_type());

        String reult=getCouponInfo(corp_code).toJSONString();
        int num=0;
        if(vipRules1!=null){
            status="该企业已存在该会员类型";
        }else{
            vipRules.setCorp_code(corp_code);
            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipRules.setPresent_coupon(present_coupon);
            vipRules.setCreater(user_id);
            vipRules.setModifier(user_id);
            vipRules.setIsactive(Common.IS_ACTIVE_Y);
            vipRules.setCreated_date(Common.DATETIME_FORMAT.format(now));
            num=vipRulesMapper.insertVipRules(vipRules);
            if(num>0){
                VipRules vipRules2=this.getVipRulesByType(vipRules.getCorp_code(),vipRules.getVip_type());
                status=String.valueOf(vipRules2.getId());
                System.out.print(String.valueOf(vipRules2.getId()));
                return  status;

            }else{
                status=Common.DATABEAN_CODE_ERROR;
            }

        }


        return status;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
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

        VipRules vipRules1=this.getVipRulesByType(corp_code,vip_type);
        VipRules vipRules=getVipRulesById(id);

            if(vipRules1==null||vipRules.getVip_type().equals(vip_type)){
                Date now = new Date();
                vipRules.setCorp_code(corp_code);
                vipRules.setVip_type(vip_type);
                vipRules.setHigh_vip_type(high_vip_type);
                vipRules.setDiscount(discount);
                vipRules.setJoin_threshold(join_threshold);
                vipRules.setUpgrade_time(upgrade_time);
                vipRules.setUpgrade_amount(upgrade_amount);
                vipRules.setPoints_value(points_value);
                vipRules.setPresent_coupon(present_coupon);
                vipRules.setPresent_point(present_point);
                vipRules.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipRules.setCreater(user_id);
                vipRules.setModifier(user_id);
                vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipRules.setIsactive(Common.IS_ACTIVE_Y);
                int num=vipRulesMapper.updateVipRules(vipRules);
                if(num>0){
                    return status;
                }else{
                    status=Common.DATABEAN_CODE_ERROR;
                }
            }else{
                status="该企业已存在该会员类型";
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
    public VipRules getVipRulesByType(String corp_code, String vip_type) throws Exception {
        return vipRulesMapper.selectByVipType(corp_code,vip_type);
    }

    @Override
    public List<VipRules> selectVipRules(String corp_code, String vip_types) throws SQLException {
        return null;
    }
    public  String getCouponType(JSONObject extras) throws Exception {

        RequestBody body = RequestBody.create(Common.JSON, extras.toJSONString());
        Request request = new Request.Builder().url(Common.COUPON_TYPE_URL).post(body).build();
        Response response = httpClient.post(request);
        String result = response.body().string();
        return result;
    }

    public JSONObject   getCouponInfo(String corp_code)throws Exception{
        List<CorpWechat> corpWechats= corpService.getWByCorp(corp_code);
        JSONObject coupon=new JSONObject();
        String timestemp=System.currentTimeMillis()+"";//时间戳
        JSONObject param=new JSONObject();//业务参数
        String sign="";//签名方式MD5(appid+ts+secretkey)
        String appid="";//公众号
        String secretkey="sf0001";//secretkey为密钥，圆周率，会员通、erp三方一致，测试（sf0001）
        String method="o2ocoupontype";//业务方法
        String str="";
        coupon.put("ts",timestemp);
        coupon.put("method",method);
        coupon.put("params",param);
        String appname="";
        for (int i = 0; i < corpWechats.size(); i++) {
             appid = corpWechats.get(i).getApp_id();
            coupon.put("appid",appid);
             str=appid+timestemp+secretkey;
             sign=CheckUtils.encryptMD5Hash(str);//MD%加密
             coupon.put("sign",sign);
            appname=appname+corpWechats.get(i).getApp_name()+",";
        }
        String result=getCouponType(coupon);
        JSONObject  info= JSON.parseObject(result);
        String appnames[]=appname.split(",");
        for (int i = 0; i <appnames.length ; i++) {
            info.put("appname",appnames[i]);
        }

        return info;
    }
}
