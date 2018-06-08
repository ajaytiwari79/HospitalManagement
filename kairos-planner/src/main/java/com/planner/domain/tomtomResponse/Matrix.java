package com.planner.domain.tomtomResponse;

/**
 * @author pradeep
 * @date - 8/6/18
 */

public class Matrix {
    private int statusCode;
    private Response response;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
