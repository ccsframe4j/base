package cc.creamcookie.config.security;

import cc.creamcookie.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author eomjeongjae
 * @since 2019-05-03
 */
@Slf4j
@Component
public class CCSAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public CCSAuthenticationEntryPoint(@Nullable String loginFormUrl) {
        super(loginFormUrl == null ? "/login" : loginFormUrl);
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String accept = request.getHeader("Accept");
        log.info("Accept: {}", accept);
        if (Utils.isJsonProducesRequest(accept)) {
            log.info("isJsonProducesRequest");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        } else {
            super.commence(request, response, authException);
        }
    }
}
