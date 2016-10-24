package com.bizvane.ishop.utils.websockect.handler;

import com.bizvane.ishop.utils.websockect.request.HttpRequest;
import com.bizvane.ishop.utils.websockect.utils.LogUtil;
import com.bizvane.ishop.utils.websockect.utils.MD5Sum;
import com.bizvane.sun.app.client.Client;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.Status;
import com.bizvane.sun.v1.common.ValueType;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class MessageHandler {
	private static Logger logger = LogUtil.getLogger(MessageHandler.class);
	
	public JSONObject getMessage(String message){
		JSONObject mh=new JSONObject();
		String ts=String.valueOf(System.currentTimeMillis());
		HashMap<String, String> params =new HashMap<String, String>();
		JSONObject obj=new JSONObject(message);
		String userId=obj.getString("FromUserName");
		params.put("args[params]", message);
		params.put("format", "JSON");
		params.put("client", "");
		params.put("ver","1.0");
		params.put("ts",ts);
		params.put("sig",this.getSign(ts,userId));
		try {
	   mh= HttpRequest.sendRequest("http://www.o2o.shoptao.cn/servlets/binserv/nds.weixinpublicparty.ext.BindDemo",params,"POST");
	   //mh=HttpRequest.sendRequest("http://192.168.1.125:8080/servlets/binserv/AdminController_send.do",params,"POST");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mh;
	}
	public String getSign(String ts,String userId){
		InputStream inputStream=this.getClass().getClassLoader().getResourceAsStream("r.properties");
		 Properties pts=new  Properties();
		 try   {    
			  pts.load(inputStream);    
			   }   catch  (IOException e1)  {    
			  e1.printStackTrace();    
			  }
		 String sign=null;
			try {
				new MD5Sum();
				sign =MD5Sum.toCheckSumStr(userId + ts+pts.getProperty("skey"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return sign;
	}

	
	public String sendMessage(String message){
		logger.info("进入sendmessage:"+message);
		String timePattern="yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
	    String date = sdf.format(new Date());
        Client client = new Client();
		DataBox dataBox;
		JSONObject obj=new JSONObject(message);
		HashMap<String, Data> dataList=new HashMap<String, Data>();
		 dataList.put("open_id",new Data("open_id",obj.optString("FromUserName"), ValueType.PARAM));
	     dataList.put("send_type",new Data("send_type","1", ValueType.PARAM));
	     dataList.put("corp_code",new Data("corp_code",obj.optString("corp_code"), ValueType.PARAM));
	     dataList.put("nickname",new Data("nickname","", ValueType.PARAM));// 微信昵称
	     dataList.put("auth_appid",new Data("auth_appid","", ValueType.PARAM));//公众号的app_id
	     dataList.put("headimgurl",new Data("headimgurl","", ValueType.PARAM));//微信头像url地址
	     dataList.put("user_id",new Data("user_id",obj.optString("ToUserName"), ValueType.PARAM));
	     dataList.put("message_date",new Data("message_date",date, ValueType.PARAM));
	     dataList.put("message_type",new Data("message_type",obj.optString("MsgType"), ValueType.PARAM));
	     if(obj.optString("MsgType").equals("image")){
	    	 dataList.put("message_content",new Data("message_content",obj.optString("PicUrl"), ValueType.PARAM));
	     }
	     else if(obj.optString("MsgType").equals("text")){
	    	 dataList.put("message_content",new Data("message_content",obj.optString("Content"), ValueType.PARAM));
	     }
	     else if(obj.optString("MsgType").equals("link")){
	    	 dataList.put("message_content",new Data("message_content",obj.optString("Url"), ValueType.PARAM));
		}
	     logger.info("message_content:"+obj.optString("Content"));
	     logger.info("datalist:"+dataList.toString());
	     logger.info("发送sendmessage");
	     dataBox = client.put(new DataBox("1", Status.ONGOING, "", "com.bizvane.sun.app.method.ChatToUser", dataList, null, null, System.currentTimeMillis()));	
	     logger.info("CaptchaMethod " + dataBox.data.get("message").value);
		return dataBox.data.get("message").value;
	}
}
