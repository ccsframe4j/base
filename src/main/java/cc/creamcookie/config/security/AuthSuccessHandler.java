package cc.creamcookie.config.security;

import cc.creamcookie.security.entity.BaseAccount;
import cc.creamcookie.security.entity.BaseAccountRepository;
import cc.creamcookie.security.dto.SignDetails;
import cc.creamcookie.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Autowired
    private BaseAccountRepository accountRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String accept = request.getHeader("Accept");
        log.info("Accept: {}", accept);
        if (Utils.isJsonProducesRequest(accept)) {
            log.info("isJsonProducesRequest");
            SavedRequest savedRequest = this.requestCache.getRequest(request, response);
            if (savedRequest == null) {
                this.clearAuthenticationAttributes(request);
            } else {
                String targetUrlParameter = this.getTargetUrlParameter();
                if (!this.isAlwaysUseDefaultTargetUrl() && (targetUrlParameter == null || !StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
                    this.clearAuthenticationAttributes(request);
                } else {
                    this.requestCache.removeRequest(request, response);
                    this.clearAuthenticationAttributes(request);
                }
            }
        } else {

            String uri = request.getParameter("redirect_uri");
            if (uri != null && !uri.isEmpty()) {
                getRedirectStrategy().sendRedirect(request, response, uri);
                return;
            }
//
//            SignDetails details = (SignDetails) authentication.getPrincipal();
//            if (details != null) {
//                BaseAccount account = accountRepository.findById(details.getLong("id")).orElse(null);
//                if (account != null) {
//                    //	if (account.getState() != State.READY && account.getActivateKey() != null) {
//                    //	    account.setActivateKey(null);
//                    //	}
//                    account.setLastSignedAt(LocalDateTime.now());
//                    account = accountRepository.save(account);
//                }
//            }
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

}
