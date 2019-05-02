package cc.creamcookie.config.security;

import cc.creamcookie.IAppConfig;
import cc.creamcookie.security.service.SecurityProvider;
import cc.creamcookie.security.service.UserDetailsServiceImpl;
import cc.creamcookie.security.dto.Permission;
import com.hectorlopezfernandez.pebble.springsecurity.SpringSecurityExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProvider securityProvider;
    private final AuthSuccessHandler authSuccessHandler;
    private final AuthFailureHandler authFailureHandler;

    @Autowired(required = false)
    private IAppConfig config;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**",
                "/js/**",
                "/static-bundle/**",
                "/i18n/**",
                "/webfonts/**",
                "/assets/**",
                "/fonts/**",
                "/img/**",
                "/favicon.ico",
                "/webfonts",
                "/error");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/login")
                .usernameParameter("signname")
                .passwordParameter("password")
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler);

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry perms = http.authorizeRequests();
        if (config != null && config.getSecurityRules().size() > 0) {
            for (Permission permission : config.getSecurityRules()) {
                switch (permission.getMethod()) {
                    case PERMIT_ALL:
                        perms.antMatchers(permission.getUrl()).permitAll();
                        break;
                    case REQUIRE_LOGIN:
                        perms.antMatchers(permission.getUrl()).authenticated();
                        break;
                    case HAS_ROLE:
                        perms.antMatchers(permission.getUrl()).hasAnyRole(permission.getRoles());
                        break;
                }
            }
        }
        perms.anyRequest().permitAll();

        http.logout().invalidateHttpSession(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/");

        String appName = "webapp";
        if (config != null && config.getName() != null) appName = config.getName();

        http.rememberMe()
                .key(appName)
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(86400);

        http.csrf().requireCsrfProtectionMatcher(new CsrfMatcher());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(securityProvider);
    }

    @Bean
    public Extension securityExtension() {
        return new SpringSecurityExtension();
    }

    @Configuration
    @ConditionalOnMissingBean(name = "passwordEncoder")
    public static class PasswordConfiguration {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }
    }

    @Configuration
    @ConditionalOnMissingBean(name = "userDetailsService")
    public static class UserDetailsServiceConfiguration {
        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailsServiceImpl();
        }
    }

    private class CsrfMatcher implements RequestMatcher {
        @Override
        public boolean matches(HttpServletRequest request) {
            if (!request.getMethod().equals("POST") ||
                    request.getRequestURL().indexOf("/lambda") >= 0 ||
                    request.getRequestURL().indexOf("/api") >= 0)
                return false;
            return true;
        }
    }

}
