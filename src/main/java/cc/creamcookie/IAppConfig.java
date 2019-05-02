package cc.creamcookie;

import cc.creamcookie.security.dto.Permission;

import java.util.List;

public interface IAppConfig {

    String getName();

    List<Permission> getSecurityRules();

}
