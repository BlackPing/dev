package dev.blackping.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
	
	@GetMapping(value="/")
	public String home() {
		return "home";
	}
	
	@GetMapping(value="/1")
	public String echo() {
		return "echo1";
	}
}
