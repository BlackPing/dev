package dev.blackping.shop.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
	@PostMapping(value="/login")
	public HashMap<String, Object> Login(HttpServletResponse res) {
		
		return null;
	}
}
