package dev.blackping.shop.service;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.blackping.shop.dao.AutoDAOInterface;
import dev.blackping.shop.listener.ConstructListener;
import dev.blackping.shop.object.SessionObject;
import dev.blackping.shop.util.Mybatis;
import net.sf.json.JSONObject;

@Service
public class DataService {
	@Autowired
	AutoDAOInterface adi;
	
	// DataAccessException 처리해야함
	
	public String cert(HttpSession session, String nickname) {
		if(nickname == null) {
			return ConstructListener.Status.toString();
		} else if(nickname.equals("")) {
			return ConstructListener.Status.toString();
		}

		HashMap<String, Object> resultMap = adi.sql("SO", "login", "nickname-check", nickname);
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
				session.setAttribute("SESSION_OBJECT", sessionObject);
				resultMap.put("msg", "닉네임 인증에 성공했습니다.");
				resultMap.put("status", true);
			} else {
				resultMap.put("status", false);
			}
		}
		return JSONObject.fromObject(resultMap).toString();
	}
	
	public String topicRoom(HttpSession session, String roomname) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		SessionObject sessionObject = (SessionObject)session.getAttribute("SESSION_OBJECT");
		paramsMap.put("no", sessionObject.getId());
		paramsMap.put("roomname", roomname);
		
		HashMap<String, Object> check = adi.sql("IS", "topic", "room-insert", paramsMap);
		if(Mybatis.findInt(check) > 0) {
			session.setAttribute("SESSION_OBJECT", sessionObject);
			resultMap.put("msg", "구독 채널이 추가됐습니다.");
			resultMap.put("status", true);
		} else {
			resultMap.put("status", false);
		}
		
		return JSONObject.fromObject(resultMap).toString();
	}
	
	public String topicSelect() {
		return "test";
	}
}
