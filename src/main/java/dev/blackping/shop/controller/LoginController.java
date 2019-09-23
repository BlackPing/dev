package dev.blackping.shop.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
	@PostMapping(value="/login")
	public HashMap<String, Object> Login(HttpServletResponse res) {
		
		return null;
	}
	
	@GetMapping(value="/")
	public void test(HttpServletResponse res) {
		try {
			res.getWriter().write("test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@GetMapping(value="/test")
	public void test2(HttpServletResponse res) {
		try {
			res.setContentType("application/json;");
			res.getWriter().write("{\"test\": \"test2\"}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
