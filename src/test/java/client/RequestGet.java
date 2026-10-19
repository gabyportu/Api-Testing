package client;

import io.restassured.response.Response;

public class RequestGet implements IRequest {
    @Override
    public Response send(RequestInfo info) {
        return RequestSupport.prepare(info).when().get(info.getUrl());
    }
}
