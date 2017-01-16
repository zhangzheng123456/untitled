package com.bizvane.ishop.service;

import com.bizvane.ishop.service.imp.CRMInterfaceServiceImpl;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by yanyadong on 2017/1/13.
 */
public class Test02 {

    public static  void main(String[] args){


//JSONArray类型可传入数组
////
//////
//
//        com.alibaba.fastjson.JSONObject json
   HashMap<String,Object> map=new HashMap<String, Object>();
////////////        //查询
//           map.put("table","12899");
//        map.put("columns",new String[]{"C_STORE_ID","id","VIPNAME"});
//        // map.put("range",1);
//        JSONObject expr1 = new JSONObject();
//        HashMap<String,Object> map1=new HashMap<String, Object>();
//        expr1.put("column", "VIPNAME");
//        expr1.put("condition", "test123");
//        map.put("params",expr1);
//Object=new com.alibaba.fastjson.JSONObject(map);

    //    System.out.println(map.toString());
//
//        CRMInterfaceServiceImpl crmInterfaceService=new CRMInterfaceServiceImpl();
//        String ij= crmInterfaceService.selVip(1059167);
//        System.out.println(ij);

//        //新增
//           map.put("VIPNAME", "test123000");
//        map.put("C_VIPTYPE_ID__NAME", "玖姿卡");
//       map.put("C_CUSTOMER_ID__NAME", "上海尹默公司");
//        map.put("C_STORE_ID__NAME","大庆大商新玛特购物休闲广场尹默专柜");
//        map.put("SEX", "W");
//        map.put("BIRTHDAY","19950824");
//        map.put("MOBIL","13291090901");
//        map.put("table","18690");
//       map.put("SALESREP_ID__NAME","付梦");
//      //  map.put("DOCNOS",2324);
//        JSONObject jsonObject=new JSONObject(map);
//
//        CRMInterfaceServiceImpl crmInterfaceService=new CRMInterfaceServiceImpl();
//        String info=crmInterfaceService.addVip("C10016",map);
//        System.out.println(info);

//
//        //修改
//       map.put("VIPNAME", "test123");
//       map.put("C_CUSTOMER_ID__NAME", "上海尹默公司");
//        map.put("C_STORE_ID__NAME","上海久光百货尹默专柜");
//        //只能为男女 如果传入的类型或者值不匹配 虽显示修改成功  但实际不会变化
      map.put("SEX", "女");
//        map.put("C_STORE_ID",802);
//        map.put("PASS_WORD","123rrr");
 //       map.put("INTEGRAL_PASSWORD","456ttt");
        //必传
      //  map.put("SALESREP_ID__NAME","付梦");
     //   map.put("partial_update", true);
       // map.put("STATUS",1);
        map.put("id",1059167);

        JSONObject jsonObject=new JSONObject(map);
        System.out.println(jsonObject);
        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
        String infp=crmInterfaceService.modInfoVip("C10016",map);
        System.out.println(infp);


    //获取一个vip的所有信息
//        map.put("table","12899");
//        map.put("id",22522);

    //    CRMInterfaceServiceImpl;




        //webaction

//[[8116,"贺月梅"],[672998,"贺月梅"],[9706,"贺月梅"]]}]
      //  map.put("webaction","1059167");
//        map.put("id",8116);
//        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//        crmInterfaceService.couponInfo(9706);


        //删除一个vip
//        map.put("table","12899");
//        map.put("id",8124);
//        JSONObject jsonObject=new JSONObject(map);
//        System.out.println(jsonObject);
//        CRMInterfaceServiceImpl.delObject(map);

    }
}
