package com.issuerMs.common;

import java.util.Date;

public class UpdateFailedException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String message;
	
	public UpdateFailedException(Date date,String message) {
		
		this.date = date;
		this.message = message;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
