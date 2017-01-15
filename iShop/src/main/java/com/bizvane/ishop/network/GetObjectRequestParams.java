package com.bizvane.ishop.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetObjectRequestParams  extends RequestParams{

	public class GetObjectTran extends BaseTrans{

		private JSONObject params;
		
		public class GetObjectTransParams{
			private String table;
			private int id;
			/**
			 * int[]型数组
			 */
			private JSONArray reftables;
			public String getTable() {
				return table;
			}
			public void setTable(String table) {
				this.table = table;
			}
			public int getId() {
				return id;
			}
			public void setId(int id) {
				this.id = id;
			}
			public JSONArray getReftables() {
				return reftables;
			}
			public void setReftables(JSONArray reftables) {
				this.reftables = reftables;
			}
			
		}

		public JSONObject getParams() {
			return params;
		}

		public void setParams(GetObjectTransParams params) throws JSONException {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put("table", params.table);
			jsonParams.put("id", params.id);
			jsonParams.put("reftables", params.reftables);
			this.params = jsonParams;
		}
		
	}

}
