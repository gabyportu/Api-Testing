package client;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/** Prepara headers, body y autenticacion para mantener simples los cuatro verbos HTTP. */
final class RequestSupport {
    private RequestSupport() { }

    static RequestSpecification prepare(RequestInfo info) {
        RequestSpecification request = given().accept("application/json");

        if (info.getToken() != null && !info.getToken().isBlank()) {
            request.header("Token", info.getToken());
        } else if (info.getEmail() != null && info.getPassword() != null) {
            request.auth().preemptive().basic(info.getEmail(), info.getPassword());
        }

        if (info.getBody() != null && !info.getBody().isBlank()) {
            request.contentType("application/json").body(info.getBody());
        }

        return request;
    }
}
