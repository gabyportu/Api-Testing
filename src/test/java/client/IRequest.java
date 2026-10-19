package client;

import io.restassured.response.Response;

/** Contrato comun para ejecutar una solicitud HTTP. */
public interface IRequest {
    Response send(RequestInfo info);
}
