package cc.creamcookie.config.security;

import cc.creamcookie.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

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
public class CCSLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String accept = request.getHeader("Accept");
        log.info("Accept: {}", accept);
        if (Utils.isJsonProducesRequest(accept)) {
            log.info("isJsonProducesRequest");

            String accessToken = request.getHeader("AccessToken");
            log.info("accessToken: {}", accessToken);
            if (StringUtils.isNotEmpty(accessToken)) {
                response.setHeader("AccessToken", null);
            }

            String targetUrl = determineTargetUrl(request, response);
            if (response.isCommitted()) {
                logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            }
        } else {
            super.onLogoutSuccess(request, response, authentication);
        }
    }
}
