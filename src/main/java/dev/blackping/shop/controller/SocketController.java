package dev.blackping.shop.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import dev.blackping.shop.service.SocketService;

@Controller
public class SocketController {
	@Autowired
	SocketService socketService;
	
	@GetMapping(value="/chatting")
	public String socketMain(HttpServletResponse res, HttpSession session) {
		
		return "chatting";
	}
	
	@PostMapping(value="/chatting/{namespace}")
	public void restapi(@PathVariable String namespace, HttpServletRequest req, HttpServletResponse res) {
		socketService.restkey(namespace);
	}
}
