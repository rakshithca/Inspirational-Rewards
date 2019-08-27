package com.inspiration.inspirationrewards.JSON;

import org.apache.http.Header;

public class ResponseHolder {

	private Header header;
	private String responseStr;
	private int responseCode;

	public String getResponseStr() {
		return responseStr;
	}

	public void setResponseStr(String responseStr) {
		this.responseStr = responseStr;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

}