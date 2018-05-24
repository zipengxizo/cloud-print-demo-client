package com.trackmeeasy.cloudprint;

public class Response {

    private String responseType;

    private String responseMessage;

    public Response() {
    }

    public Response(String responseType, String responseMessage) {
    	this.responseType = responseType;
        this.responseMessage = responseMessage;
    }

	public String getResponseType() {
		return responseType;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

}
