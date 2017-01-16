package com.bizvane.ishop.network.drpapi.burgeon.requestparam;


import org.json.JSONException;
import org.json.JSONObject;

public class ProcessOrderRequestParams extends  RequestParams {

	public ProcessOrderRequestParams(String corpcode) {
		super(corpcode);
	}

	public class ProcessOrderTrans extends  BaseTrans{

	//	private String command = "ProcessOrder";
		private JSONObject params;

		public class ProcessOrderTransParams {

			private JSONObject masterobj;
			private JSONObject detailobjs;
			private String pre_actions;
			private String post_actions;
			private String submit;

			public JSONObject getMasterobj() {
				return masterobj;
			}

			public void setMasterobj(JSONObject masterobj) {
				this.masterobj = masterobj;
			}

			public JSONObject getDetailobjs() {
				return detailobjs;
			}

			public void setDetailobjs(JSONObject detailobjs) {
				this.detailobjs = detailobjs;
			}

			public String getPre_actions() {
				return pre_actions;
			}

			public void setPre_actions(String pre_actions) {
				this.pre_actions = pre_actions;
			}

			public String getPost_actions() {
				return post_actions;
			}

			public void setPost_actions(String post_actions) {
				this.post_actions = post_actions;
			}

			public String getSubmit() {
				return submit;
			}

			public void setSubmit(String submit) {
				this.submit = submit;
			}

		}


		public JSONObject getParams() {
			return params;
		}

		public void setParams(ProcessOrderTransParams params) throws JSONException {

			JSONObject JSONParams = new JSONObject();
			JSONParams.put("masterobj", params.masterobj);
			JSONParams.put("detailobjs", params.detailobjs);
			JSONParams.put("pre_actions", params.pre_actions);
			JSONParams.put("post_actions", params.post_actions);
			JSONParams.put("submit", params.submit);

			this.params = JSONParams;
		}


	}


}
