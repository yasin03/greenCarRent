package com.greenrent.exeption.message;

public class ErrorMessage {
	
	public final static String RESOURCE_NOT_FOUND_MESSAGE  = "Resource with id %d not found!";
	public final static String USERNAME_NOT_FOUND_MESSAGE  = "User with email %s not found!";
	public final static String EMAIL_ALREADY_EXIST  = "Email already exist : %s ";
	public final static String ROLE_NOT_FOUND_MESSAGE  = "Role with name %s not found!";
	public final static String NOT_PERMITTED_METHOD_MESSAGE  = "You don't have any permission to change this value!";
	public final static String PASSWORD_NOT_MATCHED  = "Your password are not matched!";
	public final static String IMAGE_NOT_FOUND_MESSAGE  = "Image File with id %s not found!";
	public final static String RESERVATION_TIME_INCORRECT_MESSAGE = "Reservation pick up time or drop off time not correct!";
	public final static String CAR_NOT_AVAILABLE_MESSAGE = "Car is not available for selected time!";
	public final static String EXCEL_REPORT_CREATION_ERROR_MESSAGE = "Error occured while generation excel report!";
	public final static String CAR_USED_BY_RESERVATION_MESSAGE = "Car couldn't be deleted. Car is used by a reservation!";
	public final static String USER_USED_BY_RESERVATION_MESSAGE = "User couldn't be deleted. User is used by a reservation!";
	
}
