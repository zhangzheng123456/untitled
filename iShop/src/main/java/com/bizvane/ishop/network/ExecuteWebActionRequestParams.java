package com.bizvane.ishop.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteWebActionRequestParams  extends  RequestParams{

	private String transactions; // 一个transaction里的多个操作将全部成功，或全部失败

	public class ExecuteWebActionTrans  extends BaseTrans{

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

	public void setTransactions(BaseTrans transaction) throws JSONException {

		JSONArray jSONTransactions = new JSONArray();
		JSONObject jSONTransaction = new JSONObject();

		jSONTransaction.put("id", transaction.getId());
		jSONTransaction.put("command", transaction.getCommand());
		jSONTransaction.put("params", transaction.getParams());

		jSONTransactions.put(jSONTransaction);

		this.transactions = jSONTransactions.toString();

	}
}
