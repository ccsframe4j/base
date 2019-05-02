package cc.creamcookie.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CookieHelper {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private static Map<String, Cookie> _cookies = null;

	public CookieHelper() {
		this(null);
	}

	public CookieHelper(HttpServletResponse response) {
//		this.request = CCSApplication.getCurrentRequest();
		this.response = response;
		if (_cookies == null) _cookies = new HashMap<String, Cookie>();
	}

	public String getCookie(String key) {

		StringBuilder sb = new StringBuilder();

		try {

			Cookie cookie = null;
			Cookie[] cookies = request.getCookies();
			if(cookies != null)
				for(Cookie item : cookies) {
					sb.append(item.getName() + " : " + item.getValue() + "\n");
					if (item == null || item.getName() == null || !item.getName().trim().equals(key.trim())) continue;
					cookie = item;
					break;
				}

			if (cookie == null && _cookies != null) {
				if (_cookies.containsKey(key)) {
					cookie = _cookies.get(key);
				}
				else {
					throw new Exception("not found cookie for " + key);
				}
			}

			return URLDecoder.decode(cookie.getValue(), "UTF-8");
		}
		catch(Exception ex) {
			return null;
		}
	}

	public void setCookie(String key, String value) throws Exception {
		this.setCookie(key, value, -1);
	}

	public void setCookie(String key, String value, int expiry) throws Exception {
		this.setCookie(key, value, expiry, false);
	}


	public void setCookie(String key, String value, int expiry, boolean secure) throws Exception {

		if (response == null) throw new Exception();

		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for(Cookie item : cookies) {
				if (!item.getName().equals(key)) continue;
				cookie = item;
			}
		}

		String newValue = value;
		if (value != null && !value.isEmpty()) newValue = URLEncoder.encode(value, "UTF-8");

		if(cookie == null)  {
			cookie = new Cookie(key, newValue);
		}

		cookie.setSecure(secure);
		cookie.setValue(newValue);
		cookie.setPath("/");
		cookie.setMaxAge(expiry);

		response.addCookie(cookie);
		_cookies.put(cookie.getName(), cookie);
	}
}
