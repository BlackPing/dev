package dev.blackping.shop.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import dev.blackping.shop.listener.ConstructListener;
import net.sf.json.JSONObject;

public class HttpUtil {
	
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
	
	public static boolean ParameterAss(HttpServletRequest req, String... parameters) {
		boolean flag = true;
		
		for(String str : parameters) {
			if(!flag) break;

			if(req.getParameter(str) == null) {
				flag = false;
			} else if("".equals(req.getParameter(str))) {
				flag = false;
			}
		}
		
		return flag;
	}
	
	public static JSONObject ErrorMsg(String msgName) {
		JSONObject BufferMap = new JSONObject();
		BufferMap.put("status", false);
		BufferMap.put("msg", ConstructListener.Message.get(msgName).toString());
		return BufferMap;
	}
}
