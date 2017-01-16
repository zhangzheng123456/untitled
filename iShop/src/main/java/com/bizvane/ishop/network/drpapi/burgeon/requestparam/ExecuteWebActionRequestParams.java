package com.bizvane.ishop.network.drpapi.burgeon.requestparam;

import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteWebActionRequestParams  extends RequestParams {

    public ExecuteWebActionRequestParams(String corpcode) {
        super(corpcode);
    }

    public class ExecuteWebActionTrans  extends BaseTrans{

		private JSONObject params;

		public class ExecuteWebActionTransParams {
			private String webaction;
			private int id;
			
			public String getWebaction() {
				return webaction;
			}
			public void setWebaction(String webaction) {
				this.webaction = webaction;
			}
			public int getId() {
				return id;
			}
			public void setId(int id) {
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
