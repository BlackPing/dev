package dev.blackping.shop.hendler;

import java.util.*;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class SocketHendler extends TextWebSocketHandler {
	private HashMap<String, WebSocketSession> UserMap;
	
	public SocketHendler() {
		UserMap = new HashMap<String, WebSocketSession>();
		System.out.println("Socket Server ON");
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Map<String, Object> sessionMap = session.getAttributes();
		System.out.println(sessionMap.toString());
		System.out.println("누군가 들어옴 : " + session.getId());
		UserMap.put(session.getId(), session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		System.out.println("메세지 들어옴");
		
		for (String key : UserMap.keySet()) {
			WebSocketSession Socket = (WebSocketSession) UserMap.get(key);
			Socket.sendMessage(new TextMessage(session.getId() + " : " + message.getPayload()));
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("연결 끊김 : " + session.getId());
		UserMap.remove(session.getId());
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.out.println("전송 오류 발생");
	}
	
	
}
