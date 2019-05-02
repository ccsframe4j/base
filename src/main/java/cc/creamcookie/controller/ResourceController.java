package cc.creamcookie.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Controller
public class ResourceController {

	@RequestMapping("/static-bundle/**")
	public String staticBundle(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

		String tpl = "none";
		String url = request.getRequestURL().toString();
		url = url.substring(url.indexOf("static-bundle/") + 14);

		try {
			List<String> json = Files.readAllLines(Paths.get(this.getClass().getResource("/static-bundles.json").toURI()), Charset.defaultCharset());
			JSONObject object = JSONObject.fromObject(StringUtils.join(json, "").replaceAll("\\s+", " ").trim());

			JSONArray bundles = object.getJSONArray("bundles");
			for (Object o : bundles) {
				JSONObject bundle = (JSONObject) o;
				if (!bundle.getString("name").endsWith(url)) continue;
				tpl = "static-bundles/" + bundle.getString("type");

				if (bundle.getString("type").equals("js")) {
					response.setContentType("text/javascript");
				}
				else {
					response.setContentType("text/" + bundle.getString("type"));
				}

				List<String> files = new ArrayList<String>();
				for (Object str : bundle.getJSONArray("files")) {
					String val = str.toString();
					if (val.startsWith("../")) {
						val = request.getContextPath() + val.substring(2);
					}
					else if (val.startsWith("/")) {
						val = request.getContextPath() + val;
					}
					else {
						val = request.getContextPath() + "/css/" + val;
					}
					files.add(val);
				}
				model.addAttribute("files", files);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		return tpl;
	}

	@RequestMapping("/i18n/{msg}.js")
	public String status(HttpServletRequest request, HttpServletResponse response, ModelMap model, @PathVariable("msg") String msg) {

		String tpl = "shared/none";

		try {
			InputStream input = this.getClass().getResource("/i18n/" + msg + ".properties").openStream();

			Properties properties = new Properties();
			properties.load(new InputStreamReader(input, Charset.forName("UTF-8")));

			model.addAttribute("props", properties);

			response.setContentType("text/javascript");
			response.setCharacterEncoding("UTF-8");
			tpl = "static-bundles/commons";
		}
		catch(Exception ex) {
			response.setStatus(404);
		}

		return tpl;
	}


}
