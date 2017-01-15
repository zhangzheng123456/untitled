package com.bizvane.ishop.service;

import com.bizvane.ishop.service.imp.CRMInterfaceServiceImpl;

import java.util.HashMap;

/**
 * Created by yanyadong on 2017/1/13.
 */
public class Test02 {

    public static  void main(String[] args){


//JSONArray类型可传入数组


            HashMap<String,Object> map=new HashMap<String, Object>();
//        //查询
            map.put("table","18690");
        map.put("columns",new String[]{"id"});


  CRMInterfaceServiceImpl.selectvip(map);


//        //新增
//        map.put("VIPNAME", "你好hahaaha");
//        map.put("SALESREP_ID__NAME", "付梦");
//        map.put("C_VIPTYPE_ID__NAME", "玖姿卡");
//        map.put("C_CUSTOMER_ID__NAME", "沈阳萱姿服饰有限公司");
//        map.put("C_STORE_ID__NAME","辽宁沈阳千盛商场直营");
//        map.put("SEX", "男");
//        map.put("BIRTHDAY","19950824");
//        map.put("MOBIL","13211111111");
//        map.put("table","18690");
//        JSONObject jsonObject=new JSONObject(map);
//        System.out.println(jsonObject);
//        CRMInterfaceServiceImpl.addvip(map);

  /*      //修改
        map.put("VIPNAME", "test123");
       map.put("C_CUSTOMER_ID__NAME", "上海尹默公司");
        //只能为男女 如果传入的类型或者值不匹配 虽显示修改成功  但实际不会变化
      map.put("SEX", "男");
        map.put("C_STORE_ID","802");
        //必传
        map.put("table","12899");
        map.put("partial_update", true);
        map.put("id","1059167");

        JSONObject jsonObject=new JSONObject(map);
        System.out.println(jsonObject);
        CRMInterfaceServiceImpl.modifyvip(map);
*/

    //获取一个vip的所有信息
//        map.put("table","12899");
//        map.put("id",1059167);
//
//        CRMInterfaceServiceImpl.getObject(map);




        //webaction

//
//        map.put("webaction","1059167");
//        map.put("id","1059167");
//        CRMInterfaceServiceImpl.excuteWebaction(map);

    }
}
