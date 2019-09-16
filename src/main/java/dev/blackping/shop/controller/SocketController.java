package dev.blackping.shop.controller;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SocketController {
	
	
//	public static List<?> SocketList = new ArrayList<ArrayList<WebSocketSession>>();
	@RequestMapping(value = "/key1", method = RequestMethod.GET)
	public String key1(Locale locale, Model model, HttpSession hsession) {
		hsession.setAttribute("key", 1);
		return "echo";
	}
	
	@RequestMapping(value = "/key2", method = RequestMethod.GET)
	public String key2(Locale locale, Model model, HttpSession hsession) {
		hsession.setAttribute("key", 2);
		return "echo";
	}
	
	@RequestMapping(value = "/1", method = RequestMethod.GET)
	public String echo1(Locale locale, Model model) {
		return "echo1";
	}
	
	@RequestMapping(value = "/2", method = RequestMethod.GET)
	public String echo2(Locale locale, Model model) {
		return "echo2";
	}
}
