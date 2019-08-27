package com.inspiration.inspirationrewards.JSON;

@SuppressWarnings("serial")
public class InvalidResponseCode extends Exception {
	private int responseCode = 0;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	InvalidResponseCode(int code) {
		super("Invalid Response code " + code);
		responseCode = code;
	}

}