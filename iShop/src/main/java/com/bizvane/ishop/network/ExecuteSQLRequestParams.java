package com.bizvane.ishop.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteSQLRequestParams {

	private String transactions; // 一个transaction里的多个操作将全部成功，或全部失败

	public class ExecuteSQLTrans {
		private int id;
		private String command = "ExecuteSQL";
		private JSONObject params;

		public class ExecuteSQLTransParams {
			private String name;
			private JSONArray values;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public JSONArray getValues() {
				return values;
			}

			public void setValues(JSONArray values) {
				this.values = values;
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

		public void setParams(ExecuteSQLTransParams params) throws JSONException {

			JSONObject JSONParams = new JSONObject();

			JSONParams.put("name", params.name);
			JSONParams.put("values", params.values);

			this.params = JSONParams;
		}
	}



	public String getTransactions() {
		return transactions;
	}

	public void setTransactions(ExecuteSQLTrans transaction) throws JSONException {

		JSONArray jSONTransactions = new JSONArray();
		JSONObject JSONTransaction = new JSONObject();

		JSONTransaction.put("id", transaction.id);
		JSONTransaction.put("command", transaction.command);
		JSONTransaction.put("params", transaction.params);

		jSONTransactions.put(JSONTransaction);

		this.transactions = jSONTransactions.toString();

	}
}
