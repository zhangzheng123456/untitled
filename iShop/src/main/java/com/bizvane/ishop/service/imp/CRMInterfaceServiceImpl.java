package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.network.*;
import com.bizvane.ishop.utils.HttpResult;
import com.bizvane.ishop.utils.WebConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanyadong on 2016/1/13.
 */
public class CRMInterfaceServiceImpl  {

    private static WebConnection webConnection=new WebConnection();
    public static final String sip_appkey="nea@burgeon.com.cn";
    public static final String appSecret="lifecycle";
    public static  String url = "http://60.190.132.114:8080/servlets/binserv/Rest";


    /**
     *
     * @param hashmap
     * @return
     *
     * 增加字段
     * VIPNAME，SALESREP_ID__NAME，C_VIPTYPE_ID__NAME，DOCNOS
     *
     *  VIPCARDNO__CARDNO，MOBIL，ADDRESS，SEX，BIRTHDAY
     */

    //增加会员(通过测试)
    public static String addvip(HashMap<String,Object> hashmap){

        CreateObjectRequestParams createObjectRequestParams = new CreateObjectRequestParams();
        CreateObjectRequestParams.CreateObjectTrans createObjectTrans = createObjectRequestParams.new CreateObjectTrans();

        createObjectRequestParams.setAppSecret(appSecret);
        createObjectRequestParams.setSip_appkey(sip_appkey);
        createObjectRequestParams.setSip_timestamp();
        createObjectRequestParams.setSip_sign();

        //请求方式
        createObjectTrans.setCommand("ObjectCreate");
        //设置id
        createObjectTrans.setId("112");
        //请求params
        JSONObject vipJsonObject = new JSONObject(hashmap);

        createObjectTrans.setParams(vipJsonObject);

        createObjectRequestParams.setTransactions(createObjectTrans);

        //执行添加
        String info=getConnection(createObjectRequestParams.getSip_sign(),createObjectRequestParams.getSip_appkey(),
                             createObjectRequestParams.getAppSecret(),createObjectRequestParams.getSip_timestamp(),
                             createObjectRequestParams.getTransactions());


        return  info;
    }

     //查询vip(通过测试)
    public static String selectvip(HashMap<String,Object> hashmap){

        QueryRequestParams queryRequestParams = new QueryRequestParams();
        QueryRequestParams.QueryTrans queryTrans = queryRequestParams.new QueryTrans();
        QueryRequestParams.QueryTrans.QueryTransParams queryTransParams = queryTrans.new QueryTransParams();

        queryRequestParams.setAppSecret(appSecret);
        queryRequestParams.setSip_appkey(sip_appkey);
        queryRequestParams.setSip_timestamp();
        queryRequestParams.setSip_sign();

        //请求方式
        queryTrans.setCommand("Query");
        //设置id
        queryTrans.setId(112);

        /**
         *  以下为查询的params请求参数
         */

        for(String key:hashmap.keySet()){
            if(key.equals("table")){
                queryTransParams.setTable(hashmap.get(key).toString());
            }else if(key.equals("count")){
                queryTransParams.setCount(hashmap.get(key).toString());
            }
            else if(key.equals("start")){
                queryTransParams.setStart((Integer)hashmap.get(key));
            } else if(key.equals("range")){
                queryTransParams.setRange((Integer)hashmap.get(key));
            }else if(key.equals("columns")){
                queryTransParams.setColumns(new org.json.JSONArray(hashmap.get(key)));
            }else if(key.equals("qlcid")){
                queryTransParams.setQlcid(hashmap.get(key).toString());
            }else  if(key.equals("orderby")){
                queryTransParams.setOrderby(new org.json.JSONArray(hashmap.get(key)));
            }else if(key.equals("params")){
                queryTransParams.setParams(new JSONObject(hashmap.get(key)));
            }else  if(key.equals("column_masks")){
                queryTransParams.setColumn_masks(new JSONArray(hashmap.get(key)));
            }
        }

        //请求的param
        queryTrans.setParams(queryTransParams);
        queryRequestParams.setTransactions(queryTrans);

        //查询
        String info=getConnection(queryRequestParams.getSip_sign(),queryRequestParams.getSip_appkey(),
                queryRequestParams.getAppSecret(),queryRequestParams.getSip_timestamp(),
                queryRequestParams.getTransactions());

          System.out.println(info.toString());
        return  info;
    }


    //修改会员(测试成功)
    public  static  String modifyvip(HashMap<String,Object> hashmap){

        ModifyObjectRequestParams modifyObjectRequestParams = new ModifyObjectRequestParams();
        ModifyObjectRequestParams.ModifyObjectTrans modifyObjectTrans = modifyObjectRequestParams.new ModifyObjectTrans();

        modifyObjectRequestParams.setAppSecret(appSecret);
        modifyObjectRequestParams.setSip_appkey(sip_appkey);
        modifyObjectRequestParams.setSip_timestamp();
        modifyObjectRequestParams.setSip_sign();

        //查询方式为修改
        modifyObjectTrans.setCommand("ObjectModify");

        //id值
        modifyObjectTrans.setId("112");

            JSONObject vipJsonObject = new JSONObject(hashmap);

        //params请求参数(修改表的数据)
        modifyObjectTrans.setParams(vipJsonObject);
       // System.out.println("modifyVip jsonObject:" + vipJsonObject.toString());
        modifyObjectRequestParams.setTransactions(modifyObjectTrans);

        //查询
        String info=getConnection(modifyObjectRequestParams.getSip_sign(),modifyObjectRequestParams.getSip_appkey(),
                modifyObjectRequestParams.getAppSecret(),modifyObjectRequestParams.getSip_timestamp(),
                modifyObjectRequestParams.getTransactions());

        System.out.println(info.toString());

        return  info;
    }

    //发送短信(未测试)

    public static  String sendSMS(HashMap<String,Object> hashmap){
        SendSMSRequestParams sendsmsRequestParams = new SendSMSRequestParams();
        SendSMSRequestParams.SendSMSTrans sendsmsTrans = sendsmsRequestParams.new SendSMSTrans();
        SendSMSRequestParams.SendSMSTrans.SendSMSTransParams sendsmsTransParams = sendsmsTrans.new SendSMSTransParams();

        sendsmsRequestParams.setAppSecret(appSecret);
        sendsmsRequestParams.setSip_appkey(sip_appkey);
        sendsmsRequestParams.setSip_timestamp();
        sendsmsRequestParams.setSip_sign();

        //设置id
        sendsmsTrans.setId(112);

        //请求方式
        sendsmsTrans.setCommand("com.agilecontrol.nea.monitor.SendSMS");

        //请求params
        //添加手机 短信

//        sendsmsTransParams.setContent();
//        sendsmsTransParams.setPhone();
//        sendsmsTransParams.setSendtime();


        sendsmsTrans.setParams(sendsmsTransParams);

        sendsmsRequestParams.setTransactions(sendsmsTrans);

        //执行添加
        String info=getConnection(sendsmsRequestParams.getSip_sign(),sendsmsRequestParams.getSip_appkey(),
                sendsmsRequestParams.getAppSecret(),sendsmsRequestParams.getSip_timestamp(),
                sendsmsRequestParams.getTransactions());

        return  info;
    }


    //执行查询
    public  static  String excuteSql(HashMap<String,Object> hashmap){
        ExecuteSQLRequestParams executeSQLRequestParams = new ExecuteSQLRequestParams();
        ExecuteSQLRequestParams.ExecuteSQLTrans executeSQLTrans = executeSQLRequestParams.new ExecuteSQLTrans();
        ExecuteSQLRequestParams.ExecuteSQLTrans.ExecuteSQLTransParams executeSQLTransParams = executeSQLTrans.new ExecuteSQLTransParams();

        executeSQLRequestParams.setAppSecret(appSecret);
        executeSQLRequestParams.setSip_appkey(sip_appkey);
        executeSQLRequestParams.setSip_timestamp();
        executeSQLRequestParams.setSip_sign();


        for(String key:hashmap.keySet()){
            if(key.equals("name")){
                executeSQLTransParams.setName(hashmap.get(key).toString());
            }else if(key.equals("values")){
                executeSQLTransParams.setValues(new org.json.JSONArray(hashmap.get(key)));
            }
        }

        executeSQLTrans.setId(112);
        executeSQLTrans.setParams(executeSQLTransParams);

        executeSQLRequestParams.setTransactions(executeSQLTrans);


        //执行添加
        String info=getConnection(executeSQLRequestParams.getSip_sign(),executeSQLRequestParams.getSip_appkey(),
                executeSQLRequestParams.getAppSecret(),executeSQLRequestParams.getSip_timestamp(),
                executeSQLRequestParams.getTransactions());

        return  info;
    }

    //执行WebAction查询

    public static String excuteWebaction(HashMap<String,Object> hashmap){

        ExecuteWebActionRequestParams executeWebActionRequestParams = new ExecuteWebActionRequestParams();
        ExecuteWebActionRequestParams.ExecuteWebActionTrans executeWebActionTrans = executeWebActionRequestParams.new ExecuteWebActionTrans();
        ExecuteWebActionRequestParams.ExecuteWebActionTrans.ExecuteWebActionTransParams executeWebActionTransParams = executeWebActionTrans.new ExecuteWebActionTransParams();

        executeWebActionRequestParams.setAppSecret(appSecret);
        executeWebActionRequestParams.setSip_appkey(sip_appkey);
        executeWebActionRequestParams.setSip_timestamp();
        executeWebActionRequestParams.setSip_sign();

        for(String key:hashmap.keySet()){
            if(key.equals("webaction")){
                executeWebActionTransParams.setWebaction(hashmap.get(key).toString());
            }else  if(key.equals("id")){
                executeWebActionTransParams.setId(hashmap.get(key).toString());
            }
        }

        executeWebActionTrans.setId(112);
        executeWebActionTrans.setParams(executeWebActionTransParams);

        executeWebActionRequestParams.setTransactions(executeWebActionTrans);

        //执行添加
        String info=getConnection(executeWebActionRequestParams.getSip_sign(),executeWebActionRequestParams.getSip_appkey(),
                executeWebActionRequestParams.getAppSecret(),executeWebActionRequestParams.getSip_timestamp(),
                executeWebActionRequestParams.getTransactions());

        System.out.println(info);
        return  info;

    }

    //获取一个对象(vip)的信息
    public static String getObject(HashMap<String,Object> hashmap){

        GetObjectRequestParams getObjectRequestParams=new GetObjectRequestParams();
        GetObjectRequestParams.GetObjectTran getObjectTran= getObjectRequestParams.new GetObjectTran();
        GetObjectRequestParams.GetObjectTran.GetObjectTransParams getObjectTransParams=
                getObjectTran.new GetObjectTransParams();

        getObjectRequestParams.setAppSecret(appSecret);
        getObjectRequestParams.setSip_appkey(sip_appkey);
        getObjectRequestParams.setSip_timestamp();
        getObjectRequestParams.setSip_sign();

        for(String key:hashmap.keySet()){
            if(key.equals("table")){
                getObjectTransParams.setTable(hashmap.get(key).toString());
            }else  if(key.equals("id")){
                getObjectTransParams.setId((Integer)hashmap.get(key));
            }else if(key.equals("reftables")){
                getObjectTransParams.setReftables(new org.json.JSONArray(hashmap.get(key)));
            }
        }

        getObjectTran.setId(112);
        getObjectTran.setParams(getObjectTransParams);

        getObjectRequestParams.setTransactions(getObjectTran);

        //执行添加
        String info=getConnection(getObjectRequestParams.getSip_sign(),getObjectRequestParams.getSip_appkey(),
                getObjectRequestParams.getAppSecret(),getObjectRequestParams.getSip_timestamp(),
                getObjectRequestParams.getTransactions());


        System.out.println(info.toString());
        return  info;


    }



    //网络请求
    public static String getConnection(String sip_sign,String sip_appkey,String appSecret,String sip_timestamp,
                                 String transactions){



        HttpResult result=null;
        try{
            Map<String,String> head=new HashMap<String,String>();
            head.put("Content-Type", "application/x-www-form-urlencoded");
            StringBuffer sb=new StringBuffer();
            sb.append("sip_sign=").append(sip_sign).append("&")
                    .append("sip_appkey=").append(sip_appkey).append("&")
                    .append("appSecret=").append(appSecret).append("&")
                    .append("sip_timestamp=").append(sip_timestamp).append("&")
                    .append("transactions=").append(transactions);


             result=webConnection.Request(url,"POST",head,sb.toString().getBytes());

        }catch(Exception e){
            e.printStackTrace();
        }



        return  result.getPage().toString();
    }


}
