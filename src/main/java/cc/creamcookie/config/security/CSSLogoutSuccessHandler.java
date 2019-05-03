package cc.creamcookie.config.security;

import cc.creamcookie.utils.Utils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author eomjeongjae
 * @since 2019-05-03
 */
public class CSSLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String accept = request.getHeader("Accept");
        if (Utils.isJsonProducesRequest(accept)) {
            String targetUrl = determineTargetUrl(request, response);
            if (response.isCommitted()) {
                logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            }
        } else {
            super.onLogoutSuccess(request, response, authentication);
        }
    }
}
