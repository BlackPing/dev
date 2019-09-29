package dev.blackping.shop.listener;

import net.sf.json.JSONObject;

public class ConstructListener {
	public static JSONObject Status = new JSONObject();
	public static JSONObject Message = new JSONObject();
	public ConstructListener() {
		Status.put("status", false);
		Message.put("LoginCheckMsg", "로그인이 필요한 서비스 입니다.");
		System.out.println("status false put");
	}
}
