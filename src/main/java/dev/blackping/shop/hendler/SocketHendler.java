package dev.blackping.shop.hendler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class SocketHendler extends TextWebSocketHandler {
	private List<WebSocketSession> sessionList;
	
	public SocketHendler() {
		sessionList = new ArrayList<WebSocketSession>();
		System.out.println("Socket Server ON");
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Map<String, Object> sessionMap = session.getAttributes();
		System.out.println(sessionMap.toString());
		System.out.println("누군가 들어옴 : " + session.getId());
		sessionList.add(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		System.out.println("메세지 들어옴");
		for (WebSocketSession sess : sessionList) {
			sess.sendMessage(new TextMessage(session.getId() + " : " + message.getPayload()));
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("연결 끊김 : " + session.getId());
		sessionList.remove(0);
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.out.println("전송 오류 발생");
	}
	
	
}
