package com.bizvane.ishop.network.drpapi.burgeon.requestparam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QueryRequestParams  extends  RequestParams{


    public QueryRequestParams(String corpcode) {
        super(corpcode);
    }

    public class QueryTrans extends  BaseTrans{

		private JSONObject params;

		public class QueryTransParams {
			private String table;
			private JSONArray columns;

			public JSONArray getColumn_masks() {
				return column_masks;
			}

			public void setColumn_masks(JSONArray column_masks) {
				this.column_masks = column_masks;
			}

			private JSONArray column_masks;
			private String qlcid;
			private JSONObject params;
			private int range;
			private int start;
			private String count;
			private JSONArray orderby;

			public String getTable() {
				return table;
			}

			public void setTable(String table) {
				this.table = table;
			}

			public JSONArray getColumns() {
				return columns;
			}

			public void setColumns(JSONArray columns) {
				this.columns = columns;
			}

			public String getQlcid() {
				return qlcid;
			}

			public void setQlcid(String qlcid) {
				this.qlcid = qlcid;
			}

			public JSONObject getParams() {
				return params;
			}

			public void setParams(JSONObject params) {
				this.params = params;
			}

			public int getRange() {
				return range;
			}

			public void setRange(int range) {
				this.range = range;
			}

			public int getStart() {
				return start;
			}

			public void setStart(int start) {
				this.start = start;
			}

			public String getCount() {
				return count;
			}

			public void setCount(String count) {
				this.count = count;
			}

			public JSONArray getOrderby() {
				return orderby;
			}

			public void setOrderby(JSONArray orderby) {
				this.orderby = orderby;
			}
		}


		public JSONObject getParams() {
			return params;
		}

		public void setParams(QueryTransParams params) throws JSONException {

			JSONObject JSONParams = new JSONObject();

			JSONParams.put("table", params.table);
			JSONParams.put("columns", params.columns);
			JSONParams.put("column_masks", params.column_masks);
			JSONParams.put("params", params.params);
			JSONParams.put("range", params.range);
			JSONParams.put("table", params.table);
			JSONParams.put("start", params.start);
			JSONParams.put("count", params.count);
			JSONParams.put("orderby", params.orderby);

			this.params = JSONParams;
		}
	}


}
