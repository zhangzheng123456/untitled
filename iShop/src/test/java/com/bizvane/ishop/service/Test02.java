package com.bizvane.ishop.service;

import com.bizvane.ishop.service.imp.CRMInterfaceServiceImpl;

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
        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
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
//       CRMInterfaceServiceImpl crmInterfaceService=new CRMInterfaceServiceImpl();
    //   String ij= crmInterfaceService.selVip("C10016",1059167);
//////
//////        JSONObject jsonObject=new JSONObject(ij);
//////        String  s=jsonObject.get("rows").toString();
    //   System.out.println(ij);

//        //新增
//           map.put("VIPNAME", "test123000");
//        map.put("C_VIPTYPE_ID__NAME", "玖姿卡");
//       map.put("C_CUSTOMER_ID__NAME", "上海尹默公司");
//        map.put("C_STORE_ID__NAME","大庆大商新玛特购物休闲广场尹默专柜");
//        map.put("SEX", "女");
//        map.put("BIRTHDAY","19950824");
//        map.put("MOBIL","13492050901");
//        map.put("table","18690");
//       map.put("SALESREP_ID__NAME","付梦");
//      //  map.put("DOCNOS",2324);
// //       map.put("id",1060501);
// //       JSONObject jsonObject=new JSONObject(map);
////
//        CRMInterfaceServiceImpl crmInterfaceService=new CRMInterfaceServiceImpl();
//        String info=crmInterfaceService.addRefund("C10016",map);
//        System.out.println(info);


        //addPrepaidDocuments
//
//          map.put("BILLDATE","20171230");
////         map.put("RECHARGE_TYPE","VM");
//   map.put("RECHARGE_TYPE","余额退款");
//        map.put("C_VIPMONEY_STORE_ID__NAME","上海茂名南路安正男装专柜");
//        map.put("C_VIP_ID__CARDNO","IW13270801449");
//        crmInterfaceService.addRefund("C10016",map);
////        map.put("ORGDOCNO","VM0000000003");
//        map.put("SALESREP_ID__NAME","宋怡");
// //      map.put("TOT_AMT_ACTUAL","-3000");
// //       map.put("DOCNO","VM1701170000046");
//       // map.put("PASS_WORD","1234")
//////        map.put("AMOUNT_ACTUAL","2999");
//////        map.put("ACTIVE_CONTENT","啦啦啦我是卖报的小画家");
////////
//////////       // map.put("")
//////////
////////        CRMInterfaceServiceImpl crmInterfaceService=new CRMInterfaceServiceImpl();
//       String info=crmInterfaceService.addRefund("C10016",map);
//        System.out.println(info);

//        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//       String info =crmInterfaceService.confPrepaidOrder("C10016","VM1701170000034");
//
//    System.out.println(info);

        //INTEGRAL_PASSWORD

//        map.put("name","modfiy_integral_password");
//
//        //modfiy_password
//
////        JSONArray jsonArray=new JSONArray();
////        jsonArray.put("1324");
////        jsonArray.put("8115");
//        map.put("values",new String[]{"1234","225788"});
//       String info= Rest.excuteSql("C10016",map);
//        System.out.println(info);


//
//        //修改
//      map.put("VIPNAME", "test123000");
////       map.put("C_CUSTOMER_ID__NAME", "上海尹默公司");
////        map.put("C_STORE_ID__NAME","上海久光百货尹默专柜");
////        //只能为男女 如果传入的类型或者值不匹配 虽显示修改成功  但实际不会变化
   //   map.put("SEX", "女");
////        map.put("C_STORE_ID",802);
////        map.put("PASS_WORD","7711");
////        map.put("INTEGRAL_PASSWORD",9482);
////       // 必传
////        map.put("SALESREP_ID__NAME","付梦");
////        map.put("partial_update", true);
////       map.put("MOBIL","13270809989");
 //      map.put("id",1060501);
//  // map.put("AU_STATE","N/A");
//   //     map.put("RECHARGE_TYPE",":余额退款");
//        JSONObject jsonObject=new JSONObject(map);
//        System.out.println(jsonObject);
//        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//        String infp=crmInterfaceService.modInfoVip("C10016",map);
//       System.out.println(infp);


    //获取一个vip的所有信息
//        map.put("table","12899");
//        map.put("id",22522);

    //    CRMInterfaceServiceImpl;




        //webaction

//[[8116,"贺月梅"],[672998,"贺月梅"],[9706,"贺月梅"]]}]
      //  map.put("webaction","1059167");
//        map.put("id",8116);
//        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//        crmInterfaceService.couponInfo("C10016",11154);


        //删除一个vip
//        map.put("table","12899");
//        map.put("id",8124);
//        JSONObject jsonObject=new JSONObject(map);
//        System.out.println(jsonObject);
//        CRMInterfaceServiceImpl.delObject(map);
//        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//          String info= crmInterfaceService.cancleRefundBill("C10016",8088);
//        System.out.println(info);

//
//        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//        crmInterfaceService.submitRefundBill("C10016",8123);



//        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//        String s=crmInterfaceService.confRefundBalance("C10016","VM1701170000024");
//        System.out.println(s);



        //修改integerpassword

//        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//        map.put("PASS_WORD",8181);
//        map.put("id",225788);
//        String info=crmInterfaceService.modfiy_passwordVip("C10016",map);
//        System.out.println(info);


        //修改password
//
//        CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//        String info=crmInterfaceService.modfiy_passwordVip("C10016",8888,225788);
//        System.out.println(info);

        //余额

      //  CRMInterfaceService crmInterfaceService=new CRMInterfaceServiceImpl();
//        String info="";
//        for(int i=0;i<2;i++){
//         info =crmInterfaceService.getBalance("C10016","8148");
//
//        }
//        System.out.println(info);

        //充值单
//        HashMap<String,Object> map1=new HashMap<String, Object>();
//        map1.put("DOCNO","VM1302270000020");
//        String s=crmInterfaceService.getPrepaidOrder("C10016",map1);
//        System.out.println(s);

        //退款
        map.put("DOCNO","VM1701160000003");
        String s=crmInterfaceService.getRefundOrder("C10016",map);
        System.out.println(s);


//       map.put("bill_date","1111");
//        for(String key:map.keySet()){
//            if(key.equals("bill_date")){
//                map.put("Date",map.get(key).toString());
//                map.remove(key);
//            }
//        }
//        JSONObject jsonObject=new JSONObject(map);
//        System.out.println(jsonObject.toString());





    }
}
