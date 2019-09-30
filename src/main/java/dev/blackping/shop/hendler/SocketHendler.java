package dev.blackping.shop.hendler;

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
			String sessionValue = sessionMap.get("HTTP.SESSION.ID").toString();
			System.out.println(sessionMap.toString());
			if(!UserMap.isEmpty()) {
				
				boolean flag = true;
				for(String key : UserMap.keySet()) {
					CheckMap = UserMap.get(key);
					
					if(CheckMap.get("session").toString().equals(sessionValue)) {
						flag = false;
						break;
					}
				}
				
				BufferMap.put("room", "");
				BufferMap.put("session", sessionValue);
				UserMap.put(session.getId(), BufferMap);
				if(!flag) {  // 동시 접속자 close 처리
					JSONObject messageMap = new JSONObject();
					messageMap.put("type", "error");
					messageMap.put("msg", "다른 브라우저에서 실행중입니다.");
					session.sendMessage(new TextMessage(messageMap.toString()));
				}
			} else { // 서버 처음 켜지고 처음 입장할때만 발동
				BufferMap.put("room", "");
				BufferMap.put("session", sessionValue);
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
							
							System.out.println(UserMap.toString());
							BufferMap = UserMap.get(session.getId());
							BufferMap.put("room", BufferMap.get("room").toString() + roomNumber + ",");
							
							UserMap.put(session.getId(), BufferMap);
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
						
						BufferMap = UserMap.get(session.getId());
						BufferMap.put("room", BufferMap.get("room").toString().replace(roomNumber + ",", ""));
						
						UserMap.put(session.getId(), BufferMap);
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
			messageMap.put("nickname", sessionObject.getNickname());
			messageMap.put("msg", msg);
			messageMap.put("type", "disconnect");
			
			HashMap<String, WebSocketSession> SocketRoom;
			for(int i = 0; i < Length; i++) {
				System.out.println("DIS CON!!!!!!");
				
				SocketRoom = ConstructService.SocketServer.get(roomList[i]);
				SocketRoom.remove(session.getId());
				
				messageMap.put("roomNumber", roomList[i]);
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
			}
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
