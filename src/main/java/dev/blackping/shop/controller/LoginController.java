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

import dev.blackping.shop.DAO.AutoDAOInterface;
import dev.blackping.shop.bean.UserBean;
import dev.blackping.shop.util.HttpUtil;
import net.sf.json.JSONObject;

@Controller
public class LoginController {
	String redirect_url = "http://127.0.0.1:8080/kakaoback";
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
				
				String nickname = jobj.get("nickname").toString();
				String profile_image = jobj.get("profile_image").toString();
				System.out.println(jobj.toString());
				
				UserBean ub = new UserBean(Integer.parseInt(id), nickname, profile_image, 0);
				HashMap<String, Object> resultMap = adi.sql("SO", "login", "user-select", ub);
				System.out.println(resultMap.get("count").toString());
				
//				session.setMaxInactiveInterval(21600);
//				session.setAttribute("id", id);
//				session.setAttribute("nickname", nickname);
//				session.setAttribute("profile_image", profile_image);
			}
			
			res.sendRedirect("/");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
