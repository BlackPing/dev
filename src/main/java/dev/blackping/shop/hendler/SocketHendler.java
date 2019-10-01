package dev.blackping.shop.hendler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static HashMap<String, HashMap<String, Object>> UserMap;
	
	public SocketHendler() {
		UserMap = new HashMap<String, HashMap<String, Object>>();
		System.out.println(ConstructService.SocketServer.toString());
		System.out.println("Socket Server ON");
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("Connect Session ID : " + session.getId());
		
		if(SessionCheck(session)) {
			Map<String, Object> sessionMap = session.getAttributes();
			HashMap<String, Object> BufferMap = new HashMap<String, Object>();
			HashMap<String, Object> CheckMap;
			
			SessionObject sessionObject = (SessionObject) sessionMap.get("SESSION_OBJECT");
			
			
			System.out.println(sessionMap.toString());
			if(!UserMap.isEmpty()) {
				
				boolean flag = true;
				for(String key : UserMap.keySet()) {
					CheckMap = UserMap.get(key);
					
					if(CheckMap.get("session").toString().equals(sessionObject.getId())) {
						flag = false;
						break;
					}
				}
				
				BufferMap.put("room", "");
				BufferMap.put("session", sessionObject.getId());
				UserMap.put(session.getId(), BufferMap);
				if(!flag) {  // 동시 접속자 close 처리
					JSONObject messageMap = new JSONObject();
					messageMap.put("type", "error");
					messageMap.put("msg", "다른 브라우저에서 실행중입니다.");
					session.sendMessage(new TextMessage(messageMap.toString()));
				}
			} else { // 서버 처음 켜지고 처음 입장할때만 발동
				BufferMap.put("room", "");
				BufferMap.put("session", sessionObject.getId());
				UserMap.put(session.getId(), BufferMap);
			}
			
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		System.out.println("Send Check");
		System.out.println(ConstructService.SocketServer.toString());
		if(SessionCheck(session)) {
			Map<String, Object> sessionMap = session.getAttributes();
			Map<String, Object> ParamMap = new HashMap<String, Object>();
			HashMap<String, Object> BufferMap = new HashMap<String, Object>();
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
							SocketRoom.put(session.getId(), session);
							
							msg = "<div class=\"text-center color-green\" >" + sessionObject.getNickname() + " 님이 입장하셨습니다.</div>";
							messageMap = CreateMap("roomNumber", roomNumber, "nickname", sessionObject.getNickname(), "msg", msg, "type", "connect",
									"count", SocketRoom.size(), "userList", UserList(roomNumber));
							
							System.out.println(ConstructService.SocketServer.toString());
							RoomSend(SocketRoom, messageMap);
							
							System.out.println(UserMap.toString());
							BufferMap = UserMap.get(session.getId());
							BufferMap.put("room", BufferMap.get("room").toString() + roomNumber + ",");
							
							UserMap.put(session.getId(), BufferMap);
						} else {
							messageMap = CreateMap("roomNumber", roomNumber, "nickname", sessionObject.getNickname(), "msg", "구독 채널이 아닙니다.", "type", "error");
						}
						break;
						
					case "disconnect":
						SocketRoom.remove(session.getId());
						
						msg = "<div class=\"text-center color-green\" >" + sessionObject.getNickname() + " 님이 퇴장하셨습니다.</div>";
						messageMap = CreateMap("roomNumber", roomNumber, "nickname", sessionObject.getNickname(), "msg", msg, "type", "connect",
								"count", SocketRoom.size(), "userList", UserList(roomNumber));
						
						RoomSend(SocketRoom, messageMap);
						
						BufferMap = UserMap.get(session.getId());
						BufferMap.put("room", BufferMap.get("room").toString().replace(roomNumber + ",", ""));
						
						UserMap.put(session.getId(), BufferMap);
						System.out.println(UserMap.toString());
						break;
						
					case "send":
						messageMap = CreateMap("roomNumber", roomNumber, "nickname", sessionObject.getNickname(), "msg", Message, "type", "send");
						
						RoomSend(SocketRoom, messageMap);
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
		
		WebSocketSession Socket;
		Map<String, Object> sessionMap = session.getAttributes();
		SessionObject sessionObject = (SessionObject) sessionMap.get("SESSION_OBJECT");
		
		JSONObject messageMap = new JSONObject();
		
		HashMap<String, Object> BufferMap = new HashMap<String, Object>();
		BufferMap = UserMap.get(session.getId());
		String[] roomList = BufferMap.get("room").toString().split(",");
		
		System.out.println(roomList[0]);
		int Length = roomList.length;
		
		if("".equals(roomList[0])) { 
			Length = 0;
		} else {
			String msg = "<div class=\"text-center color-green\" >" + sessionObject.getNickname() + " 님이 퇴장하셨습니다.</div>";
			messageMap = CreateMap("nickname", sessionObject.getNickname(), "msg", msg, "type", "disconnect");
			
			HashMap<String, WebSocketSession> SocketRoom;
			for(int i = 0; i < Length; i++) {
				SocketRoom = ConstructService.SocketServer.get(roomList[i]);
				SocketRoom.remove(session.getId());
				
				messageMap = CreateMap("roomNumber", roomList[i], "count", SocketRoom.size(), "userList", UserList(roomList[i]));

				RoomSend(SocketRoom, messageMap);
			}
		}
		
		UserMap.remove(session.getId());
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.out.println("Send Error");
		
		JSONObject messageMap = new JSONObject();
		messageMap = CreateMap("type", "error", "msg", "전송 오류 발생");
		session.sendMessage(new TextMessage(messageMap.toString()));
	}
	
	public boolean SessionCheck(WebSocketSession session) {
		boolean flag = true;
		Map<String, Object> sessionMap = session.getAttributes();
		
		if(sessionMap.get("HTTP.SESSION.ID") == null) {
			flag = false;
		}
		
		return flag;
	}
	
	public List<String> UserList(String roomNumber) {
		HashMap<String, WebSocketSession> SocketRoom = ConstructService.SocketServer.get(roomNumber);
		
		WebSocketSession Socket;
		SessionObject sessionObject;
		
		List<String> UserList = new ArrayList<String>();
		for(String key : SocketRoom.keySet()) { // 접속 유저 리스트
			Socket = (WebSocketSession) SocketRoom.get(key);
			sessionObject = (SessionObject) Socket.getAttributes().get("SESSION_OBJECT");
			UserList.add(sessionObject.getNickname());
		}
		
		return UserList;
	}
	
	public JSONObject CreateMap(Object... str) {
		JSONObject JSON = new JSONObject();
		
		if(str.length % 2 == 1) {
			return JSON;
		}

		int index = 0;
		int Check = 0;
		String Key = "";
		Object Value = "";
		boolean put = false;
		
		for(Object s : str) {
			Check = index % 2;
			
			if(Check == 0) {
				Key = s.toString();
			} else {
				Value = s;
				put = true;
			}
			
			if(put) {
				put = false;
				JSON.put(Key, Value);
			}
			
			index++;
		}
		return JSON;
	}
	
	public void RoomSend(HashMap<String, WebSocketSession> SocketRoom, JSONObject messageMap) throws IOException {
		WebSocketSession Socket;
		
		for(String key : SocketRoom.keySet()) {
			Socket = (WebSocketSession) SocketRoom.get(key);
			Socket.sendMessage(new TextMessage(messageMap.toString()));
		}
	}
}
