package dev.blackping.shop.controller;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins="*")
public class TestController {
	@PostMapping("/session")
	public HashMap<String, Object> sessionCreate(HttpSession session) {
		System.out.println("세션만들러옴");
		System.out.println(session.getId());
		session.setAttribute("test", "todo");
		System.out.println(session.getAttribute("test"));
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("state", true);
		return resultMap;
	}
}
