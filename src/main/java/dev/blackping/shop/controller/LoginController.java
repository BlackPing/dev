package dev.blackping.shop.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dev.blackping.shop.util.HttpUtil;

@Controller
public class LoginController {
	String redirect_url = "http://dev.blackping.shop/kakaoback";
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public void Login(HttpServletResponse res) {
		String url = "";
		try {
			url = HttpUtil.getOauth("authorize", "1976e916cf04c3a6a22e4e8d06e05c50", URLEncoder.encode(redirect_url, "UTF-8"), "response_type=code");
			res.sendRedirect("https://accounts.kakao.com/login?continue=" + URLEncoder.encode(url, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/kakaoback", method=RequestMethod.POST)
	public void token() {
		
	}
}
