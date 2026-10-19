package client;

import io.restassured.response.Response;

public class RequestDelete implements IRequest {
    @Override
    public Response send(RequestInfo info) {
        return RequestSupport.prepare(info).when().delete(info.getUrl());
    }
}
