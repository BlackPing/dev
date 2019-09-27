package dev.blackping.shop.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mybatis {
	
	// AutoDAO find result Map
	public static HashMap<String, Object> findMap(HashMap<String, Object> map) {
		if(map.isEmpty()) {
			return new HashMap<String, Object>();
		} else {
			return (HashMap<String, Object>) map.get("result");
		}
	}
	
	public static int findInt(HashMap<String, Object> map) {
		if(map.isEmpty()) {
			return 0;
		} else {
			return (int) map.get("result");
		}
	}
	
	public static ArrayList<HashMap<String, Object>> findList(HashMap<String, Object> map) {
		if(map.isEmpty()) {
			return new ArrayList<HashMap<String, Object>>();
		} else {
			return (ArrayList<HashMap<String, Object>>) map.get("result");
		}
	}
}
