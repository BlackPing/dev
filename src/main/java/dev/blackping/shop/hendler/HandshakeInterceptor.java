package dev.blackping.shop.hendler;

import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		// TODO Auto-generated method stub
		ServletServerHttpRequest ssreq = (ServletServerHttpRequest) request;
		System.out.println(request.getURI());
		HttpServletRequest req = ssreq.getServletRequest();
		
		attributes.put("id", req.getParameter("userId"));
		return super.beforeHandshake(request, response, wsHandler, attributes);
	}
}
