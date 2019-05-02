package cc.creamcookie.security.service;

import cc.creamcookie.security.dto.SignDetails;
import com.mitchellbosecke.pebble.boot.autoconfigure.PebbleAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Locale;

@Component
public class SecurityProvider implements AuthenticationProvider {

	@Autowired private UserDetailsService userDetailsService;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private MessageSource messageSource;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();

		Collection<? extends GrantedAuthority> authorities;

		Locale locale = LocaleContextHolder.getLocale();

		SignDetails user = (SignDetails) userDetailsService.loadUserByUsername(username);
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException(messageSource.getMessage("security.bad_credentials", null, locale));
		}

		authorities = user.getAuthorities();
		return new UsernamePasswordAuthenticationToken(user, password, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
        return true;
	}

}
