package com.bizvane.ishop.network.drpapi.burgeon.requestparam;

import org.json.JSONException;
import org.json.JSONObject;

public class SendSMSRequestParams extends  RequestParams {

	public SendSMSRequestParams(String corpcode) {
		super(corpcode);
	}

	public class SendSMSTrans extends  BaseTrans{

		private JSONObject params;

		public class SendSMSTransParams {
			private  String phone;

			public String getPhone() {
				return phone;
			}

			public void setPhone(String phone) {
				this.phone = phone;
			}

			public String getContent() {
				return content;
			}

			public void setContent(String content) {
				this.content = content;
			}

			public String getSendtime() {
				return sendtime;
			}

			public void setSendtime(String sendtime) {
				this.sendtime = sendtime;
			}

			private String content;
			private String sendtime;



		}

		public JSONObject getParams() {
			return params;
		}

		public void setParams(SendSMSTransParams params) throws JSONException {

			JSONObject JSONParams = new JSONObject();

		//	JSONParams.put("table", params.table);
			JSONParams.put("phone ", params.getPhone());
			JSONParams.put("content", params.getContent());
			JSONParams.put("sendtime", params.getSendtime());

			this.params = JSONParams;
		}
	}




}
