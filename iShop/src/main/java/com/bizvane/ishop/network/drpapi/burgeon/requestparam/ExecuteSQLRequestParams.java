package com.bizvane.ishop.network.drpapi.burgeon.requestparam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteSQLRequestParams  extends RequestParams {

	public ExecuteSQLRequestParams(String corpcode) {
		super(corpcode);
	}

	public class ExecuteSQLTrans extends BaseTrans{

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



}
