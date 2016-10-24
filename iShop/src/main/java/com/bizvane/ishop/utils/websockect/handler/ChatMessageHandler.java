package com.bizvane.ishop.utils.websockect.handler;

import com.bizvane.ishop.utils.websockect.pojo.Constants;
import com.bizvane.ishop.utils.websockect.utils.LogUtil;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatMessageHandler extends TextWebSocketHandler {
	private static Logger logger = LogUtil.getLogger(ChatMessageHandler.class);
	private static final Map<String, Object> users;// 这个会出现性能问题，最好用Map来存储，key用userid
	static {
		users = new HashMap<String, Object>();
	}

	/**
	 * 连接成功时候，会触发UI上onopen方法
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("connect to the websocket success......");
		String userName = (String) session.getAttributes().get(Constants.WEBSOCKET_USERNAME);
		users.put(userName, session);
	}

	/**
	 * 在UI在用js调用websocket.send()时候，会调用该方法
	 */
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		long currentTime=System.currentTimeMillis();
		String msg=message.getPayload().toString();
		//转发消息到后台
		MessageHandler mh = new MessageHandler();
		mh.sendMessage(msg);
		super.handleMessage(session, message);
	}

	/**
	 * 给某个用户发送消息
	 *
	 *
	 */
	public JSONObject sendMessageToUser(String messageJson,long time) throws JSONException {
		JSONObject obj=new JSONObject();
		MessageHandler mh = new MessageHandler();
		JSONObject obj1 = new JSONObject(messageJson);
		String transSign = (String) obj1.get("sig");
		String ts = (String) obj1.get("ts");
		// 获取fromusername进行本地加密
		String param = (String) obj1.get("args[params]");
		JSONObject obj2 = new JSONObject(param);
		String fromUsername = (String) obj2.get("FromUserName");
		String localSign = mh.getSign(ts, fromUsername);
		String code = null;
		String msg = null;
		String no_read = null;
		if (transSign.equals(localSign)) {
			String userId = obj2.getString("ToUserName");
			System.out.println("用户连接信息：");
			System.out.println(userId);
			WebSocketSession session = (WebSocketSession) users.get(userId);
			if(session!=null&&session.isOpen()){
				try {
					session.sendMessage(new TextMessage(param));
					code = "0000";msg = "消息发送成功!";
				} catch (IOException e) {
					e.printStackTrace();
					code = "1001";msg = "消息发送过程中出现异常!";
				}
			}else{
				code = "1002";
				msg = "当前用户不在线,将转为离线发送!";
				System.out.println("当前用户不在线!");
				
			}
		} else {
			code = "1003";
			msg = "秘钥不正确!";
		}
		obj.put("code", code);
		obj.put("msg", msg);
		return obj;
	}

	/**
	 * 给所有在线用户发送消息
	 *
	 *
	 */
	public void sendMessageToUsers(String messageJson) {
		JSONArray arr = new JSONArray(messageJson);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj = arr.getJSONObject(i);
			String userId = obj.getString("ToUserName");
			WebSocketSession session = (WebSocketSession) users.get(userId);
			if (session.isOpen()) {
				try {
					if (session.isOpen()) {
						session.sendMessage(new TextMessage(messageJson));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				String code = "当前用户不在线";
			}
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		if (session.isOpen()) {
			session.close();
		}
		String userId = (String) session.getAttributes().get(Constants.WEBSOCKET_USERNAME);
		logger.debug("websocket connection closed......");
		users.remove(userId);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		logger.debug("websocket connection closed......");
		String userId = (String) session.getAttributes().get(Constants.WEBSOCKET_USERNAME);
		users.remove(userId);
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

}
