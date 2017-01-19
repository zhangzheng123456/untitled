package com.bizvane.ishop.network.drpapi.burgeon.requestparam;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.entity.ParamConfigure;
import com.bizvane.ishop.service.imp.CorpParamServiceImpl;
import com.bizvane.ishop.service.imp.ParamConfigureServiceImpl;
import com.bizvane.ishop.utils.CryptUtil;
import com.bizvane.ishop.utils.SpringBeanFactoryUtils;
import com.bizvane.ishop.utils.TimeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by yanyadong on 2017/1/15.
 */
public class RequestParams  {

   // public static final String appkey="nea@burgeon.com.cn";
  //  public static final String appsecret="lifecycle";
    private String sip_sign; // 签名，使用sip_appkey+sip_timestamp+appSecret进行MD5哈希运算，结果为32位长字符串，全部小写，服务器需要校验此值
    private String appSecret; // 密钥，即为系统用户名对应密码的MD5哈希码，32位长全部小写
    private String sip_appkey; // 应用程序编号,即为系统用户名
    private String sip_timestamp; // 服务请求时间戳(yyyy-mm-dd hh:mm:ss.xxx)，支持毫秒，若系统不能产生毫秒，必须补足内容，如使用.000
    private String transactions; // 一个transaction里的多个操作将全部成功，或全部失败
    private  String url;  //服务器地址


    public  RequestParams(String corpcode) {

        connectionDRP(corpcode);
    }


    public  String  connectionDRP(String corpcode) {
        //安正
        try {
            if (corpcode.equals("C10016")) {


//            setSip_appkey("nea@burgeon.com.cn");
//            setAppSecret("lifecycle");
//            setSip_timestamp();
//            setSip_sign();
//            setUrl("http://60.190.132.114:8080/servlets/binserv/Rest");

            ParamConfigureServiceImpl paramConfigureService = (ParamConfigureServiceImpl) SpringBeanFactoryUtils.getBean("paramConfigureServiceImpl");
            CorpParamServiceImpl corpParamService = (CorpParamServiceImpl) SpringBeanFactoryUtils.getBean("corpParamServiceImpl");
            ParamConfigure param = paramConfigureService.getParamByKey(CommonValue.CRM_DB_ACCOUNT, Common.IS_ACTIVE_Y);
            List<CorpParam> corpParams = corpParamService.selectByCorpParam(corpcode, String.valueOf(param.getId()), Common.IS_ACTIVE_Y);


                if (corpParams.size() > 0) {
                String value = corpParams.get(0).getParam_value();
                String[] paramvalues = value.split("§§");
                setSip_appkey(paramvalues[1].toString());
                setAppSecret(paramvalues[2].toString());
                setSip_timestamp();
                setSip_sign();
                setUrl(paramvalues[0].toString());
            }
       }
    }catch(Exception e){
        e.printStackTrace();
    }

        return "ddd";
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSip_sign() {
        return sip_sign;
    }

    public void setSip_sign() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sip_appkey);
        stringBuilder.append(sip_timestamp);
        stringBuilder.append(appSecret);
        try {
            this.sip_sign = CryptUtil.encryptMD5Hash(stringBuilder.toString());
        } catch (Exception e) {
            this.sip_sign = "encryptMD5 Wrong";
            e.printStackTrace();
        }
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        try {
            this.appSecret = CryptUtil.encryptMD5Hash(appSecret);
        } catch (Exception e) {
            this.appSecret = "encryptMD5 Wrong";
            e.printStackTrace();
        }
    }

    public String getSip_appkey() {
        return sip_appkey;
    }

    public void setSip_appkey(String sip_appkey) {
        this.sip_appkey = sip_appkey;
    }

    public String getSip_timestamp() {
        return sip_timestamp;
    }

    public void setSip_timestamp() {
        this.sip_timestamp = TimeUtils.getTimeWithMS(System.currentTimeMillis());
    }


    public class BaseTrans {
        private int id;
        private String command;
        private JSONObject params;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public JSONObject getParams() {
            return params;
        }

        public void setParams(JSONObject params){
            this.params = params;
        }
    }

    public String getTransactions() {
        return transactions;
    }

    public void setTransactions(BaseTrans transaction) throws JSONException {
        JSONArray JSONTransactions = new JSONArray();
        JSONObject JSONTransaction = new JSONObject();

        JSONTransaction.put("id", transaction.getId());
        JSONTransaction.put("command", transaction.getCommand());
        JSONTransaction.put("params", transaction.getParams());

        JSONTransactions.put(JSONTransaction);

        this.transactions = JSONTransactions.toString();
    }


}
