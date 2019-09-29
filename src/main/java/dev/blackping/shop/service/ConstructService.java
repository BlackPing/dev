package dev.blackping.shop.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import dev.blackping.shop.dao.AutoDAOInterface;
import dev.blackping.shop.util.Mybatis;

@Service
public class ConstructService {
	@Autowired
	AutoDAOInterface adi;
	
	public static HashMap<String, HashMap<String, WebSocketSession>> SocketServer = new HashMap<String, HashMap<String, WebSocketSession>>();
	
	@PostConstruct
	public void ConstructSQL() {
		List<HashMap<String, Object>> SocketList = Mybatis.findList(adi.sql("SL", "socket", "select", null));
		String key = "";
		HashMap<String, WebSocketSession> SocketMap;
		for(int i = 0; i < SocketList.size(); i++) {
			SocketMap = new HashMap<String, WebSocketSession>();
			key = SocketList.get(i).get("NO").toString();
			SocketServer.put(key, SocketMap);
		}
		System.out.println("Socket Server Construct");
	}
}
