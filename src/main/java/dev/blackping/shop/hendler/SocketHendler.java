package dev.blackping.shop.hendler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import dev.blackping.shop.dao.AutoDAOInterface;
import dev.blackping.shop.object.SessionObject;
import dev.blackping.shop.service.ConstructService;
import dev.blackping.shop.util.Mybatis;
import net.sf.json.JSONObject;

public class SocketHendler extends TextWebSocketHandler {
	@Autowired
	AutoDAOInterface adi;
	
	private static HashMap<String, String> UserMap;
	
	public SocketHendler() {
		UserMap = new HashMap<String, String>();
		System.out.println(ConstructService.SocketServer.toString());
		System.out.println("Socket Server ON");
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		if(SessionCheck(session)) {
			Map<String, Object> sessionMap = session.getAttributes();
			System.out.println("Connect Session ID : " + session.getId());
			System.out.println(sessionMap.toString());
			UserMap.put(session.getId(), "");
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		System.out.println("Send Check");
		System.out.println(ConstructService.SocketServer.toString());
		if(SessionCheck(session)) {
			Map<String, Object> sessionMap = session.getAttributes();
			Map<String, Object> ParamMap = new HashMap<String, Object>();
			WebSocketSession Socket;
			/*******************
			 *  [0] type - connect, disconnect, send
			 *  [1] type2 - room number
			 *******************/
			String Message = message.getPayload().toString();
			System.out.println("msg : " + Message);
			String[] spit =  Message.split(",");
			if(spit.length > 2) {
				String type = spit[0];
				String roomNumber = spit[1];
				int typeLength = spit[0].length() + spit[1].length() + 2;
				
				Message = Message.substring(typeLength);
				
				HashMap<String, WebSocketSession> SocketRoom = ConstructService.SocketServer.get(roomNumber);
				SessionObject sessionObject = (SessionObject) sessionMap.get("SESSION_OBJECT");
				JSONObject messageMap = new JSONObject();
				String msg = "";
				switch(type) {
					case "connect":
						ParamMap.put("id", sessionObject.getId());
						ParamMap.put("topic_no", roomNumber);
						int count = Mybatis.findInt(adi.sql("SO", "topic", "room-check", ParamMap));
						
						if(count > 0) {
							msg = "<div class=\"text-center color-green\" >" + sessionObject.getNickname() + " 님이 입장하셨습니다.</div>";
							
							SocketRoom.put(session.getId(), session);
							messageMap.put("roomNumber", roomNumber);
							messageMap.put("nickname", sessionObject.getNickname());
							messageMap.put("msg", msg);
							messageMap.put("type", "connect");
							messageMap.put("count", SocketRoom.size());
							
							List<String> UserList = new ArrayList<String>();
							for(String key : SocketRoom.keySet()) { // 접속 유저 리스트
								Socket = (WebSocketSession) SocketRoom.get(key);
								sessionObject = (SessionObject) Socket.getAttributes().get("SESSION_OBJECT");
								UserList.add(sessionObject.getNickname());
							}
							
							messageMap.put("userList", UserList);
							
							System.out.println(ConstructService.SocketServer.toString());
							for(String key : SocketRoom.keySet()) { // 구독 채널  Send
								Socket = (WebSocketSession) SocketRoom.get(key);
								Socket.sendMessage(new TextMessage(messageMap.toString()));
							}
							
							UserMap.put(session.getId(), UserMap.get(session.getId()).toString()  + roomNumber + ",");
						} else {
							session.sendMessage(new TextMessage("구독 채널이 아닙니다."));
						}
						break;
						
					case "disconnect":
						msg = "<div class=\"text-center color-green\" >" + sessionObject.getNickname() + " 님이 퇴장하셨습니다.</div>";
						
						SocketRoom.remove(session.getId());
						messageMap.put("roomNumber", roomNumber);
						messageMap.put("nickname", sessionObject.getNickname());
						messageMap.put("msg", msg);
						messageMap.put("type", "disconnect");
						messageMap.put("count", SocketRoom.size());
						
						
						List<String> UserList = new ArrayList<String>();
						for(String key : SocketRoom.keySet()) { // 접속 유저 리스트
							Socket = (WebSocketSession) SocketRoom.get(key);
							sessionObject = (SessionObject) Socket.getAttributes().get("SESSION_OBJECT");
							UserList.add(sessionObject.getNickname());
						}
						
						messageMap.put("userList", UserList);
						
						for(String key : SocketRoom.keySet()) {
							Socket = (WebSocketSession) SocketRoom.get(key);
							Socket.sendMessage(new TextMessage(messageMap.toString()));
						}
						
						UserMap.put(session.getId(), UserMap.get(session.getId()).toString().replace(roomNumber + ",", ""));
						System.out.println(UserMap.toString());
						break;
						
					case "send":
						messageMap.put("roomNumber", roomNumber);
						messageMap.put("nickname", sessionObject.getNickname());
						messageMap.put("msg", Message);
						messageMap.put("type", "send");
						
						for(String key : SocketRoom.keySet()) {
							Socket = (WebSocketSession) SocketRoom.get(key);
							Socket.sendMessage(new TextMessage(messageMap.toString()));
						}
						break;
						
					default:
						break;
				}
			}
			
			
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("DisConnect Session ID : " + session.getId());
		String[] roomList = UserMap.get(session.getId()).split(",");
		System.out.println(roomList[0]);
		int Length = roomList.length;
		
		if("".equals(roomList[0])) Length = 0;
		
		for(int i = 0; i < Length; i++) {
			ConstructService.SocketServer.get(roomList[i]).remove(session.getId());
		}
		UserMap.remove(session.getId());
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.out.println("전송 오류 발생");
	}
	
	public boolean SessionCheck(WebSocketSession session) {
		boolean flag = true;
		Map<String, Object> sessionMap = session.getAttributes();
		
		if(sessionMap.get("HTTP.SESSION.ID") == null) {
			flag = false;
		}
		
		return flag;
	}
}
