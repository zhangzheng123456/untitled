package com.bizvane.ishop.network;

import com.bizvane.ishop.utils.CryptUtil;
import com.bizvane.ishop.utils.TimeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QueryRequestParams {

	private String sip_sign; // 签名，使用sip_appkey+sip_timestamp+appSecret进行MD5哈希运算，结果为32位长字符串，全部小写，服务器需要校验此值
	private String appSecret; // 密钥，即为系统用户名对应密码的MD5哈希码，32位长全部小写
	private String sip_appkey; // 应用程序编号,即为系统用户名
	private String sip_timestamp; // 服务请求时间戳(yyyy-mm-dd
									// hh:mm:ss.xxx)，支持毫秒，若系统不能产生毫秒，必须补足内容，如使用.000
	private String transactions; // 一个transaction里的多个操作将全部成功，或全部失败

	public class QueryTrans {
		private int id;

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		private String command;
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

	public String getSip_sign() {
		return sip_sign;
	}

	public void setSip_sign() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(sip_appkey);
		stringBuilder.append(sip_timestamp);
		stringBuilder.append(appSecret);
		try {
			this.sip_sign = CryptUtil.encryptMD5Hash(stringBuilder.toString());
		} catch (Exception e) {
			this.sip_sign = "encryptMD5 Wrong";
			e.printStackTrace();
		}
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		try {
			this.appSecret = CryptUtil.encryptMD5Hash(appSecret);
		} catch (Exception e) {
			this.appSecret = "encryptMD5 Wrong";
			e.printStackTrace();
		}
	}

	public String getSip_appkey() {
		return sip_appkey;
	}

	public void setSip_appkey(String sip_appkey) {
		this.sip_appkey = sip_appkey;
	}

	public String getSip_timestamp() {
		return sip_timestamp;
	}

	public void setSip_timestamp() {

		this.sip_timestamp = TimeUtils.getTimeWithMS(System.currentTimeMillis());
	}

	public String getTransactions() {
		return transactions;
	}

	public void setTransactions(QueryTrans transaction) throws JSONException {

		JSONArray jSONTransactions = new JSONArray();
		JSONObject JSONTransaction = new JSONObject();

		JSONTransaction.put("id", transaction.id);
		JSONTransaction.put("command", transaction.command);
		JSONTransaction.put("params", transaction.params);

		jSONTransactions.put(JSONTransaction);

		this.transactions = jSONTransactions.toString();

	}
}
