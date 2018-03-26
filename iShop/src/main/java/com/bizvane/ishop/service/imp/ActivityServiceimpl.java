package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.dao.VipActivityMapper;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.service.ActivityService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service

public class ActivityServiceimpl implements ActivityService{
    @Autowired
    private VipActivityMapper vipActive;


    @Override
    public PageInfo<VipActivity> selectAllActivity(int page_number,int page_size,String search_value) {
        PageHelper.startPage(page_number,page_size);
        PageInfo<VipActivity> acts=null;
        List<VipActivity> vipactivi=null;
        try {
            vipactivi = vipActive.exploreExcel(search_value);

            for (VipActivity act : vipactivi) {

                if(act.getRun_scope()!=null&&!"".equals(act.getRun_scope())){
                    act.setIsactive(CheckUtils.CheckIsactive(act.getIsactive()));
                    JSONObject jsonobj=JSONObject.parseObject(act.getRun_scope());
                    act.setRunscope_areacode(jsonobj.get("area_code").toString());
                    act.setRunscope_branecode(jsonobj.getString("brand_code"));
                    act.setRunscope_storecode(jsonobj.getString("store_code"));


                }
                //0：未执行，1：执行中，2：已结束（初始值为0，步骤4点击完成后变为1）
                if(act.getActivity_state()!=null&&!"".equals(act.getActivity_state())){
                    String numstate=act.getActivity_state();
                    if("0".equals(numstate)){
                        act.setActivity_state("未执行");
                    }

                    if("1".equals(numstate)){
                        act.setActivity_state("执行中");
                    }
                    if("2".equals(numstate)){
                        act.setActivity_state("已结束");
                    }
                }

                if(act.getCoupon_type()!=null&&!"".equals(act.getCoupon_type())){
                    act.setIsactive(CheckUtils.CheckIsactive(act.getIsactive()));
                    JSONArray jsonobj= JSONArray.parseArray(act.getCoupon_type());
                    String name="";
                    for(Object jsono : jsonobj){
                        JSONObject json=(JSONObject)jsono;
                        if(!"".equals(json.getString("coupon_name"))&&json!=null) {
                            name += json.getString("coupon_name") + ",";
                        }
                    }
                    if(name.length()>=1){
                        name=name.substring(0,name.length()-1);
                        act.setCoupon_name(name);
                    }


                }
                if(act.getSend_points()!=null&&!"".equals(act.getSend_points())){
                    act.setPresent_point(act.getSend_points());
                }

                //发券类型
                if(act.getSend_coupon_type()!=null&&!"".equals(act.getSend_coupon_type())){
                    if(act.getSend_coupon_type().equalsIgnoreCase("batch")){act.setSend_coupon_type("批量发券");
                    }
                    if(act.getSend_coupon_type().equalsIgnoreCase("card")){ act.setSend_coupon_type("开卡送券");}
                    if(act.getSend_coupon_type().equalsIgnoreCase("anniversary")){act.setSend_coupon_type("纪念日发券");}
                    if(act.getSend_coupon_type().equalsIgnoreCase("consume")){act.setSend_coupon_type("消费后送券");}


                }
                //活动类别(recruit招募,h5,sales促销,coupon优惠券,invite报名,festival节日,register邀请注册)

                if(act.getRun_scope()!=null&&!"".equals(act.getRun_mode())){
                    if(act.getSend_coupon_type()!=null&&!"".equals(act.getSend_coupon_type())){
                        act.setRun_mode(CheckUtils.CheckVipActivityType(act.getRun_mode()).equals("优惠券活动")?CheckUtils.CheckVipActivityType(act.getRun_mode())+"-"+act.getSend_coupon_type():CheckUtils.CheckVipActivityType(act.getRun_mode()));
                    }else{
                        act.setRun_mode(CheckUtils.CheckVipActivityType(act.getRun_mode()));
                    }


                }


            }
            acts = new PageInfo<VipActivity>(vipactivi);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return acts;
    }

    @Override
    public PageInfo<VipActivity> selectAllCorpScreen(int page_num, int page_size, String corp_code, String user_code, Map<String, String> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("user_code", user_code);

        if (map.containsKey("created_date")) {
            JSONObject date = JSONObject.parseObject(map.get("created_date"));
            params.put("created_date_start", date.get("start").toString());
            String created_date_end = date.get("end").toString();
            if (!created_date_end.equals(""))
                created_date_end = created_date_end + " 23:59:59";
            params.put("created_date_end", created_date_end);
        }
        if (map.containsKey("start_time")) {
            JSONObject start_time = JSONObject.parseObject(map.get("start_time"));
            params.put("start_time_start", start_time.get("start").toString());
            String start_time_end = start_time.get("end").toString();
            if (!start_time_end.equals(""))
                start_time_end = start_time_end + " 23:59:59";
            params.put("start_time_end", start_time_end);

        }
        if (map.containsKey("end_time")) {
            JSONObject end_time = JSONObject.parseObject(map.get("end_time"));
            params.put("end_time_start", end_time.get("start").toString());
            String end_time_end = end_time.get("end").toString();
            if (!end_time_end.equals(""))
                end_time_end = end_time_end + " 23:59:59";
            params.put("end_time_end", end_time_end);
        }
        map.remove("created_date");
        map.remove("start_time");
        map.remove("end_time");

        for(String key:map.keySet()){
            if (key.equals("activity_state")){
                map.put("activity_state",map.get("activity_state").toString().replace("'",""));
            }
        }
        params.put("map", map);

        PageHelper.startPage(page_num, page_size);
        List<VipActivity> list1 = null;
        try {
            list1 = vipActive.selectAllCorpScreen(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (VipActivity vipActivity : list1) {
            vipActivity.setIsactive(CheckUtils.CheckIsactive(vipActivity.getIsactive()));
            vipActivity.setRun_mode(CheckUtils.CheckVipActivityType(vipActivity.getRun_mode()));
        }
        for (VipActivity act : list1) {

            if(act.getRun_scope()!=null&&!"".equals(act.getRun_scope())){
                act.setIsactive(CheckUtils.CheckIsactive(act.getIsactive()));
                JSONObject jsonobj=JSONObject.parseObject(act.getRun_scope());
                act.setRunscope_areacode(jsonobj.get("area_code").toString());
                act.setRunscope_branecode(jsonobj.getString("brand_code"));
                act.setRunscope_storecode(jsonobj.getString("store_code"));


            }
            if(act.getCoupon_type()!=null&&!"".equals(act.getCoupon_type())){
                act.setIsactive(CheckUtils.CheckIsactive(act.getIsactive()));
                JSONArray jsonobj= JSONArray.parseArray(act.getCoupon_type());
                String name="";
                for(Object jsono : jsonobj){
                    JSONObject json=(JSONObject)jsono;
                    if(!"".equals(json.getString("coupon_name"))&&json!=null) {
                        name += json.getString("coupon_name") + ",";
                    }
                }
                if(name.length()>=1){
                    name=name.substring(0,name.length()-1);
                    act.setCoupon_name(name);
                }


            }
            if(act.getSend_points()!=null&&!"".equals(act.getSend_points())){
                act.setPresent_point(act.getSend_points());
            }
            //活动状态
            //0：未执行，1：执行中，2：已结束（初始值为0，步骤4点击完成后变为1）
            if(act.getActivity_state()!=null&&!"".equals(act.getActivity_state())){
                String numstate=act.getActivity_state();
                if("0".equals(numstate)){
                    act.setActivity_state("未执行");
                }

                if("1".equals(numstate)){
                    act.setActivity_state("执行中");
                }
                if("2".equals(numstate)){
                    act.setActivity_state("已结束");
                }
            }


        }
        PageInfo<VipActivity> page = new PageInfo<VipActivity>(list1);
        return page;




    }
}
