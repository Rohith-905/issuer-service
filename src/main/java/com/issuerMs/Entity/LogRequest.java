package com.issuerMs.Entity;

public class LogRequest {

	private String level;
	private String message;
	
	public LogRequest(String level,String message) {
		this.level = level;
		this.message = message;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	public String getLevel() {
		return level;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}
