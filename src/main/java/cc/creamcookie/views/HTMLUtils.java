package cc.creamcookie.views;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HTMLUtils {

	private HttpServletRequest request;
	public HTMLUtils(HttpServletRequest request) {
		this.request = request;
	}

	public String selected(Object data1, Object data2) {
		return this.htmlattr(data1, data2, "selected=\"selected\"", false);
	}

	public String checked(Object data1, Object data2) {
		return this.htmlattr(data1, data2, "checked=\"checked\"", false);
	}

	public String selected(Object data1, Object data2, boolean def) {
		return this.htmlattr(data1, data2, "selected=\"selected\"", def);
	}

	public String checked(Object data1, Object data2, boolean def) {
		return this.htmlattr(data1, data2, "checked=\"checked\"", def);
	}


	@SuppressWarnings("rawtypes")
	public String htmlattr(Object data1, Object data2, String attr, boolean def) {
		if (data1 == null || data2 == null) {
			return (def) ? attr : "";
		}

		String str1 = data1.toString().trim();

		if (data2 instanceof Collection) {
			List<String> arr = new ArrayList<String>();
			for (Object o : (Collection) data2) {
				arr.add(o.toString().trim().toLowerCase());
			}
			return arr.contains(str1.toLowerCase()) ? attr : "";
		}
		else {

			String str2 = data2.toString().trim();

			if (str1.isEmpty() || str2.isEmpty()) {
				return (def) ? attr : "";
			}

			boolean isnull = false;
			boolean contains = (str2.subSequence(0, 1).equals("%"));
			if (contains) str2 = str2.substring(1);

			if (str1.subSequence(0, 1).equals("$")) {
				str1 = this.param(str1.substring(1));
				if (str1 == null) {
					str1 = data1.toString();
					isnull = true;
				}
			}

			if ( ( contains ? str1.contains(str2) : str1.equals(str2) ) || (isnull && def) ) {
				return attr;
			}
			else {
				return "";
			}

		}

	}

	public String val(List<String> vals, String v) {
		return vals.get(Integer.parseInt(v));
	}

	public String param(String key) {
		if (request.getParameterValues(key) == null) return null;
		return org.apache.commons.lang.StringUtils.join(request.getParameterValues(key), ",");
	}
}
