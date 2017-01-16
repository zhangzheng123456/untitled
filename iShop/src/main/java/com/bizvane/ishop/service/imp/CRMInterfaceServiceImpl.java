package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.network.drpapi.burgeon.Rest;
import com.bizvane.ishop.service.CRMInterfaceService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by yanyadong on 2016/1/13.
 */

@Service
public class CRMInterfaceServiceImpl  implements CRMInterfaceService{



    //增加会员(C_VIP_IMP)

    /**
     *
     * @param vipInfo
     * @return
     * 新增字段
     * VIPNAME,SALESREP_ID__NAME,C_VIPTYPE_ID__NAME,DOCNOS,VIPCARDNO__CARDNO,MOBIL,ADDRESS,SEX,BIRTHDAY
     */

     public  String  addVip(String corpcode,HashMap<String,Object> vipInfo) {

         String vipinfo="";
         HashMap<String,Object> errormap=new HashMap<String, Object>();

         if(corpcode.equals("C10016")) {
             if (vipInfo.get("VIPNAME") == null) {

                 errormap.put("message","缺少VIPNAME");
                 errormap.put("code",-1);
                 JSONObject jsonObject=new JSONObject(errormap);
                 return jsonObject.toString();
             }
             if (vipInfo.get("SALESREP_ID__NAME") == null) {

                 errormap.put("message","缺少SALESREP_ID__NAME");
                 errormap.put("code",-1);
                 JSONObject jsonObject=new JSONObject(errormap);
                 return jsonObject.toString();
             }
             if (vipInfo.get("C_VIPTYPE_ID__NAME") == null) {

                 errormap.put("message","缺少C_VIPTYPE_ID__NAME");
                 errormap.put("code",-1);
                 JSONObject jsonObject=new JSONObject(errormap);
                 return jsonObject.toString();
             }
             if (vipInfo.get("DOCNOS") == null) {

                 errormap.put("message","缺少DOCNOS");
                 errormap.put("code",-1);
                 JSONObject jsonObject=new JSONObject(errormap);
                 return jsonObject.toString();

             }

             if (vipInfo.get("MOBIL") == null) {

                 errormap.put("message","缺少MOBIL");
                 errormap.put("code",-1);
                 JSONObject jsonObject=new JSONObject(errormap);
                 return jsonObject.toString();

             }

             if (vipInfo.get("SEX") == null) {

                 errormap.put("message","缺少SEX");
                 errormap.put("code",-1);
                 JSONObject jsonObject=new JSONObject(errormap);
                 return jsonObject.toString();

             }
             if (vipInfo.get("BIRTHDAY") == null) {

                 errormap.put("message","缺少BIRTHDAY");
                 errormap.put("code",-1);
                 JSONObject jsonObject=new JSONObject(errormap);
                 return jsonObject.toString();

             }

             String info = Rest.Add("C_VIP_IMP", corpcode,vipInfo);

             System.out.println("info...." + info);

             JSONArray jsonArray = new JSONArray(info);
             int code = (Integer) jsonArray.getJSONObject(0).get("code");
             String message = jsonArray.getJSONObject(0).get("message").toString();
             if (code == -1) {
                 HashMap<String, Object> map = new HashMap<String, Object>();
                 map.put("code", code);
                 map.put("message", message);
                 JSONObject jsonObject = new JSONObject(map);
                 return jsonObject.toString();
             }

             int id = (Integer) jsonArray.getJSONObject(0).get("objectid");

              vipinfo= selVip(corpcode,id);

             System.out.println(vipinfo);

         }else{


         }
         return vipinfo;
     }

    /**
     *
     * @param modVip
     * 修改字段
     * @return  VIPNAME,C_CUSTOMER_ID__NAME,SEX,SALESREP_ID__NAME,SALEHREMP_ID__NAME
     */

     //修改信息（C_VIP）
     public  String  modInfoVip(String corpcode,HashMap<String,Object> modVip){

         String info="";
         HashMap<String,Object> errormap=new HashMap<String, Object>();
         if(corpcode.equals("C10016")) {

//             if (modVip.get("VIPNAME") == null) {
//                 return "缺少VIPNAME";
//             }
//             if (modVip.get("C_CUSTOMER_ID__NAME") == null) {
//                 return "缺少C_CUSTOMER_ID__NAME";
//             }
//             if (modVip.get("SEX") == null) {
//                 return "缺少SEX";
//             }
//             if (modVip.get("SALESREP_ID__NAME") == null) {
//                 return "缺少SALESREP_ID__NAME";
//             }
             if (modVip.get("id") == null) {

                 errormap.put("message","缺少id");
                 errormap.put("code",-1);
                 JSONObject jsonObject=new JSONObject(errormap);
                 return  jsonObject.toString();
             }

              info= Rest.modify("C_VIP",corpcode, modVip);
             JSONArray jsonArray=new JSONArray(info);
             info=jsonArray.getJSONObject(0).toString();
         }
         else{

         }


         return  info;
     }





     //查询vip

    public  String  selVip(String corpcode,int id){

        String  infos="";

        if(corpcode.equals("C10016")) {
            //返回 会员ID 会员类型 会员卡号
            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "C_VIP_IMP");
            map.put("columns", new String[]{"id", "C_VIPTYPE_ID", "CARDNO"});
            JSONObject expr1 = new JSONObject();
            expr1.put("column", "id");
            expr1.put("condition", id);
            map.put("params", expr1);

            String info = Rest.query(corpcode,map);

            System.out.println("cccc"+info);


            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();

            if(code==-1){
                JSONArray jsonArray1=new JSONArray(info);
                return  jsonArray1.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray rowsjsonArray = new JSONArray(rows);
            JSONArray rowjsonArray = rowsjsonArray.getJSONArray(0);
            maprow.put("id", rowjsonArray.get(0));
            maprow.put("C_VIPTYPE_ID", rowjsonArray.get(1));
            maprow.put("CARDNO", rowjsonArray.get(2));
            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);

            infos = jsonObject.toString();
        }else{

        }
        return  infos;
    }


    //会员密码短信推送,修改密码(C_VIP)

    /**
     *
     * @param modVip
     * @return
     * 修改字段
     * VIPNAME,C_CUSTOMER_ID__NAME,SEX,SALESREP_ID__NAME,SALEHREMP_ID__NAME,PASS_WORD,INTEGRAL_PASSWORD,id
     */

    public  String  modPasswordVip(String corpcode,HashMap<String,Object> modVip){

        String info="";

        HashMap<String,Object> errormap=new HashMap<String, Object>();

        if(corpcode.equals("C10016")) {

//        if(modVip.get("VIPNAME")==null){
//            return "缺少VIPNAME";
//        }
//        if(modVip.get("C_CUSTOMER_ID__NAME")==null){
//            return "缺少C_CUSTOMER_ID__NAME";
//        }
//        if(modVip.get("SEX")==null){
//            return "缺少SEX";
//        }
//        if(modVip.get("SALESREP_ID__NAME")==null){
//            return "缺少SALESREP_ID__NAME";
//        }
            if (modVip.get("id") == null) {
                errormap.put("message","缺少id");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return  jsonObject.toString();
            }
            if (modVip.get("PASS_WORD") == null && modVip.get("SALESREP_ID__NAME") == null) {

                errormap.put("message","缺少PASS_WORD或者SALESREP_ID__NAME");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return  jsonObject.toString();
            }
//        if(modVip.get("INTEGRAL_PASSWORD")==null){
//            return "缺少INTEGRAL_PASSWORD";
//        }


             info = Rest.modify("C_VIP",corpcode, modVip);
            JSONArray jsonArray=new JSONArray(info);
            info=jsonArray.getJSONObject(0).toString();
        }else{

        }

                return  info;
    }

    //券信息获取

    public String couponInfo(String corpcode,int vipid){
        String info="";
        if(corpcode.equals("C10016")) {

            info = Rest.excuteWebaction(corpcode,"C_VIP_TDEFTICKET_GEN",vipid);
            JSONArray jsonArray=new JSONArray(info);
            info=jsonArray.getJSONObject(0).toString();
        }else{

        }

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

    public  String  addPrepaidDocuments(String corpcode,HashMap<String,Object> documentInfo) {

        String info="";
        HashMap<String,Object> errormap=new HashMap<String, Object>();
        if(corpcode.equals("C10016")) {

            if (documentInfo.get("BILLDATE") == null) {

                errormap.put("message","缺少BILLDATE");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);

                return jsonObject.toString();
            }
            if (documentInfo.get("RECHARGE_TYPE") == null) {

                errormap.put("message","缺少RECHARGE_TYPE");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);

                return jsonObject.toString();
            }
            if (documentInfo.get("C_VIPMONEY_STORE_ID__NAME") == null) {

                errormap.put("message","缺少C_VIPMONEY_STORE_ID__NAME");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();

            }
            if (documentInfo.get("SALESREP_ID__NAME") == null) {

                errormap.put("message","缺少SALESREP_ID__NAME");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();

            }
            if (documentInfo.get("C_VIP_ID__CARDNO") == null) {

                errormap.put("message","缺少C_VIP_ID__CARDNO");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();

            }
            if (documentInfo.get("TOT_AMT_ACTUAL") == null) {

                errormap.put("message","缺少TOT_AMT_ACTUAL");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();

            }

            if (documentInfo.get("AMOUNT_ACTUAL") == null) {
                errormap.put("message","缺少AMOUNT_ACTUAL");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);

                return jsonObject.toString();
            }
            if (documentInfo.get("ACTIVE_CONTENT") == null) {

                errormap.put("message","缺少ACTIVE_CONTENT");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();

            }

             info = Rest.Add("B_VIPMONEY", corpcode,documentInfo);

            JSONArray jsonArray=new JSONArray(info);
            info=jsonArray.getJSONObject(0).toString();

            System.out.println("info...." + info);

//        JSONArray jsonArray=new JSONArray(info);
//        int code= (Integer)jsonArray.getJSONObject(0).get("code");
//        if(code==-1){
//            return  info;
//        }
//
//        int id=(Integer) jsonArray.getJSONObject(0).get("objectid");
//
//        String vipinfo=selVip(id);
//
//        System.out.println(vipinfo);
        }else{

        }

        return info;
    }


    ////VIP卡充值单据 审核(修改单据状态)

    public  String  modPrepaidStatus(String corpcode,HashMap<String,Object> modStatus){

        String info="";
        HashMap<String,Object> errormap=new HashMap<String, Object>();
        if(corpcode.equals("C10016")) {

//            if (modStatus.get("BILLDATE") == null) {
//
//                return "缺少BILLDATE";
//            }
//            if (modStatus.get("RECHARGE_TYPE") == null) {
//                return "缺少RECHARGE_TYPE";
//            }
//            if (modStatus.get("C_VIPMONEY_STORE_ID__NAME") == null) {
//                return "缺少C_VIPMONEY_STORE_ID__NAME";
//
//            }
//            if (modStatus.get("SALESREP_ID__NAME") == null) {
//                return "缺少SALESREP_ID__NAME";
//
//            }
//            if (modStatus.get("C_VIP_ID__CARDNO") == null) {
//                return "缺少C_VIP_ID__CARDNO";
//
//            }
//            if (modStatus.get("TOT_AMT_ACTUAL") == null) {
//                return "缺少TOT_AMT_ACTUAL";
//
//            }
//
//            if (modStatus.get("AMOUNT_ACTUAL") == null) {
//
//                return "缺少AMOUNT_ACTUAL";
//            }
//            if (modStatus.get("ACTIVE_CONTENT") == null) {
//                return "缺少ACTIVE_CONTENT";
//
//            }
            if (modStatus.get("STATUS") == null) {
                errormap.put("message","缺少STATUS");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);

                return jsonObject.toString();
            }
            if (modStatus.get("id") == null) {

                errormap.put("message","缺少id");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();
            }


            info = Rest.modify("B_VIPMONEY",corpcode, modStatus);
            JSONArray jsonArray=new JSONArray(info);
            info=jsonArray.getJSONObject(0).toString();
        }else{

        }

        return  info;
    }


    /**
     * VIP充值退款：
     处理方式：CRM调用DRP接口完成充值单据新增、审核（审核状态如何修改）
     审核（修改单据状态）
     表（B_RET_VIPMONEY）
     */

    //充值退款单据新增
    public  String addRefund(String corpcode,HashMap<String,Object> refundInfo){

        String info="";
        HashMap<String,Object> errormap=new HashMap<String, Object>();
        if(corpcode.equals("C10016")) {

            if (refundInfo.get("BILLDATE") == null) {

                errormap.put("message","缺少BILLDATE");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);

                return jsonObject.toString();
            }
            if (refundInfo.get("RECHARGE_TYPE") == null) {
                errormap.put("message","缺少RECHARGE_TYPE");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();
            }
            if (refundInfo.get("C_VIPMONEY_STORE_ID__NAME") == null) {
                errormap.put("message","缺少C_VIPMONEY_STORE_ID__NAME");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);

                return jsonObject.toString();

            }

             info = Rest.Add("B_RET_VIPMONEY", corpcode,refundInfo);
            JSONArray jsonArray=new JSONArray(info);
           info= jsonArray.getJSONObject(0).toString();

        }else{

        }
        return  info;

    }


    //修改充值退款单据状态

    public  String  modRefundStatus(String corpcode,HashMap<String,Object> modStatusRefund){
        String info="";

        HashMap<String,Object> errormap=new HashMap<String, Object>();
        if(corpcode.equals("C10016")) {

//            if (modStatusRefund.get("BILLDATE") == null) {
//                return "缺少BILLDATE";
//            }
//            if (modStatusRefund.get("RECHARGE_TYPE") == null) {
//                return "缺少RECHARGE_TYPE";
//            }
//            if (modStatusRefund.get("C_VIPMONEY_STORE_ID__NAME") == null) {
//                return "缺少C_VIPMONEY_STORE_ID__NAME";
//            }

            if (modStatusRefund.get("STATUS") == null) {

                errormap.put("message","缺少STATUS");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);

                return jsonObject.toString();
            }

            if (modStatusRefund.get("id") == null) {

                errormap.put("message","缺少id");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();
            }

            info = Rest.modify("B_RET_VIPMONEY",corpcode, modStatusRefund);
            JSONArray jsonArray=new JSONArray(info);
           info= jsonArray.getJSONObject(0).toString();

        }else{

        }
        return  info;
    }


    //充值单验证

//    public  String  selPrepaidOrder(int id){
//
//        //返回 会员ID 会员类型 会员卡号
//        HashMap<String,Object> map=new HashMap<String, Object>();
//        //查询
//        map.put("table","C_VIP_IMP");
//        map.put("columns",new String[]{"id","C_VIPTYPE_ID","CARDNO"});
//        JSONObject expr1 = new JSONObject();
//        expr1.put("column", "id");
//        expr1.put("condition", id);
//        map.put("params",expr1);
//
//        String info= Rest.query(map);
//
//        //System.out.println("cccc"+info);
//
//        HashMap<String,Object> maprows=new HashMap<String, Object>();
//        HashMap<String,Object> maprow=new HashMap<String, Object>();
//
//
//        JSONArray jsonArray=new JSONArray(info);
//        int code= (Integer)jsonArray.getJSONObject(0).get("code");
//        String message=jsonArray.getJSONObject(0).get("message").toString();
//        String rows=jsonArray.getJSONObject(0).get("rows").toString();
//        JSONArray rowsjsonArray=new JSONArray(rows);
//        JSONArray rowjsonArray=rowsjsonArray.getJSONArray(0);
//        maprow.put("id",rowjsonArray.get(0));
//        maprow.put("C_VIPTYPE_ID",rowjsonArray.get(1));
//        maprow.put("CARDNO",rowjsonArray.get(2));
//        maprows.put("message",message);
//        maprows.put("code",code);
//        maprows.put("rows",maprow);
//        JSONObject jsonObject=new JSONObject(maprows);
//
//        return  jsonObject.toString();
//    }
//
//


    //余额验证

//    public  String  selPrepaidOrder(int id){
//
//        //返回 会员ID 会员类型 会员卡号
//        HashMap<String,Object> map=new HashMap<String, Object>();
//        //查询
//        map.put("table","C_VIP_IMP");
//        map.put("columns",new String[]{"id","C_VIPTYPE_ID","CARDNO"});
//        JSONObject expr1 = new JSONObject();
//        expr1.put("column", "id");
//        expr1.put("condition", id);
//        map.put("params",expr1);
//
//        String info= Rest.query(map);
//
//        //System.out.println("cccc"+info);
//
//        HashMap<String,Object> maprows=new HashMap<String, Object>();
//        HashMap<String,Object> maprow=new HashMap<String, Object>();
//
//
//        JSONArray jsonArray=new JSONArray(info);
//        int code= (Integer)jsonArray.getJSONObject(0).get("code");
//        String message=jsonArray.getJSONObject(0).get("message").toString();
//        String rows=jsonArray.getJSONObject(0).get("rows").toString();
//        JSONArray rowsjsonArray=new JSONArray(rows);
//        JSONArray rowjsonArray=rowsjsonArray.getJSONArray(0);
//        maprow.put("id",rowjsonArray.get(0));
//        maprow.put("C_VIPTYPE_ID",rowjsonArray.get(1));
//        maprow.put("CARDNO",rowjsonArray.get(2));
//        maprows.put("message",message);
//        maprows.put("code",code);
//        maprows.put("rows",maprow);
//        JSONObject jsonObject=new JSONObject(maprows);
//
//        return  jsonObject.toString();
//    }













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
