package cc.creamcookie;

import cc.creamcookie.security.dto.Permission;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;

import java.util.List;

public interface IAppConfig {

    String getName();

    List<Permission> getSecurityRules();

    SecurityConfigurerAdapter getSecurityConfigurer();

}