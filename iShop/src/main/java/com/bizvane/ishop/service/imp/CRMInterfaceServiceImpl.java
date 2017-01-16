package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.network.drpapi.burgeon.Rest;
import com.bizvane.ishop.service.CRMInterfaceService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by yanyadong on 2016/1/13.
 */
public class CRMInterfaceServiceImpl  implements CRMInterfaceService{



    //增加会员(C_VIP_IMP)

    /**
     *
     * @param vipInfo
     * @return
     * 新增字段
     * VIPNAME,SALESREP_ID__NAME,C_VIPTYPE_ID__NAME,DOCNOS,VIPCARDNO__CARDNO,MOBIL,ADDRESS,SEX,BIRTHDAY
     */

     public  String  addVip(HashMap<String,Object> vipInfo) {

         if (vipInfo.get("VIPNAME") == null){

             return "缺少VIPNAME";
         }
         if(vipInfo.get("SALESREP_ID__NAME")==null){
             return "缺少SALESREP_ID__NAME";
         }
         if(vipInfo.get("C_VIPTYPE_ID__NAME")==null){
             return  "缺少C_VIPTYPE_ID__NAME";

         }
         if(vipInfo.get("DOCNOS")==null){
             return  "缺少DOCNOS";

         }
//         if(vipInfo.get("VIPCARDNO__CARDNO")==null){
//             return  "缺少VIPCARDNO__CARDNO";
//
//         }
         if(vipInfo.get("MOBIL")==null){
             return  "缺少MOBIL";

         }
//         if(vipInfo.get("ADDRESS")==null){
//
//             return  "缺少ADDRESS";
//         }
         if(vipInfo.get("SEX")==null){

             return  "缺少SEX";
         }
         if(vipInfo.get("BIRTHDAY")==null){
             return  "缺少BIRTHDAY";

         }

         String  info=Rest.Add("C_VIP_IMP", vipInfo);

         System.out.println("info...."+info);

         JSONArray jsonArray=new JSONArray(info);
          int code= (Integer)jsonArray.getJSONObject(0).get("code");
         if(code==-1){
             return  info;
         }

         int id=(Integer) jsonArray.getJSONObject(0).get("objectid");

          String vipinfo=selVip(id);

         System.out.println(vipinfo);


         return vipinfo;
     }

    /**
     *
     * @param modVip
     * 修改字段
     * @return  VIPNAME,C_CUSTOMER_ID__NAME,SEX,SALESREP_ID__NAME,SALEHREMP_ID__NAME
     */

     //修改信息（C_VIP）
     public  String  modInfoVip(HashMap<String,Object> modVip){

         if(modVip.get("VIPNAME")==null){
             return "缺少VIPNAME";
         }
         if(modVip.get("C_CUSTOMER_ID__NAME")==null){
             return "缺少C_CUSTOMER_ID__NAME";
         }
         if(modVip.get("SEX")==null){
             return "缺少SEX";
         }
         if(modVip.get("SALESREP_ID__NAME")==null){
             return "缺少SALESREP_ID__NAME";
         }
         if(modVip.get("id")==null){
             return  "缺少id";
         }

         String info= Rest.modify("C_VIP",modVip);


         return  info;
     }





     //查询vip

    public  String  selVip(int id){

        //返回 会员ID 会员类型 会员卡号
        HashMap<String,Object> map=new HashMap<String, Object>();
        //查询
        map.put("table","C_VIP_IMP");
        map.put("columns",new String[]{"id","C_VIPTYPE_ID","CARDNO"});
        JSONObject expr1 = new JSONObject();
        expr1.put("column", "id");
        expr1.put("condition", id);
        map.put("params",expr1);

        String info= Rest.selectVip(map);
                return  info;
    }


    //会员密码短信推送,修改密码(C_VIP)

    /**
     *
     * @param modVip
     * @return
     * 修改字段
     * VIPNAME,C_CUSTOMER_ID__NAME,SEX,SALESREP_ID__NAME,SALEHREMP_ID__NAME,PASS_WORD,INTEGRAL_PASSWORD,id
     */

    public  String  modPasswordVip(HashMap<String,Object> modVip){

        if(modVip.get("VIPNAME")==null){
            return "缺少VIPNAME";
        }
        if(modVip.get("C_CUSTOMER_ID__NAME")==null){
            return "缺少C_CUSTOMER_ID__NAME";
        }
        if(modVip.get("SEX")==null){
            return "缺少SEX";
        }
        if(modVip.get("SALESREP_ID__NAME")==null){
            return "缺少SALESREP_ID__NAME";
        }
        if(modVip.get("id")==null){
            return  "缺少id";
        }
        if(modVip.get("PASS_WORD")==null){
            return "缺少PASS_WORD";
        }
        if(modVip.get("INTEGRAL_PASSWORD")==null){
            return "缺少INTEGRAL_PASSWORD";
        }


            String info= Rest.modify("C_VIP",modVip);

                return  info;
    }

    //券信息获取

    public String couponInfo(int vipid){


        String info=Rest.excuteWebaction("C_VIP_TDEFTICKET_GEN",vipid);

        return  info;
    }


    //VIP卡充值单据 新增

    /**
     *
     * @param
     * @return
     * 新增字段
     * BILLDATE,RECHARGE_TYPE,C_VIPMONEY_STORE_ID__NAME,SALESREP_ID__NAME,C_VIP_ID__CARDNO，
     *    TOT_AMT_ACTUAL,AMOUNT_ACTUAL,ACTIVE_CONTENT,DESCRIPTION,KVGR1
     */

    public  String  addPrepaidDocuments(HashMap<String,Object> documentInfo) {

        if (documentInfo.get("BILLDATE") == null){

            return "缺少BILLDATE";
        }
        if(documentInfo.get("RECHARGE_TYPE")==null){
            return "缺少RECHARGE_TYPE";
        }
        if(documentInfo.get("C_VIPMONEY_STORE_ID__NAME")==null){
            return  "缺少C_VIPMONEY_STORE_ID__NAME";

        }
        if(documentInfo.get("SALESREP_ID__NAME")==null){
            return  "缺少SALESREP_ID__NAME";

        }
         if(documentInfo.get("C_VIP_ID__CARDNO")==null){
             return  "缺少C_VIP_ID__CARDNO";

         }
        if(documentInfo.get("TOT_AMT_ACTUAL")==null){
            return  "缺少TOT_AMT_ACTUAL";

        }

        if(documentInfo.get("AMOUNT_ACTUAL")==null){

            return  "缺少AMOUNT_ACTUAL";
        }
        if(documentInfo.get("ACTIVE_CONTENT")==null){
            return  "缺少ACTIVE_CONTENT";

        }

        String  info=Rest.Add("B_VIPMONEY", documentInfo);

        System.out.println("info...."+info);

        JSONArray jsonArray=new JSONArray(info);
        int code= (Integer)jsonArray.getJSONObject(0).get("code");
        if(code==-1){
            return  info;
        }

        int id=(Integer) jsonArray.getJSONObject(0).get("objectid");

        String vipinfo=selVip(id);

        System.out.println(vipinfo);


        return vipinfo;
    }


    ////VIP卡充值单据 审核(修改单据状态)

    public  String  modPrepaidStatus(HashMap<String,Object> modStatus){

        if(modStatus.get("VIPNAME")==null){
            return "缺少VIPNAME";
        }
        if(modStatus.get("C_CUSTOMER_ID__NAME")==null){
            return "缺少C_CUSTOMER_ID__NAME";
        }
        if(modStatus.get("SEX")==null){
            return "缺少SEX";
        }
        if(modStatus.get("SALESREP_ID__NAME")==null){
            return "缺少SALESREP_ID__NAME";
        }
        if(modStatus.get("id")==null){
            return  "缺少id";
        }
        if(modStatus.get("PASS_WORD")==null){
            return "缺少PASS_WORD";
        }
        if(modStatus.get("INTEGRAL_PASSWORD")==null){
            return "缺少INTEGRAL_PASSWORD";
        }
        if(modStatus.get("ISACTIVE")==null){

            return  "缺少ISACTIVE";
        }


        String info= Rest.modify("B_VIPMONEY",modStatus);

        return  info;
    }










//
//
//    //发送短信(未测试)
//
//    public static  String sendSMS(HashMap<String,Object> hashmap){
//        SendSMSRequestParams sendsmsRequestParams = new SendSMSRequestParams();
//        SendSMSRequestParams.SendSMSTrans sendsmsTrans = sendsmsRequestParams.new SendSMSTrans();
//        SendSMSRequestParams.SendSMSTrans.SendSMSTransParams sendsmsTransParams = sendsmsTrans.new SendSMSTransParams();
//
//        //设置id
//        sendsmsTrans.setId(112);
//
//        //请求方式
//        sendsmsTrans.setCommand("com.agilecontrol.nea.monitor.SendSMS");
//
//        //请求params
//        //添加手机 短信
//
////        sendsmsTransParams.setContent();
////        sendsmsTransParams.setPhone();
////        sendsmsTransParams.setSendtime();
//
//
//        sendsmsTrans.setParams(sendsmsTransParams);
//
//        sendsmsRequestParams.setTransactions(sendsmsTrans);
//
//        //执行添加
//        String info=getConnection(sendsmsRequestParams.getSip_sign(),sendsmsRequestParams.getSip_appkey(),
//                sendsmsRequestParams.getAppSecret(),sendsmsRequestParams.getSip_timestamp(),
//                sendsmsRequestParams.getTransactions());
//
//        return  info;
//    }
//
//
//    //执行查询
//    public  static  String excuteSql(HashMap<String,Object> hashmap){
//
//        ExecuteSQLRequestParams executeSQLRequestParams = new ExecuteSQLRequestParams();
//        ExecuteSQLRequestParams.ExecuteSQLTrans executeSQLTrans = executeSQLRequestParams.new ExecuteSQLTrans();
//        ExecuteSQLRequestParams.ExecuteSQLTrans.ExecuteSQLTransParams executeSQLTransParams = executeSQLTrans.new ExecuteSQLTransParams();
//
//        for(String key:hashmap.keySet()){
//            if(key.equals("name")){
//                executeSQLTransParams.setName(hashmap.get(key).toString());
//            }else if(key.equals("values")){
//                executeSQLTransParams.setValues(new org.json.JSONArray(hashmap.get(key)));
//            }
//        }
//
//        executeSQLTrans.setId(112);
//        executeSQLTrans.setCommand("ExecuteSQL");
//        executeSQLTrans.setParams(executeSQLTransParams);
//
//        executeSQLRequestParams.setTransactions(executeSQLTrans);
//
//
//        //执行添加
//        String info=getConnection(executeSQLRequestParams.getSip_sign(),executeSQLRequestParams.getSip_appkey(),
//                executeSQLRequestParams.getAppSecret(),executeSQLRequestParams.getSip_timestamp(),
//                executeSQLRequestParams.getTransactions());
//
//        return  info;
//    }
//

//
//    //获取一个对象(vip)的信息
//    public static String getObject(HashMap<String,Object> hashmap){
//
//        GetObjectRequestParams getObjectRequestParams=new GetObjectRequestParams();
//        GetObjectRequestParams.GetObjectTran getObjectTran= getObjectRequestParams.new GetObjectTran();
//        GetObjectRequestParams.GetObjectTran.GetObjectTransParams getObjectTransParams=
//                getObjectTran.new GetObjectTransParams();
//
//
//        for(String key:hashmap.keySet()){
//            if(key.equals("table")){
//                getObjectTransParams.setTable(hashmap.get(key).toString());
//            }else  if(key.equals("id")){
//                getObjectTransParams.setId((Integer)hashmap.get(key));
//            }else if(key.equals("reftables")){
//                getObjectTransParams.setReftables(new org.json.JSONArray(hashmap.get(key)));
//            }
//        }
//
//        getObjectTran.setId(112);
//        getObjectTran.setCommand("GetObject");
//        getObjectTran.setParams(getObjectTransParams);
//
//        getObjectRequestParams.setTransactions(getObjectTran);
//
//        //执行添加
//        String info=getConnection(getObjectRequestParams.getSip_sign(),getObjectRequestParams.getSip_appkey(),
//                getObjectRequestParams.getAppSecret(),getObjectRequestParams.getSip_timestamp(),
//                getObjectRequestParams.getTransactions());
//
//
//        System.out.println(info.toString());
//        return  info;
//
//
//    }
//
//
//    //删除一个vip信息
//
//    public  static  String  delObject(HashMap<String,Object> hashmap){
//
//        GetObjectRequestParams getObjectRequestParams=new GetObjectRequestParams();
//        GetObjectRequestParams.GetObjectTran getObjectTran= getObjectRequestParams.new GetObjectTran();
//        GetObjectRequestParams.GetObjectTran.GetObjectTransParams getObjectTransParams=
//                getObjectTran.new GetObjectTransParams();
//
//
//        for(String key:hashmap.keySet()){
//            if(key.equals("table")){
//                getObjectTransParams.setTable(hashmap.get(key).toString());
//            }else  if(key.equals("id")){
//                getObjectTransParams.setId((Integer)hashmap.get(key));
//            }
//        }
//
//        getObjectTran.setId(112);
//        getObjectTran.setCommand("ObjectDelete");
//        getObjectTran.setParams(getObjectTransParams);
//
//        getObjectRequestParams.setTransactions(getObjectTran);
//
//        //执行添加
//        String info=getConnection(getObjectRequestParams.getSip_sign(),getObjectRequestParams.getSip_appkey(),
//                getObjectRequestParams.getAppSecret(),getObjectRequestParams.getSip_timestamp(),
//                getObjectRequestParams.getTransactions());
//
//
//        System.out.println(info.toString());
//        return  info;
//
//    }
//
//    //提交单据
//    public  static  String  submitObject(HashMap<String,Object> hashmap){
//
//        GetObjectRequestParams getObjectRequestParams=new GetObjectRequestParams();
//        GetObjectRequestParams.GetObjectTran getObjectTran= getObjectRequestParams.new GetObjectTran();
//        GetObjectRequestParams.GetObjectTran.GetObjectTransParams getObjectTransParams=
//                getObjectTran.new GetObjectTransParams();
//
//
//        for(String key:hashmap.keySet()){
//            if(key.equals("table")){
//                getObjectTransParams.setTable(hashmap.get(key).toString());
//            }else  if(key.equals("id")){
//                getObjectTransParams.setId((Integer)hashmap.get(key));
//            }
//        }
//
//        getObjectTran.setId(112);
//        getObjectTran.setCommand("ObjectSubmit");
//        getObjectTran.setParams(getObjectTransParams);
//
//        getObjectRequestParams.setTransactions(getObjectTran);
//
//        //执行添加
//        String info=getConnection(getObjectRequestParams.getSip_sign(),getObjectRequestParams.getSip_appkey(),
//                getObjectRequestParams.getAppSecret(),getObjectRequestParams.getSip_timestamp(),
//                getObjectRequestParams.getTransactions());
//
//
//        System.out.println(info.toString());
//        return  info;
//
//    }
//
//
//    //取消提交单据
//    public  static  String  unSubmitObject(HashMap<String,Object> hashmap){
//
//        GetObjectRequestParams getObjectRequestParams=new GetObjectRequestParams();
//        GetObjectRequestParams.GetObjectTran getObjectTran= getObjectRequestParams.new GetObjectTran();
//        GetObjectRequestParams.GetObjectTran.GetObjectTransParams getObjectTransParams=
//                getObjectTran.new GetObjectTransParams();
//
//
//        for(String key:hashmap.keySet()){
//            if(key.equals("table")){
//                getObjectTransParams.setTable(hashmap.get(key).toString());
//            }else  if(key.equals("id")){
//                getObjectTransParams.setId((Integer)hashmap.get(key));
//            }
//        }
//
//        getObjectTran.setId(112);
//        getObjectTran.setCommand("ObjectUnsubmit");
//        getObjectTran.setParams(getObjectTransParams);
//
//        getObjectRequestParams.setTransactions(getObjectTran);
//
//        //执行添加
//        String info=getConnection(getObjectRequestParams.getSip_sign(),getObjectRequestParams.getSip_appkey(),
//                getObjectRequestParams.getAppSecret(),getObjectRequestParams.getSip_timestamp(),
//                getObjectRequestParams.getTransactions());
//
//
//        System.out.println(info.toString());
//        return  info;
//
//    }
//


//    //网络请求
//    public static String getConnection(String sip_sign,String sip_appkey,String appSecret,String sip_timestamp,
//                                 String transactions){
//
//
//
//        HttpResult result=null;
//        try{
//            Map<String,String> head=new HashMap<String,String>();
//            head.put("Content-Type", "application/x-www-form-urlencoded");
//            StringBuffer sb=new StringBuffer();
//            sb.append("sip_sign=").append(sip_sign).append("&")
//                    .append("sip_appkey=").append(sip_appkey).append("&")
//                    .append("appSecret=").append(appSecret).append("&")
//                    .append("sip_timestamp=").append(sip_timestamp).append("&")
//                    .append("transactions=").append(transactions);
//
//
//             result=webConnection.Request(url,"POST",head,sb.toString().getBytes());
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//
//
//        return  result.getPage().toString();
//    }


}
