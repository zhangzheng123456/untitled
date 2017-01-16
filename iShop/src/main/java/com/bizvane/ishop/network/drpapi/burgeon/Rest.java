package com.bizvane.ishop.network.drpapi.burgeon;

import com.bizvane.ishop.network.drpapi.burgeon.requestparam.ExecuteWebActionRequestParams;
import com.bizvane.ishop.network.drpapi.burgeon.requestparam.QueryRequestParams;
import com.bizvane.ishop.network.drpapi.burgeon.requestparam.RequestParams;
import com.bizvane.ishop.utils.HttpResult;
import com.bizvane.ishop.utils.WebConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanyadong on 2017/1/15.
 */
public class Rest {



    private static WebConnection webConnection=new WebConnection();

    public static  String url = "http://60.190.132.114:8080/servlets/binserv/Rest";



    public static String Add(String table,HashMap<String,Object> tableRow){

        RequestParams createObjectRequestParams = new RequestParams();
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
        String info=getConnection(createObjectRequestParams.getSip_sign(), createObjectRequestParams.getSip_appkey(),
                createObjectRequestParams.getAppSecret(), createObjectRequestParams.getSip_timestamp(),
                createObjectRequestParams.getTransactions());


        return info;
    }


    //查询vip(通过测试)
    public static String selectVip(HashMap<String,Object> tableRow){

        QueryRequestParams queryRequestParams = new QueryRequestParams();
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
        String info=getConnection(queryRequestParams.getSip_sign(),queryRequestParams.getSip_appkey(),
                queryRequestParams.getAppSecret(),queryRequestParams.getSip_timestamp(),
                queryRequestParams.getTransactions());

        return  info;
    }


    //修改会员(测试成功)
    public  static  String modify(String table,HashMap<String,Object> modVip){

        RequestParams modifyObjectRequestParams = new RequestParams();
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

        //查询
        String info=getConnection(modifyObjectRequestParams.getSip_sign(),modifyObjectRequestParams.getSip_appkey(),
                modifyObjectRequestParams.getAppSecret(),modifyObjectRequestParams.getSip_timestamp(),
                modifyObjectRequestParams.getTransactions());

        System.out.println(info.toString());

        return  info;
    }


    //执行WebAction查询

    public static String excuteWebaction(String webaction,int vipid){

        ExecuteWebActionRequestParams executeWebActionRequestParams = new ExecuteWebActionRequestParams();
        ExecuteWebActionRequestParams.ExecuteWebActionTrans executeWebActionTrans = executeWebActionRequestParams.new ExecuteWebActionTrans();
        ExecuteWebActionRequestParams.ExecuteWebActionTrans.ExecuteWebActionTransParams executeWebActionTransParams = executeWebActionTrans.new ExecuteWebActionTransParams();

                executeWebActionTransParams.setWebaction(webaction);

                executeWebActionTransParams.setId(vipid);

        executeWebActionTrans.setId(112);
        executeWebActionTrans.setCommand("ExecuteWebAction");
        executeWebActionTrans.setParams(executeWebActionTransParams);

        executeWebActionRequestParams.setTransactions(executeWebActionTrans);

        //执行添加
        String info=getConnection(executeWebActionRequestParams.getSip_sign(),executeWebActionRequestParams.getSip_appkey(),
                executeWebActionRequestParams.getAppSecret(),executeWebActionRequestParams.getSip_timestamp(),
                executeWebActionRequestParams.getTransactions());

        System.out.println(info);
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
