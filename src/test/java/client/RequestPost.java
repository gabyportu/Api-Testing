package client;

import io.restassured.response.Response;

public class RequestPost implements IRequest {
    @Override
    public Response send(RequestInfo info) {
        return RequestSupport.prepare(info).when().post(info.getUrl());
    }
}
