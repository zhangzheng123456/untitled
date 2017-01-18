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

             if (code == -1) {
                 JSONObject jsonObject=jsonArray.getJSONObject(0);
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
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if(code==-1){

                return  jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1=new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id",id1);

            if(jsonArray1.length()>0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                maprow.put("id", rowjsonArray.get(0));
                maprow.put("C_VIPTYPE_ID", rowjsonArray.get(1));
                maprow.put("CARDNO", rowjsonArray.get(2));
            }
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();

        }else{

        }
        return  infos;
    }

    //查询单据编号+id
    public  String selBill(String table,String corpcode,int id){

        String  infos="";

        if(corpcode.equals("C10016")) {

            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", table);
            map.put("columns", new String[]{"id", "DOCNO"});
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
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if(code==-1){

                return  jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1=new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id",id1);

            if(jsonArray1.length()>0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                maprow.put("id", rowjsonArray.get(0));
                maprow.put("DOCNO", rowjsonArray.get(1));
            }
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
     * @param
     * @return
     * 修改modfiy_integral_password
     * VIPNAME,C_CUSTOMER_ID__NAME,SEX,SALESREP_ID__NAME,SALEHREMP_ID__NAME,PASS_WORD,INTEGRAL_PASSWORD,id
     */

    //积分付款密码（INTEGRAL_PASSWORD）
    public  String  modIntegral_passwordVip(String corpcode,HashMap<String,Object> integral_passwordVip){

        String info="";

        if(corpcode.equals("C10016")) {

            String integral_password=integral_passwordVip.get("INTEGRAL_PASSWORD").toString();
            String id=integral_passwordVip.get("id").toString();

            HashMap<String,Object> map=new HashMap<String, Object>();
            map.put("name","modfiy_integral_password");
            JSONArray jsonArray=new JSONArray();
            jsonArray.put(integral_password);
            jsonArray.put(id);
            map.put("values",jsonArray);
            info= Rest.excuteSql(corpcode,map);
            JSONArray jsonArray1=new JSONArray(info);
            info=jsonArray1.getJSONObject(0).toString();

        }else{

        }

        return  info;

    }


    //预存款密码（PASS_WORD）
    public  String  modfiy_passwordVip(String corpcode,HashMap<String,Object> passwordMap){

        String info="";

        if(corpcode.equals("C10016")) {

            String modfiy_password=passwordMap.get("PASS_WORD").toString();
            String id=passwordMap.get("id").toString();

            HashMap<String,Object> map=new HashMap<String, Object>();
            map.put("name","modfiy_password");
            JSONArray jsonArray=new JSONArray();
            jsonArray.put(modfiy_password);
            jsonArray.put(id);
            map.put("values",jsonArray);
            info= Rest.excuteSql(corpcode,map);
            JSONArray jsonArray1=new JSONArray(info);
            info=jsonArray1.getJSONObject(0).toString();

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

        String billinfo="";
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

           String info = Rest.Add("B_VIPMONEY", corpcode,documentInfo);

            JSONArray jsonArray=new JSONArray(info);
            JSONObject jsonOb=jsonArray.getJSONObject(0);

            int code = (Integer) jsonOb.get("code");

            if (code == -1) {
                return jsonOb.toString();
            }

            int id = (Integer) jsonOb.get("objectid");

            billinfo= selBill("B_VIPMONEY",corpcode,id);

      //      System.out.println("info...." + info);

        }else{

        }

        return billinfo;
    }


    ////VIP卡充值单据

    public  String  modPrepaidStatus(String corpcode,HashMap<String,Object> modStatus){

        String info="";
        HashMap<String,Object> errormap=new HashMap<String, Object>();
        if(corpcode.equals("C10016")) {

            if (modStatus.get("id") == null) {

                errormap.put("message","缺少id");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();
            }

            for(String key:modStatus.keySet()){
                if(key.equals("bill_date")){
                    modStatus.put("BILLDATE",modStatus.get(key).toString());
                    modStatus.remove(key);
                }
                if(key.equals("recharge_type")){
                    modStatus.put("RECHARGE_TYPE",modStatus.get(key).toString());
                    modStatus.remove(key);
                }
                if(key.equals("store_name")){
                    modStatus.put("C_VIPMONEY_STORE_ID__NAME",modStatus.get(key).toString());
                    modStatus.remove(key);
                }
                if(key.equals("user_name")){
                    modStatus.put("SALESREP_ID__NAME",modStatus.get(key).toString());
                    modStatus.remove(key);
                }
                if(key.equals("vip_cardno")){
                    modStatus.put("C_VIP_ID__CARDNO",modStatus.get(key).toString());
                    modStatus.remove(key);
                }
                if(key.equals("tag_price")){
                    modStatus.put("TOT_AMT_ACTUAL",modStatus.get(key).toString());
                    modStatus.remove(key);
                }
                if(key.equals("pay_price")){
                    modStatus.put("AMOUNT_ACTUAL",modStatus.get(key).toString());
                    modStatus.remove(key);
                }
                if(key.equals("active_content")){
                    modStatus.put("ACTIVE_CONTENT",modStatus.get(key).toString());
                    modStatus.remove(key);
                }

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

        String billinfo="";
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

           String  info = Rest.Add("B_RET_VIPMONEY", corpcode,refundInfo);

            JSONArray jsonArray=new JSONArray(info);
            JSONObject jsonOb=jsonArray.getJSONObject(0);

            int code = (Integer) jsonOb.get("code");

            if (code == -1) {
                return jsonOb.toString();
            }

            int id = (Integer) jsonOb.get("objectid");
            System.out.println("充值退款:"+id);

            billinfo= selBill("B_RET_VIPMONEY",corpcode,id);

        }else{

        }
        return  billinfo;

    }


    //修改充值退款单据状态

    public  String  modRefundStatus(String corpcode,HashMap<String,Object> modStatusRefund){
        String info="";

        HashMap<String,Object> errormap=new HashMap<String, Object>();
        if(corpcode.equals("C10016")) {

            if (modStatusRefund.get("id") == null) {

                errormap.put("message","缺少id");
                errormap.put("code",-1);
                JSONObject jsonObject=new JSONObject(errormap);
                return jsonObject.toString();
            }

            for(String key:modStatusRefund.keySet()){

                if(key.equals("bill_date")){
                    modStatusRefund.put("BILLDATE",modStatusRefund.get(key).toString());
                    modStatusRefund.remove(key);
                }
                if(key.equals("recharge_type")){
                    modStatusRefund.put("RECHARGE_TYPE",modStatusRefund.get(key).toString());
                    modStatusRefund.remove(key);
                }
                if(key.equals("store_name")){
                    modStatusRefund.put("C_VIPMONEY_STORE_ID__NAME",modStatusRefund.get(key).toString());
                    modStatusRefund.remove(key);
                }
                if(key.equals("source_no")){
                    modStatusRefund.put("ORGDOCNO",modStatusRefund.get(key).toString());
                    modStatusRefund.remove(key);
                }
                if(key.equals("tag_price")){
                    modStatusRefund.put("TOT_AMT_ACTUAL",modStatusRefund.get(key).toString());
                    modStatusRefund.remove(key);
                }
            }

            info = Rest.modify("B_RET_VIPMONEY",corpcode, modStatusRefund);
            JSONArray jsonArray=new JSONArray(info);
           info= jsonArray.getJSONObject(0).toString();

        }else{

        }
        return  info;
    }


    //VIP卡充值单据提交
    public String submitPrepaidBill(String corpcode,int id){

        String info="";
        if(corpcode.equals("C10016")) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_VIPMONEY");
            map.put("id", id);
            info = Rest.submitObject(corpcode, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
        }
         return  info;
    }

    //VIP卡充值退款单据提交
    public String submitRefundBill(String corpcode,int id){

        String info="";
        if(corpcode.equals("C10016")) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_RET_VIPMONEY");
            map.put("id", id);
            info = Rest.submitObject(corpcode, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
        }
        return  info;
    }


    //VIP卡充值单据取消提交
    public String canclePrepaidBill(String corpcode,int id){

        String info="";
        if(corpcode.equals("C10016")) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_VIPMONEY");
            map.put("id", id);
            info = Rest.unSubmitObject(corpcode, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
        }
        return  info;
    }

    //VIP卡充值退款单据取消提交
    public String cancleRefundBill(String corpcode,int id){

        String info="";
        if(corpcode.equals("C10016")) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_RET_VIPMONEY");
            map.put("id", id);
            info = Rest.unSubmitObject(corpcode, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
        }
        return  info;
    }





    //充值单详情

    public  String  getPrepaidOrder(String corpcode,HashMap<String,Object> prepaidMap){

        String infos="";

        if(corpcode.equals("C10016")) {


            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "B_VIPMONEY");
            String[] columns = new String[]{"id", "AD_CLIENT_ID", "AD_ORG_ID", "DOCNO", "BILLDATE", "RECHARGE_TYPE", "C_VIPMONEY_STORE_ID",
                    "SALESREP_ID", "C_VIP_ID", "TOT_AMT_ACTUAL", "AMOUNT_ACTUAL", "DISCOUNT", "C_MARKBALTYPE_ID", "C_CUSTOMER_ID", "STATUS",
                    "C_STORE_ID", "ACTIVE_CONTENT", "AU_STATE", "DESCRIPTION", "KVGR1", "IS_INSAP", "INSAPTIME", "AU_PI_ID", "OWNERID", "MODIFIERID"
                    , "CREATIONDATE", "MODIFIEDDATE", "STATUSERID", "STATUSTIME", "ISACTIVE"};
            map.put("columns", columns);
            JSONObject expr1 = new JSONObject();

            if(prepaidMap.get("DOCNO")!=null){
                expr1.put("column", "DOCNO");
                expr1.put("condition", prepaidMap.get("DOCNO").toString());
            }else{
                expr1.put("column", "id");
                expr1.put("condition", prepaidMap.get("id").toString());
            }

            map.put("params", expr1);

            String info = Rest.query(corpcode, map);

            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if(code==-1){

                return  jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1=new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id",id1);

            if(jsonArray1.length()>0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                for(int i=0;i<rowjsonArray.length();i++){
                    maprow.put(columns[i],rowjsonArray.get(i).toString());
                }
            }
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();

        }else{

        }
        return  infos;
    }


    //获取充值退款详情
    @Override
    public String getRefundOrder(String corpcode, String id) {

        String  infos="";

        if(corpcode.equals("C10016")) {

            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "B_RET_VIPMONEY");

            String[] columns= new String[]{"id","AD_CLIENT_ID", "AD_ORG_ID","DOCNO","BILLDATE","RECHARGE_TYPE","C_VIPMONEY_STORE_ID"
            ,"ORGDOCNO","USER_ID","C_VIP_ID","PASS_WORD","TOT_AMT_ACTUAL","AMOUNT_ACTUAL","DISCOUNT","C_MARKBALTYPE_ID","C_CUSTOMER_ID"
            ,"C_STORE_ID","STATUS","AU_STATE","DESCRIPTION","KVGR1","IS_INSAP","INSAPTIME","AU_PI_ID"};

            map.put("columns",columns);
            JSONObject expr1 = new JSONObject();
            expr1.put("column", "id");
            expr1.put("condition", id);
            map.put("params", expr1);

            String info = Rest.query(corpcode,map);

          //  System.out.println("cccc"+info);


            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if(code==-1){

                return  jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1=new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id",id1);

            if(jsonArray1.length()>0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                for(int i=0;i<rowjsonArray.length();i++){
                    maprow.put(columns[i],rowjsonArray.get(i).toString());
                }
            }
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();

        }else{

        }
        return  infos;

    }


    // 获取余额详情  FA_VIPACC

    public  String  getBalance(String corpcode,String vipid){

        String  infos="";

        if(corpcode.equals("C10016")) {

            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "FA_VIPACC");

            String[] columns= new String[]{"id","AD_CLIENT_ID", "AD_ORG_ID","C_VIP_ID","AMOUNT","AMOUNT_ACTUAL","PRE_DISCOUNT"
                    ,"AMT_ORDER","INTEGRAL","INTEGRAL_RANK","TOT_AMT_ACTUAL","TOT_AMT_LIST","AVG_DISCOUNT","TIMES","AVG_TIME_ACTUAL"
                    ,"AVG_QTY_ACTUAL","TOTQTY","LASTDATE","LAST_AMT_ACTUAL","FIRSTDATE","FIRST_AMT_ACTUAL","OWNERID","MODIFIERID"
                    ,"CREATIONDATE","MODIFIEDDATE","ISACTIVE"};

            map.put("columns",columns);
            JSONObject expr1 = new JSONObject();
            expr1.put("column", "C_VIP_ID");
            expr1.put("condition", vipid);
            map.put("params", expr1);

            String info = Rest.query(corpcode,map);

            System.out.println("获取余额信息"+info);


            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

                JSONArray jsonArray = new JSONArray(info);
                int code = (Integer) jsonArray.getJSONObject(0).get("code");
                String message = jsonArray.getJSONObject(0).get("message").toString();
                String id1 = jsonArray.getJSONObject(0).get("id").toString();

                if (code == -1) {

                    return jsonArray.getJSONObject(0).toString();

                }
                String rows = jsonArray.getJSONObject(0).get("rows").toString();
                JSONArray jsonArray1 = new JSONArray(rows);

                maprows.put("message", message);
                maprows.put("code", code);
                maprows.put("id", id1);

                if (jsonArray1.length() > 0) {
                    JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                    for (int i = 0; i < rowjsonArray.length(); i++) {
                        maprow.put(columns[i], rowjsonArray.get(i).toString());
                    }
                }
                maprows.put("rows", maprow);
                JSONObject jsonObject = new JSONObject(maprows);
                infos = jsonObject.toString();


        }else{

        }
        return  infos;


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

}
