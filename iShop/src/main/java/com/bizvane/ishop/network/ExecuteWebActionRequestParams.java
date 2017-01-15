package com.bizvane.ishop.network;

import com.bizvane.ishop.utils.CryptUtil;
import com.bizvane.ishop.utils.TimeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteWebActionRequestParams {

	private String sip_sign; // 签名，使用sip_appkey+sip_timestamp+appSecret进行MD5哈希运算，结果为32位长字符串，全部小写，服务器需要校验此值
	private String appSecret; // 密钥，即为系统用户名对应密码的MD5哈希码，32位长全部小写
	private String sip_appkey; // 应用程序编号,即为系统用户名
	private String sip_timestamp; // 服务请求时间戳(yyyy-mm-dd
									// hh:mm:ss.xxx)，支持毫秒，若系统不能产生毫秒，必须补足内容，如使用.000
	private String transactions; // 一个transaction里的多个操作将全部成功，或全部失败

	public class ExecuteWebActionTrans {
		private int id;
		private String command = "ExecuteWebAction";
		private JSONObject params;

		public class ExecuteWebActionTransParams {
			private String webaction;
			private String id;
			
			public String getWebaction() {
				return webaction;
			}
			public void setWebaction(String webaction) {
				this.webaction = webaction;
			}
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}

			
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		// public String getCommand() {
		// return command;
		// }
		//
		// public void setCommand(String command) {
		// this.command = command;
		// }

		public JSONObject getParams() {
			return params;
		}

		public void setParams(ExecuteWebActionTransParams params) throws JSONException {

			JSONObject JSONParams = new JSONObject();

			JSONParams.put("webaction", params.webaction);
			JSONParams.put("id", params.id);

			this.params = JSONParams;
		}
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

	public String getTransactions() {
		return transactions;
	}

	public void setTransactions(ExecuteWebActionTrans transaction) throws JSONException {

		JSONArray jSONTransactions = new JSONArray();
		JSONObject jSONTransaction = new JSONObject();

		jSONTransaction.put("id", transaction.id);
		jSONTransaction.put("command", transaction.command);
		jSONTransaction.put("params", transaction.params);

		jSONTransactions.put(jSONTransaction);

		this.transactions = jSONTransactions.toString();

	}
}
