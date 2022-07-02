package com.greenrent.exeption.message;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;


// Oluşan exception'ları throw ettiğimiz zaman cliente gönderilecek message için custom bir error oluşturduk.
public class ApiResponseError {

	private HttpStatus status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timeStamp;

	private String message;
	
	private String requestURI;

	private ApiResponseError() {
		timeStamp = LocalDateTime.now();
	}

	public ApiResponseError(HttpStatus status) {
		this();
		this.status = status;
	}
	
	public ApiResponseError(HttpStatus status, String message, String requestURI) {
		this(status);
		this.message= message;
		this.requestURI=requestURI;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	
	

}
