package com.greenrent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarAvailabilityResponse extends GRResponse {

	private boolean isAvailable;
	private double totalPrice;
	
	public CarAvailabilityResponse (boolean isAvailable, double totalPrice, String message, boolean success) {
		super(success, message);
		this.isAvailable = isAvailable;
		this.totalPrice = totalPrice;
		
	}
}
