package client;

/** Datos que necesita cada solicitud: URL, body y una forma opcional de autenticacion. */
public class RequestInfo {
    private final String url;
    private final String body;
    private final String email;
    private final String password;
    private final String token;

    public RequestInfo(String url, String body) {
        this(url, body, null, null, null);
    }

    private RequestInfo(String url, String body, String email, String password, String token) {
        this.url = url;
        this.body = body;
        this.email = email;
        this.password = password;
        this.token = token;
    }

    public static RequestInfo withBasicAuth(String url, String body, String email, String password) {
        return new RequestInfo(url, body, email, password, null);
    }

    public static RequestInfo withToken(String url, String body, String token) {
        return new RequestInfo(url, body, null, null, token);
    }

    public String getUrl() { return url; }
    public String getBody() { return body; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getToken() { return token; }
}
