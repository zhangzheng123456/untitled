package com.bizvane.ishop.utils.websockect.request;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;

public class HttpRequest {

	public static JSONObject sendRequest(String apiURL, Map<String, String> params, String method) throws Exception {
		JSONObject json = new JSONObject(params);
		System.out.println(json.toString());
		HttpURLConnection conn = (HttpURLConnection) new URL(apiURL).openConnection();
		conn.setConnectTimeout(6 * 1000);
		conn.setRequestMethod(method);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.connect();
		conn.getOutputStream().write(json.toString().getBytes());
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		String msg= buffer.toString();
		System.out.println("return msg-->"+msg);
		conn.disconnect();
		JSONObject obj = new JSONObject();
		obj.put("code", "a");
		obj.put("data", msg);
		return obj;
	}

	/**
	 * parse int value from <code>str</code>
	 * 
	 * @param str
	 *            normally it should be a string
	 * @param defaultValue
	 *            if errors found when parsing, this value will be returned
	 */
	public static int getInt(Object str, int defaultValue) {
		try {
			int i = (new Integer((str + "").trim())).intValue();
			return i;
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static String getChareset(String contentType) {
		int i = contentType == null ? -1 : contentType.indexOf("charset=");
		return i == -1 ? "UTF-8" : contentType.substring(i + 8);
	}

	// ����querystring
	public static String delimit(Collection<Map.Entry<String, String>> entries, boolean doEncode) throws Exception {
		if (entries == null || entries.isEmpty()) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		boolean notFirst = false;
		for (Map.Entry<String, ?> entry : entries) {
			if (notFirst) {
				buffer.append("&");
			} else {
				notFirst = true;
			}
			Object value = entry.getValue();
			if (value == null)
				value = "";
			buffer.append(doEncode ? URLEncoder.encode(entry.getKey(), "UTF8") : entry.getKey()).append("=")
					.append(doEncode ? URLEncoder.encode(value.toString(), "UTF8") : value);
		}
		return buffer.toString();
	}
}
