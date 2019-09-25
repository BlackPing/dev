package dev.blackping.shop.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DataController {
	
	@PostMapping(value="/cert")
	public void cert(HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		System.out.println(req.getParameter("test"));
		try {
			res.getWriter().write("{\"test\": \"tott\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}