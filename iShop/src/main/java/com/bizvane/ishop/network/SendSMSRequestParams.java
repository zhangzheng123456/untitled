package com.bizvane.ishop.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SendSMSRequestParams {

	private String transactions; // 一个transaction里的多个操作将全部成功，或全部失败

	public class SendSMSTrans {
		private int id;

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		private String command;
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

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
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



	public String getTransactions() {
		return transactions;
	}

	public void setTransactions(SendSMSTrans transaction) throws JSONException {

		JSONArray jSONTransactions = new JSONArray();
		JSONObject JSONTransaction = new JSONObject();

		JSONTransaction.put("id", transaction.id);
		JSONTransaction.put("command", transaction.command);
		JSONTransaction.put("params", transaction.params);

		jSONTransactions.put(JSONTransaction);

		this.transactions = jSONTransactions.toString();

	}
}
