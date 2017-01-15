package com.bizvane.ishop.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModifyObjectRequestParams {

	private String transactions; // 一个transaction里的多个操作将全部成功，或全部失败

	public class ModifyObjectTrans{
		private String id;
		private String command;
		private JSONObject params;

		public String getId() {
			return id;
		}

		public void setId(String id) {
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

		public void setParams(JSONObject params) {
			this.params = params;
		}
	}



	public String getTransactions() {
		return transactions;
	}

	public void setTransactions(ModifyObjectTrans transaction) throws JSONException {
		JSONArray jSONTransactions = new JSONArray();
		JSONObject JSONTransaction = new JSONObject();

		JSONTransaction.put("id", transaction.id);
		JSONTransaction.put("command", transaction.command);
		JSONTransaction.put("params", transaction.params);

		jSONTransactions.put(JSONTransaction);

		this.transactions = jSONTransactions.toString();

	}

}
