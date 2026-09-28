package vn.spring.pkd.config;

public class Endpoints {
    public static final String endpoint_fe = "http://localhost:3000";
    public static final String[] PUBLIC_GET_ENDPOINT = {
            "/products",
            "/products/*",


    };
    public static final String[] PUBLIC_POST_ENDPOINT = {
            "/auth/login",
            "/auth/introspect",
            "/auth/login/v2",
            "/users/register"
    };
    public static final String[] PUBLIC_DELETE_ENDPOINT = {

    };
    public static final String[] PUBLIC_PUT_ENDPOINT = {

    };

}