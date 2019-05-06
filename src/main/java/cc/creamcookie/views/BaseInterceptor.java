package cc.creamcookie.views;

import cc.creamcookie.http.CookieHelper;
import cc.creamcookie.http.UAgentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class BaseInterceptor implements HandlerInterceptor {

    @Autowired
    private Environment env;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        uri = uri.substring(request.getContextPath().length());
        if (uri.startsWith("/files/")) return true;

        response.setHeader("X-Location", uri);

        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        request.setAttribute("nowtime", LocalDateTime.now());
        request.setAttribute("html", new HTMLUtils(request));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        if (modelAndView == null) return;

        Boolean isSplit = env.getProperty("pebble.split-view", Boolean.class);
        if (isSplit) {

            ModelMap model = modelAndView.getModelMap();
            if (model.get("layout") == null)
                model.addAttribute("layout", "shared/layout.default");

            String view = modelAndView.getViewName();
            if (!view.startsWith("/")) {
                String viewname = "defaults/" + view;
                if (request != null) {

                    String usrAgent = request.getHeader("User-Agent");
                    UAgentInfo ua = new UAgentInfo(usrAgent, request.getHeader("Accept"));

                    String ie9 = env.getProperty("ie9-barricade");
                    if (ie9 != null && !ie9.isEmpty()) {
                        if (usrAgent != null && !usrAgent.isEmpty() && usrAgent.contains("MSIE")) {
                            String v = usrAgent.substring(usrAgent.indexOf("MSIE") + 4).trim();
                            v = v.substring(0, v.indexOf(";"));
                            if (Float.parseFloat(v) <= 9.0) {
                                modelAndView.setViewName(ie9);
                            }
                        }
                    }

                    CookieHelper cookie = new CookieHelper();
                    if (ua.isMobilePhone && !"Y".equals(cookie.getCookie("pcmode"))) {
                        model.addAttribute("mobile", true);

                        if (model.containsAttribute("layout")) model.addAttribute("layout", "shared/layout.mobile");
                        URL url = BaseInterceptor.class.getResource("/templates/mobile/" + view + ".html");
                        if (url != null) {
                            viewname = "/mobile/" + view;
                        } else {
                            viewname = "/mobile/index";
                        }

                        model.addAttribute("pcmode", cookie.getCookie("pcmode"));
                    }

                    modelAndView.setViewName(viewname);
                }
            }
            else if (view.startsWith("/")) {
                modelAndView.setViewName(view.substring(1));
            }
        }
    }
}
