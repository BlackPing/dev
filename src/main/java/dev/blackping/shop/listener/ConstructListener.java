package dev.blackping.shop.listener;

import net.sf.json.JSONObject;

public class ConstructListener {
	public static JSONObject Status = new JSONObject();
	public ConstructListener() {
		Status.put("status", false);
		System.out.println("status false put");
	}
}
