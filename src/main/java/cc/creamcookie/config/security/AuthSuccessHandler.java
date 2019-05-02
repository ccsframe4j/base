package cc.creamcookie.config.security;

import cc.creamcookie.security.entity.BaseAccount;
import cc.creamcookie.security.entity.BaseAccountRepository;
import cc.creamcookie.security.dto.SignDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired private BaseAccountRepository accountRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

		String uri = request.getParameter("redirect_uri");
		if (uri != null && !uri.isEmpty()) {
            getRedirectStrategy().sendRedirect(request, response, uri);
            return;
		}

		SignDetails details = (SignDetails) authentication.getPrincipal();
		if (details != null) {
			BaseAccount account = accountRepository.findById(details.getLong("id")).orElse(null);
			if (account != null) {
//				if (account.getState() != State.READY && account.getActivateKey() != null) {
//					account.setActivateKey(null);
//				}
				account.setLastSignedAt(LocalDateTime.now());
				account = accountRepository.save(account);
			}
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}

}
