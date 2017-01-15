package com.bizvane.ishop.network;

import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteWebActionRequestParams  extends  RequestParams{

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


}
