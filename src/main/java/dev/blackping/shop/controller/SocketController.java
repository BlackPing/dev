package dev.blackping.shop.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SocketController {
	@GetMapping(value="/1")
	public String test(HttpServletResponse res, HttpSession session) {
		session.setAttribute("Name", "test");
		System.out.println(session.getAttribute("Name"));
		return "echo1";
	}
}
