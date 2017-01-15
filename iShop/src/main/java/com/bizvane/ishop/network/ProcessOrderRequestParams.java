package com.bizvane.ishop.network;


import com.bizvane.ishop.utils.CryptUtil;
import com.bizvane.ishop.utils.TimeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProcessOrderRequestParams {

	private String sip_sign; // 签名，使用sip_appkey+sip_timestamp+appSecret进行MD5哈希运算，结果为32位长字符串，全部小写，服务器需要校验此值
	private String appSecret; // 密钥，即为系统用户名对应密码的MD5哈希码，32位长全部小写
	private String sip_appkey; // 应用程序编号,即为系统用户名
	private String sip_timestamp; // 服务请求时间戳(yyyy-mm-dd
									// hh:mm:ss.xxx)，支持毫秒，若系统不能产生毫秒，必须补足内容，如使用.000
	private String transactions; // 一个transaction里的多个操作将全部成功，或全部失败

	public class ProcessOrderTrans {
		private String id;
		private String command = "ProcessOrder";
		private JSONObject params;

		public class ProcessOrderTransParams {

			private JSONObject masterobj;
			private JSONObject detailobjs;
			private String pre_actions;
			private String post_actions;
			private String submit;

			public JSONObject getMasterobj() {
				return masterobj;
			}

			public void setMasterobj(JSONObject masterobj) {
				this.masterobj = masterobj;
			}

			public JSONObject getDetailobjs() {
				return detailobjs;
			}

			public void setDetailobjs(JSONObject detailobjs) {
				this.detailobjs = detailobjs;
			}

			public String getPre_actions() {
				return pre_actions;
			}

			public void setPre_actions(String pre_actions) {
				this.pre_actions = pre_actions;
			}

			public String getPost_actions() {
				return post_actions;
			}

			public void setPost_actions(String post_actions) {
				this.post_actions = post_actions;
			}

			public String getSubmit() {
				return submit;
			}

			public void setSubmit(String submit) {
				this.submit = submit;
			}

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

		public void setParams(ProcessOrderTransParams params) throws JSONException {

			JSONObject JSONParams = new JSONObject();
			JSONParams.put("masterobj", params.masterobj);
			JSONParams.put("detailobjs", params.detailobjs);
			JSONParams.put("pre_actions", params.pre_actions);
			JSONParams.put("post_actions", params.post_actions);
			JSONParams.put("submit", params.submit);

			this.params = JSONParams;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
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

	public void setTransactions(ProcessOrderTrans transaction) throws JSONException {

		JSONArray jSONTransactions = new JSONArray();
		JSONObject JSONTransaction = new JSONObject();

		JSONTransaction.put("id", transaction.getId());
		JSONTransaction.put("command", transaction.command);
		JSONTransaction.put("params", transaction.params);

		jSONTransactions.put(JSONTransaction);

		this.transactions = jSONTransactions.toString();

	}
}
