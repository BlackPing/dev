package dev.blackping.shop.service;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import dev.blackping.shop.dao.AutoDAOInterface;
import dev.blackping.shop.object.SessionObject;
import dev.blackping.shop.util.Mybatis;
import net.sf.json.JSONObject;

@Service
public class DataService {
	@Autowired
	AutoDAOInterface adi;
	
	// DataAccessException 처리해야함
	
	public String cert(HttpSession session, String nickname) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap = adi.sql("SO", "login", "nickname-check", nickname);
			
			if(Integer.parseInt(Mybatis.findMap(resultMap).get("count").toString()) > 0) {
				resultMap.put("msg", "이미 존재하는 닉네임입니다.");
				resultMap.put("status", false);
			} else {
				HashMap<String, Object> paramsMap = new HashMap<String, Object>();
				SessionObject sessionObject = (SessionObject)session.getAttribute("SESSION_OBJECT");
				paramsMap.put("no", sessionObject.getId());
				paramsMap.put("nickname", nickname);
				HashMap<String, Object> check = adi.sql("UD", "login", "nickname-update", paramsMap);
				if(Mybatis.findInt(check) > 0) {
					sessionObject.setNickname(nickname);
					sessionObject.setPower(1);
					session.setAttribute("SESSION_OBJECT", sessionObject);
					resultMap.put("msg", "닉네임 인증에 성공했습니다.");
					resultMap.put("status", true);
				} else {
					resultMap.put("status", false);
				}
			}
		} catch(DataAccessException e) {
			resultMap.put("status", false);
		}
			
		return JSONObject.fromObject(resultMap).toString();
	}
	
	public String topicRoom(HttpSession session, String roomname) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		
		try {
			SessionObject sessionObject = (SessionObject)session.getAttribute("SESSION_OBJECT");
			paramsMap.put("no", sessionObject.getId());
			paramsMap.put("roomname", roomname);
			
			int count = 0;
			count = Mybatis.findInt(adi.sql("SO", "topic", "topic-count", sessionObject.getId()));
			
			if(count < 5) {
				count = Mybatis.findInt(adi.sql("IS", "topic", "room-insert", paramsMap));
				if(count > 0) {
					session.setAttribute("SESSION_OBJECT", sessionObject);
					resultMap.put("msg", "구독 채널이 추가됐습니다.");
					resultMap.put("status", true);
					HashMap<String, WebSocketSession> SocketMap = new HashMap<String, WebSocketSession>();
					ConstructService.SocketServer.put(Mybatis.findString(adi.sql("SO", "topic", "room-all", null)), SocketMap);
				} else {
					resultMap.put("status", false);
				}
			} else {
				resultMap.put("status", false);
				resultMap.put("msg", "구독 채널은 5개까지 만들 수 있습니다.");
			}
		} catch(DataAccessException e) {
			resultMap.put("status", false);
		}
		
		return JSONObject.fromObject(resultMap).toString();
	}
	
	public String topicSelect(HttpSession session, String search) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		HashMap<String, Object> BufferMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> BufferList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> ParamMap = new HashMap<String, Object>();
		HashMap<String, Object> createMap = new HashMap<String, Object>();
		
		try {
			if(!search.isEmpty()) BufferMap.put("search", search);
			resultMap.put("select", Mybatis.findList(adi.sql("SL", "topic", "room-select", BufferMap)));
			if(session.getAttribute("SESSION_OBJECT") != null) {
				SessionObject sessionObject = (SessionObject)session.getAttribute("SESSION_OBJECT");
				BufferMap.put("id", sessionObject.getId());
				BufferList = Mybatis.findList(adi.sql("SL", "topic", "room-topic", BufferMap));
				
				String key = "";
				for(int i = 0; i < BufferList.size(); i++) {
					key = BufferList.get(i).get("TOPIC_NO").toString();
					ParamMap.put(key, true);
				}
				
				BufferList = Mybatis.findList(adi.sql("SL", "topic", "room-edit", BufferMap));
				key = "";
				for(int i = 0; i < BufferList.size(); i++) {
					key = BufferList.get(i).get("NO").toString();
					createMap.put(key, true);
				}
			}
			resultMap.put("topic", ParamMap);
			resultMap.put("create", createMap);
			resultMap.put("status", true);
		} catch(DataAccessException e) {
			e.printStackTrace();
			resultMap.put("status", false);
		}
		
		return JSONObject.fromObject(resultMap).toString();
	}
	
	public String topicDaynamicOne(HttpSession session) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		HashMap<String, Object> BufferMap = new HashMap<String, Object>();
		HashMap<String, Object> ParamMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> BufferList = new ArrayList<HashMap<String, Object>>();
		try {
			SessionObject sessionObject = (SessionObject)session.getAttribute("SESSION_OBJECT");
			ParamMap.put("id", sessionObject.getId());
			BufferList = Mybatis.findList(adi.sql("SL", "topic", "room-topic", ParamMap));
			resultMap.put("topic", BufferList);
			
			String key;
			for(int i = 0; i < BufferList.size(); i++) {
				key = BufferList.get(i).get("TOPIC_NO").toString();
				BufferMap.put(key, ConstructService.SocketServer.get(key).size());
			}
			
			resultMap.put("account", BufferMap);
		} catch(DataAccessException e) {
			resultMap.put("status", false);
		}
		
		return JSONObject.fromObject(resultMap).toString();
	}
	
	public String topicUp(HttpSession session, String param_no) {
		HashMap<String, Object> ParamMap = new HashMap<String, Object>();
		HashMap<String, Object> BufferMap = new HashMap<String, Object>();
		
		SessionObject sessionObject = (SessionObject)session.getAttribute("SESSION_OBJECT");
		
		ParamMap.put("id", sessionObject.getId().toString());
		ParamMap.put("topic_no", param_no);
		
		try {
			
			int count = 0;
			
			count = Mybatis.findInt(adi.sql("SO", "login", "power", sessionObject.getId()));
			
			if(count > 0) {
				count = Mybatis.findInt(adi.sql("SO", "topic", "room-check", ParamMap));
				if(count > 0) { // 구독중
					adi.sql("KILL", "topic", "topic-kill", ParamMap);
					BufferMap.put("msg", "채널 구독을 취소했습니다.");
				} else { // 구독중 아님
					adi.sql("IS", "topic", "topic-add", ParamMap);
					BufferMap.put("msg", "채널을 구독했습니다.");
				}
				
				BufferMap.put("status", true);
			} else {
				BufferMap.put("status", false);
				BufferMap.put("msg", "닉네임 인증이 필요합니다.");
			}
		} catch (DataAccessException e) {
			BufferMap.put("status", false);
		}
		
		System.out.println(BufferMap.toString());
		
		return JSONObject.fromObject(BufferMap).toString();
	}
	
	public String topicDel(HttpSession session, String param_no) {
		HashMap<String, Object> BufferMap = new HashMap<String, Object>();
		HashMap<String, Object> ParamMap = new HashMap<String, Object>();
		try {
			SessionObject sessionObject = (SessionObject) session.getAttribute("SESSION_OBJECT");
			BufferMap.put("id", sessionObject.getId());
			BufferMap.put("no", param_no);
			
			int Check = Mybatis.findInt(adi.sql("UD", "topic", "topic-update", BufferMap));
			if(Check > 0) {
				ParamMap.put("status", true);
				ParamMap.put("msg", "구독 채널을 삭제했습니다.");
			} else {
				ParamMap.put("status", false);
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		return JSONObject.fromObject(ParamMap).toString();
	}
}
