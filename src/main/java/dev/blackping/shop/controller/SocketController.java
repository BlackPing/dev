package dev.blackping.shop.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SocketController {
	@GetMapping(value="/1")
	public String test(HttpServletResponse res) {
		return "echo1";
	}
}
