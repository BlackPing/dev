package dev.blackping.shop.controller;

import java.io.IOException;
import java.net.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import dev.blackping.shop.listener.ConstructListener;
import dev.blackping.shop.service.DataService;
import dev.blackping.shop.util.HttpUtil;

@Controller
public class DataController {
	
	@Autowired
	DataService ds;
	
	@PostMapping(value="/cert")
	public void cert(HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			if(HttpUtil.ParameterAss(session, req, "nickname")) {
				res.getWriter().write(ds.cert(session, req.getParameter("nickname")));
			} else {
				res.getWriter().write(ConstructListener.Status.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@PostMapping(value="/topicroom")
	public void topicroom(HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			if(HttpUtil.ParameterAss(session, req, "roomname")) {
				res.getWriter().write(ds.topicRoom(session, req.getParameter("roomname")));
			} else {
				res.getWriter().write(ConstructListener.Status.toString());
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@PostMapping(value="/topicsl")
	public void topicselect(HttpSession session, HttpServletResponse res) {
		try {
			if(session.getAttribute("SESSION_OBJECT") == null) {
				res.getWriter().write(ConstructListener.Status.toString());
			} else {
				res.getWriter().write(ds.topicSelect());
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}