package client;

import io.restassured.response.Response;

public class RequestPut implements IRequest {
    @Override
    public Response send(RequestInfo info) {
        return RequestSupport.prepare(info).when().put(info.getUrl());
    }
}
