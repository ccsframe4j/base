package cc.creamcookie.config.security;

import cc.creamcookie.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException arg2)
            throws IOException, ServletException {
        String accept = req.getHeader("Accept");
        log.info("Accept: {}", accept);
        if (Utils.isJsonProducesRequest(accept)) {
            log.info("isJsonProducesRequest");
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        } else {
            String qs = req.getQueryString();
            if (qs == null || !qs.contains("error=true")) qs = "error=true" + (qs == null ? "" : "&" + qs);

            if (arg2.getClass() != UsernameNotFoundException.class) {
                req.setAttribute("signname", req.getParameter("signname"));
            }
            req.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", arg2);
            req.getRequestDispatcher("/login?" + qs).forward(req, res);
        }
    }

}

