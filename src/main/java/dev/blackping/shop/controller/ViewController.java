package dev.blackping.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import dev.blackping.shop.bean.TestVO;
import dev.blackping.shop.dao.AutoDAO;
import dev.blackping.shop.dao.AutoDAOInterface;

@Controller
public class ViewController {
	
	@Autowired
	AutoDAOInterface adi;
	
	@GetMapping(value="/")
	public String home() {
		return "home";
	}
	
	@GetMapping(value="/1")
	public String echo() {
		return "echo1";
	}
}
