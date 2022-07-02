package com.greenrent.exeption;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.greenrent.exeption.message.ApiResponseError;

@ControllerAdvice
public class GreenRentExceptionHandler extends ResponseEntityExceptionHandler {
	
	private ResponseEntity<Object> buildResponseEntity(ApiResponseError error){
		return new ResponseEntity<Object>(error, error.getStatus()); 
	}
	
	
	@ExceptionHandler(ResourceNotFoundExeption.class)
	protected ResponseEntity<Object> handleResourceNotFoundExeption(ResourceNotFoundExeption ex, WebRequest request){
		ApiResponseError error = new ApiResponseError(HttpStatus.NOT_FOUND, "Green Rent "+ ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@ExceptionHandler(ConflictException.class)
	protected ResponseEntity<Object> handleConflictException(ConflictException ex, WebRequest request){
		ApiResponseError error = new ApiResponseError(HttpStatus.CONFLICT, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@ExceptionHandler(BadRequestException.class)
	protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request){
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(e->e.getDefaultMessage()).collect(Collectors.toList());
		
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, errors.get(0).toString(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	
}
