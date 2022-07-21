package com.greenrent.exeption;

//@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExcelReportException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExcelReportException (String message) {
		super(message); 
	}
}
