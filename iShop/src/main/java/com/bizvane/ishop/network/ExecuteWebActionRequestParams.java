package com.bizvane.ishop.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteWebActionRequestParams {

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
