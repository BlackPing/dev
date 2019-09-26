package dev.blackping.shop.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.blackping.shop.dao.AutoDAOInterface;

@Controller
public class ViewController {
	
	@GetMapping(value="/")
	public String home() {
		return "home";
	}
	
	@Autowired
	AutoDAOInterface adi;
	
	@RequestMapping(value="/tests")
	public void aaaa(HttpServletResponse res) {
		try {
			res.getWriter().write(adi.sql("SO", "login", "test", null).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
