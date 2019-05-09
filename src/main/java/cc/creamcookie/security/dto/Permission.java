package cc.creamcookie.security.dto;

import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.List;

@Getter
public class Permission {

    private HttpMethod httpMethod;
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

    public Permission(HttpMethod httpMethod, String url, String role) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.method = Method.HAS_ROLE_WITH_HTTP_METHOD;
        this.roles = new String[]{ role };
    }

    public Permission(HttpMethod httpMethod, String url, String[] roles) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.method = Method.HAS_ROLE_WITH_HTTP_METHOD;
        this.roles = roles;
    }

    public Permission(HttpMethod httpMethod, String url, List<String> roles) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.method = Method.HAS_ROLE_WITH_HTTP_METHOD;
        this.roles = (String[]) roles.toArray();
    }

    public static enum Method {
        PERMIT_ALL,
        REQUIRE_LOGIN,
        HAS_ROLE,
        HAS_ROLE_WITH_HTTP_METHOD
    }

}
