package com.greenrent.exeption;

//@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImageFileException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImageFileException (String message) {
		super(message); 
	}
}
