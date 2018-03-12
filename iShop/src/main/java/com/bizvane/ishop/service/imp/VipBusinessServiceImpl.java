package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.service.VipBusinessService;
import org.springframework.stereotype.Service;

/**
 * Created by yanyadong on 2017/7/12.
 */
@Service
public class VipBusinessServiceImpl implements VipBusinessService {

    public JSONObject getSumParam(String type) throws  Exception {

        JSONObject obj=new JSONObject();
        if (type.equals("user")) {

            obj.put("sum_amt_trade",0);
            obj.put("sum_num_sales",0);
            obj.put("sum_num_trade",0);

            obj.put("avg_amt_trade",0);
            obj.put("avg_num_sales",0);
            obj.put("avg_num_trade",0);

            obj.put("avg_trade_price",0);
            obj.put("avg_sales_price",0);
            obj.put("avg_relate_rate",0);

        } else if (type.equals("vip")) {

            obj.put("sum_vip_amt_trade",0);
            obj.put("sum_vip_num_sales",0);
            obj.put("sum_vip_num_trade",0);

            obj.put("avg_vip_amt_trade",0);
            obj.put("avg_vip_num_sales",0);
            obj.put("avg_vip_num_trade",0);

            obj.put("avg_vip_trade_price",0);
            obj.put("avg_vip_sales_price",0);
            obj.put("avg_vip_relate_rate",0);

        }else if(type.equals("wechat")){
            obj.put("sum_new_fans", 0);//新增粉丝
            obj.put("sum_wechat_vip_sum", 0);//微会员
            obj.put("sum_wechat_vip", 0);//新增微会员
            obj.put("sum_wechat_vip_bind", 0);//绑卡微会员

            obj.put("avg_new_fans", 0);//新增粉丝
            obj.put("avg_wechat_vip_sum", 0);//微会员
            obj.put("avg_wechat_vip", 0);//新增微会员
            obj.put("avg_wechat_vip_bind",0);//绑卡微会员
        }

        return  obj;
    }
}
