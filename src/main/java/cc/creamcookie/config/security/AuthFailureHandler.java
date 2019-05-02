package cc.creamcookie.config.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException arg2)
            throws IOException, ServletException {

        String qs = req.getQueryString();
        if (qs == null || !qs.contains("error=true")) qs = "error=true" + (qs == null ? "" : "&" + qs);

        if (arg2.getClass() == BadCredentialsException.class) {
            req.setAttribute("signname", req.getParameter("signname"));
        }
        req.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", arg2);
        req.getRequestDispatcher("/login?" + qs).forward(req, res);
    }

}

