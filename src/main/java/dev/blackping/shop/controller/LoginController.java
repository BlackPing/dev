package dev.blackping.shop.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import dev.blackping.shop.bean.UserBean;
import dev.blackping.shop.dao.AutoDAOInterface;
import dev.blackping.shop.object.SessionObject;
import dev.blackping.shop.util.HttpUtil;
import dev.blackping.shop.util.Mybatis;
import net.sf.json.JSONObject;

@Controller
public class LoginController {
//	String redirect_url = "http://socket.com:8080/kakaoback";
	String redirect_url = "http://dev.blackping.shop/kakaoback";
	String Rest_Key = "1976e916cf04c3a6a22e4e8d06e05c50";
	
	@PostMapping(value="/login")
	public void Login(HttpServletResponse res) {
		String url = "";
		try {
			url = HttpUtil.getOauth("authorize", Rest_Key, URLEncoder.encode(redirect_url, "UTF-8"), "response_type=code");
			res.sendRedirect("https://accounts.kakao.com/login?continue=" + URLEncoder.encode(url, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Autowired
	AutoDAOInterface adi;
	
	@GetMapping(value="/kakaoback")
	public void kakaoback(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		HashMap<String, Object> httpMap = new HashMap<String, Object>();
		try {
			String code = req.getParameter("code");
			String token_url = HttpUtil.getOauth("token", Rest_Key, URLEncoder.encode(redirect_url, "UTF-8"), "code=" + code, "grant_type=authorization_code");
			
			httpMap = HttpUtil.kakaoApi(token_url);
			
			String userInfo_url = "https://kapi.kakao.com/v2/user/me";
			userInfo_url += "?access_token=" + httpMap.get("access_token").toString();
			
			boolean check = Boolean.valueOf(httpMap.get("status").toString());
			if(check) {
				
				session.setAttribute("access_token", httpMap.get("access_token").toString());
				httpMap = HttpUtil.kakaoApi(userInfo_url);
				
				String id = httpMap.get("id").toString();
				
				JSONObject jobj = JSONObject.fromObject(httpMap.get("properties"));
				System.out.println(jobj.toString());
				String nickname = jobj.get("nickname").toString();
				String profile_image = "";
				
				UserBean ub = new UserBean(Integer.parseInt(id), nickname, profile_image, 0);
				HashMap<String, Object> resultMap = adi.sql("SO", "login", "user-select", ub);
				int Count = Integer.parseInt(Mybatis.findMap(resultMap).get("count").toString());
				
				session.setMaxInactiveInterval(21600);
				if(Count == 0) {
					adi.sql("IS", "login", "user-insert", ub);
					session.setAttribute("SESSION_OBJECT", new SessionObject(id, nickname, 0));
				} else {
					resultMap = adi.sql("SO", "login", "user-login", ub);
					nickname = Mybatis.findMap(resultMap).get("NICKNAME").toString();
					int power = Integer.parseInt(Mybatis.findMap(resultMap).get("POWER").toString());
					session.setAttribute("SESSION_OBJECT", new SessionObject(id, nickname, power));
				}
			}
			
			res.sendRedirect("/");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@PostMapping(value="/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}
