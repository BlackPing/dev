package dev.blackping.shop.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
	
	@GetMapping(value="/")
	public String home() {
		return "home";
	}
	
	@RequestMapping(value="/tests")
	public void aaaa(HttpServletResponse res) {
		try {
			res.getWriter().write("testest");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
