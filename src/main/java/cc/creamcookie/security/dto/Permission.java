package cc.creamcookie.security.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Permission {

    private String url;
    private Method method;
    private String[] roles;

    public Permission(String url, Method method) {
        this.url = url;
        this.method = method;
    }

    public Permission(String url, String role) {
        this.url = url;
        this.method = Method.HAS_ROLE;
        this.roles = new String[]{ role };
    }

    public Permission(String url, String[] roles) {
        this.url = url;
        this.method = Method.HAS_ROLE;
        this.roles = roles;
    }

    public Permission(String url, List<String> roles) {
        this.url = url;
        this.method = Method.HAS_ROLE;
        this.roles = (String[]) roles.toArray();
    }

    public static enum Method {
        PERMIT_ALL,
        REQUIRE_LOGIN,
        HAS_ROLE,
    }

}
