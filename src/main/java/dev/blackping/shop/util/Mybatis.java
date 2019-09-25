package dev.blackping.shop.util;

import java.util.HashMap;

public class Mybatis {
	
	// AutoDAO find result Map
	public static HashMap<String, Object> findMap(HashMap<String, Object> map) {
		if(map.isEmpty()) {
			return new HashMap<String, Object>();
		} else {
			return (HashMap<String, Object>) map.get("result");
		}
	}
}