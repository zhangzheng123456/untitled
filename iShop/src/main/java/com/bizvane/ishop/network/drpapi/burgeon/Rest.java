package com.bizvane.ishop.network.drpapi.burgeon;

import com.bizvane.ishop.network.drpapi.burgeon.requestparam.*;
import com.bizvane.ishop.utils.HttpResult;
import com.bizvane.ishop.utils.WebConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/1/15.
 */
public class Rest {



    private static WebConnection webConnection=new WebConnection();

    //public static  String url = "http://60.190.132.114:8080/servlets/binserv/Rest";



    public static String Add(String table, List<String> drplist, HashMap<String,Object> tableRow){

        RequestParams createObjectRequestParams = new RequestParams(drplist);
        RequestParams.BaseTrans createObjectTrans = createObjectRequestParams.new BaseTrans();

        //请求方式
        createObjectTrans.setCommand("ObjectCreate");
        //设置id
        createObjectTrans.setId(112);

        tableRow.put("table",table);
        //请求params
        JSONObject  paramsObject = new JSONObject(tableRow);


        createObjectTrans.setParams(paramsObject);

        createObjectRequestParams.setTransactions(createObjectTrans);

        //执行添加
        String info=getConnection(createObjectRequestParams.getUrl(),createObjectRequestParams.getSip_sign(), createObjectRequestParams.getSip_appkey(),
                createObjectRequestParams.getAppSecret(), createObjectRequestParams.getSip_timestamp(),
                createObjectRequestParams.getTransactions());


        return info;
    }


    //查询
    public static String query(List<String> drplist,HashMap<String,Object> tableRow){

        QueryRequestParams queryRequestParams = new QueryRequestParams(drplist);
        QueryRequestParams.QueryTrans queryTrans = queryRequestParams.new QueryTrans();
        QueryRequestParams.QueryTrans.QueryTransParams queryTransParams = queryTrans.new QueryTransParams();

        //请求方式
        queryTrans.setCommand("Query");
        //设置id
        queryTrans.setId(112);

        /**
         *  以下为查询的params请求参数
         */

        for(String key:tableRow.keySet()){
            if(key.equals("table")){
                queryTransParams.setTable(tableRow.get(key).toString());
            }else if(key.equals("count")){
                queryTransParams.setCount(tableRow.get(key).toString());
            }
            else if(key.equals("start")){
                queryTransParams.setStart((Integer)tableRow.get(key));
            } else if(key.equals("range")){
                queryTransParams.setRange((Integer)tableRow.get(key));
            }else if(key.equals("columns")){
                queryTransParams.setColumns(new org.json.JSONArray(tableRow.get(key)));
            }else if(key.equals("qlcid")){
                queryTransParams.setQlcid(tableRow.get(key).toString());
            }else  if(key.equals("orderby")){
                queryTransParams.setOrderby(new org.json.JSONArray(tableRow.get(key)));
            }else if(key.equals("params")){
                queryTransParams.setParams((JSONObject)tableRow.get(key));
            }else  if(key.equals("column_masks")){
                queryTransParams.setColumn_masks(new JSONArray(tableRow.get(key)));
            }
        }

        //请求的param

        queryTrans.setParams(queryTransParams);
        queryRequestParams.setTransactions(queryTrans);

        System.out.println("trans:..."+queryRequestParams.getTransactions());
        //查询
        String info=getConnection(queryRequestParams.getUrl(),queryRequestParams.getSip_sign(),queryRequestParams.getSip_appkey(),
                queryRequestParams.getAppSecret(),queryRequestParams.getSip_timestamp(),
                queryRequestParams.getTransactions());

        return  info;
    }


    //修改
    public  static  String modify(String table,List<String>  drplist,HashMap<String,Object> modVip){

        RequestParams modifyObjectRequestParams = new RequestParams(drplist);
        RequestParams.BaseTrans modifyObjectTrans = modifyObjectRequestParams.new BaseTrans();


        //查询方式为修改
        modifyObjectTrans.setCommand("ObjectModify");

        //id值
        modifyObjectTrans.setId(112);

        modVip.put("table",table);
        modVip.put("partial_update", true);

        JSONObject vipJsonObject = new JSONObject(modVip);

        //params请求参数(修改表的数据)
        modifyObjectTrans.setParams(vipJsonObject);
         System.out.println("modifyVip jsonObject:" + vipJsonObject.toString());
        modifyObjectRequestParams.setTransactions(modifyObjectTrans);

        //修改
        String info=getConnection(modifyObjectRequestParams.getUrl(),modifyObjectRequestParams.getSip_sign(),modifyObjectRequestParams.getSip_appkey(),
                modifyObjectRequestParams.getAppSecret(),modifyObjectRequestParams.getSip_timestamp(),
                modifyObjectRequestParams.getTransactions());

        System.out.println(info.toString());

        return  info.toString();
    }


    //执行WebAction查询

    public static String excuteWebaction(List<String> drplist,String webaction,int vipid){

        ExecuteWebActionRequestParams executeWebActionRequestParams = new ExecuteWebActionRequestParams(drplist);
        ExecuteWebActionRequestParams.ExecuteWebActionTrans executeWebActionTrans = executeWebActionRequestParams.new ExecuteWebActionTrans();
        ExecuteWebActionRequestParams.ExecuteWebActionTrans.ExecuteWebActionTransParams executeWebActionTransParams = executeWebActionTrans.new ExecuteWebActionTransParams();

                executeWebActionTransParams.setWebaction(webaction);

                executeWebActionTransParams.setId(vipid);

        executeWebActionTrans.setId(112);
        executeWebActionTrans.setCommand("ExecuteWebAction");
        executeWebActionTrans.setParams(executeWebActionTransParams);

        executeWebActionRequestParams.setTransactions(executeWebActionTrans);

        //执行webaction
        String info=getConnection(executeWebActionRequestParams.getUrl(),executeWebActionRequestParams.getSip_sign(),executeWebActionRequestParams.getSip_appkey(),
                executeWebActionRequestParams.getAppSecret(),executeWebActionRequestParams.getSip_timestamp(),
                executeWebActionRequestParams.getTransactions());

        System.out.println(info);
        return  info;

    }

    //执行查询
    public  static  String excuteSql(List<String> drplist,HashMap<String,Object> hashmap){

        ExecuteSQLRequestParams executeSQLRequestParams = new ExecuteSQLRequestParams(drplist);
        ExecuteSQLRequestParams.ExecuteSQLTrans executeSQLTrans = executeSQLRequestParams.new ExecuteSQLTrans();
        ExecuteSQLRequestParams.ExecuteSQLTrans.ExecuteSQLTransParams executeSQLTransParams = executeSQLTrans.new ExecuteSQLTransParams();

        for(String key:hashmap.keySet()){
            if(key.equals("name")){
                executeSQLTransParams.setName(hashmap.get(key).toString());
            }else if(key.equals("values")){
                executeSQLTransParams.setValues((JSONArray)hashmap.get(key));
            }
        }
        System.out.println(executeSQLTransParams.getValues());

        executeSQLTrans.setId(112);
        executeSQLTrans.setCommand("ExecuteSQL");
        executeSQLTrans.setParams(executeSQLTransParams);
        executeSQLRequestParams.setTransactions(executeSQLTrans);

        //执行添加
        String info=getConnection(executeSQLRequestParams.getUrl(),executeSQLRequestParams.getSip_sign(),executeSQLRequestParams.getSip_appkey(),
                executeSQLRequestParams.getAppSecret(),executeSQLRequestParams.getSip_timestamp(),
                executeSQLRequestParams.getTransactions());

        return  info;
    }

    //提交单据
    public  static  String  submitObject(List<String> drplist,HashMap<String,Object> hashmap){

        GetOrSubOrUnSubRequestParams getObjectRequestParams=new GetOrSubOrUnSubRequestParams(drplist);
        GetOrSubOrUnSubRequestParams.GetObjectTran getObjectTran= getObjectRequestParams.new GetObjectTran();
        GetOrSubOrUnSubRequestParams.GetObjectTran.GetObjectTransParams getObjectTransParams=
                getObjectTran.new GetObjectTransParams();


        for(String key:hashmap.keySet()){
            if(key.equals("table")){
                getObjectTransParams.setTable(hashmap.get(key).toString());
            }else  if(key.equals("id")){
                getObjectTransParams.setId((Integer)hashmap.get(key));
            }
        }

        getObjectTran.setId(112);
        getObjectTran.setCommand("ObjectSubmit");
        getObjectTran.setParams(getObjectTransParams);

        getObjectRequestParams.setTransactions(getObjectTran);

        //执行添加
        String info=getConnection(getObjectRequestParams.getUrl(),getObjectRequestParams.getSip_sign(),getObjectRequestParams.getSip_appkey(),
                getObjectRequestParams.getAppSecret(),getObjectRequestParams.getSip_timestamp(),
                getObjectRequestParams.getTransactions());


        System.out.println(info.toString());
        return  info;

    }


    //取消提交单据
    public  static  String  unSubmitObject(List<String> drplist,HashMap<String,Object> hashmap){

        GetOrSubOrUnSubRequestParams getObjectRequestParams=new GetOrSubOrUnSubRequestParams(drplist);
        GetOrSubOrUnSubRequestParams.GetObjectTran getObjectTran= getObjectRequestParams.new GetObjectTran();
        GetOrSubOrUnSubRequestParams.GetObjectTran.GetObjectTransParams getObjectTransParams=
                getObjectTran.new GetObjectTransParams();


        for(String key:hashmap.keySet()){
            if(key.equals("table")){
                getObjectTransParams.setTable(hashmap.get(key).toString());
            }else  if(key.equals("id")){
                getObjectTransParams.setId((Integer)hashmap.get(key));
            }
        }

        getObjectTran.setId(112);
        getObjectTran.setCommand("ObjectUnsubmit");
        getObjectTran.setParams(getObjectTransParams);

        getObjectRequestParams.setTransactions(getObjectTran);

        //执行添加
        String info=getConnection(getObjectRequestParams.getUrl(),getObjectRequestParams.getSip_sign(),getObjectRequestParams.getSip_appkey(),
                getObjectRequestParams.getAppSecret(),getObjectRequestParams.getSip_timestamp(),
                getObjectRequestParams.getTransactions());


        System.out.println(info.toString());
        return  info;

    }


    //删除一个记录的信息
    public  static  String  delObject(List<String> drplist,HashMap<String,Object> hashmap){
        GetOrSubOrUnSubRequestParams getObjectRequestParams=new GetOrSubOrUnSubRequestParams(drplist);
        GetOrSubOrUnSubRequestParams.GetObjectTran getObjectTran= getObjectRequestParams.new GetObjectTran();
        GetOrSubOrUnSubRequestParams.GetObjectTran.GetObjectTransParams getObjectTransParams=
                getObjectTran.new GetObjectTransParams();
        for(String key:hashmap.keySet()){
            if(key.equals("table")){
                getObjectTransParams.setTable(hashmap.get(key).toString());
            }else  if(key.equals("id")){
                getObjectTransParams.setId((Integer)hashmap.get(key));
            }
        }

        getObjectTran.setId(112);
        getObjectTran.setCommand("ObjectDelete");
        getObjectTran.setParams(getObjectTransParams);
        getObjectRequestParams.setTransactions(getObjectTran);
        //执行添加
        String info=getConnection(getObjectRequestParams.getUrl(),getObjectRequestParams.getSip_sign(),getObjectRequestParams.getSip_appkey(),
                getObjectRequestParams.getAppSecret(),getObjectRequestParams.getSip_timestamp(),
                getObjectRequestParams.getTransactions());
        System.out.println("delObject信息:......"+info.toString());
        return  info;
    }


    //网络请求
    public static String getConnection(String url,String sip_sign,String sip_appkey,String appSecret,String sip_timestamp,
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
//    //获取一个对象(vip)的信息
//    public static String getObject(HashMap<String,Object> hashmap){
//        GetObjectRequestParams getObjectRequestParams=new GetObjectRequestParams();
//        GetObjectRequestParams.GetObjectTran getObjectTran= getObjectRequestParams.new GetObjectTran();
//        GetObjectRequestParams.GetObjectTran.GetObjectTransParams getObjectTransParams=
//                getObjectTran.new GetObjectTransParams();
//        for(String key:hashmap.keySet()){
//            if(key.equals("table")){
//                getObjectTransParams.setTable(hashmap.get(key).toString());
//            }else  if(key.equals("id")){
//                getObjectTransParams.setId((Integer)hashmap.get(key));
//            }else if(key.equals("reftables")){
//                getObjectTransParams.setReftables(new org.json.JSONArray(hashmap.get(key)));
//            }
//        }
//        getObjectTran.setId(112);
//        getObjectTran.setCommand("GetObject");
//        getObjectTran.setParams(getObjectTransParams);
//        getObjectRequestParams.setTransactions(getObjectTran);
//        //执行添加
//        String info=getConnection(getObjectRequestParams.getSip_sign(),getObjectRequestParams.getSip_appkey(),
//                getObjectRequestParams.getAppSecret(),getObjectRequestParams.getSip_timestamp(),
//                getObjectRequestParams.getTransactions());
//        return  info;
//    }


}
