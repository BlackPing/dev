package dev.blackping.shop.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.json.JSONObject;

public class HttpUtil {
	
	// AutoDAO find result Map
	public static HashMap<String, Object> findMap(HashMap<String, Object> map) {
		if(map.isEmpty()) {
			return new HashMap<String, Object>();
		} else {
			return (HashMap<String, Object>) map.get("result");
		}
	}
	
	public static String getOauth(String url, String appkey, String redirectUrl, String... getreq) {
		String path = "https://kauth.kakao.com/oauth/";
		path += url;
		path += "?client_id=" + appkey + "&redirect_uri=" + redirectUrl;
		
		for(String s : getreq) {
			path += "&" + s;
		}
		
		return path;
	}
	
	public static HashMap<String, Object> kakaoApi(String api_url) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			URL url = new URL(api_url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			int res_code = conn.getResponseCode();
			if(res_code == 200) {
				InputStream input = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(input));
				String read = "";
				String result = "";
				
				while((read = br.readLine()) != null) {
					result += read;
				}
				
				JSONObject jobj = JSONObject.fromObject(result);
				
				Iterator<?> iterator = jobj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					String value = jobj.get(key).toString();
					resultMap.put(key, value);
				}
				resultMap.put("status", true);
				
				input.close();
			} else if(res_code == 401) {
				resultMap.put("status", false);
			}
		} catch (Exception e) {
			resultMap.put("status", false);
			e.printStackTrace();
		}
		return resultMap;
	}
}
