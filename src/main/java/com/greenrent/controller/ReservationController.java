package com.greenrent.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greenrent.dto.ReservationDTO;
import com.greenrent.dto.request.ReservationRequest;
import com.greenrent.dto.request.ReservationUpdateRequest;
import com.greenrent.dto.response.CarAvailabilityResponse;
import com.greenrent.dto.response.GRResponse;
import com.greenrent.dto.response.ResponseMessage;
import com.greenrent.service.ReservationService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/reservations")
public class ReservationController {

	private ReservationService reservationService;


	/**
	 * 
	 * @param request
	 * @param carId
	 * @param reservationRequest
	 * @return
	 */

	// http://localhost:8080/reservations/add?carId=1
	@PostMapping("/add")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public ResponseEntity<GRResponse> makeReservation(HttpServletRequest request, @RequestParam("carId") Long carId,
			@Valid @RequestBody ReservationRequest reservationRequest) {
		Long userId = (Long) request.getAttribute("id");
		reservationService.createReservation(reservationRequest, userId, carId);
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.RESERVATION_SAVED_RESPONSE_MESSAGE);
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// http://localhost:8080/reservations/add/auth?carId=4&userId=3
	@PostMapping("/add/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> addReservation(@RequestParam("userId") Long userId,
			@RequestParam("carId") Long carId, @Valid @RequestBody ReservationRequest reservationRequest) {
		reservationService.createReservation(reservationRequest, userId, carId);
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.RESERVATION_SAVED_RESPONSE_MESSAGE);
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// http://localhost:8080/reservations/admin/auth?carId=2&reservationId=1
	@PostMapping("/admin/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> updateeservation(@RequestParam(value = "carId") Long carId,
			@RequestParam(value = "reservationId") Long reservationId,
			@Valid @RequestBody ReservationUpdateRequest reservationUpdateRequest) {
		reservationService.updateReservation(reservationId, carId, reservationUpdateRequest);
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.RESERVATION_UPDATE_RESPONSE_MESSAGE);
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// http://localhost:8080/reservations/auth?carId=2&pickUpTime=07/21/2022
	// 23:00:00&dropOffTime=07/22/2022 23:00:00
	@GetMapping("/auth")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public ResponseEntity<GRResponse> checkCarIsAvailable(@RequestParam(value = "carId") Long carId,
			@RequestParam(value = "pickUpTime") @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") LocalDateTime pickUpTime,
			@RequestParam(value = "dropOffTime") @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") LocalDateTime dropOffTime) {
		boolean isNotAvailable = reservationService.checkCarAvailability(carId, pickUpTime, dropOffTime);
		Double totalPrice = reservationService.getTotalPrice(carId, pickUpTime, dropOffTime);
		CarAvailabilityResponse response = new CarAvailabilityResponse(!isNotAvailable, totalPrice,
			ResponseMessage.CAR_AVAILABLE_MESSAGE, true);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// http://localhost:8080/reservations/admin/1/auth
	@DeleteMapping("/admin/{id}/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> deleteReservation(@PathVariable Long id) {
		reservationService.removeById(id);
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.RESERVATION_DELETE_RESPONSE_MESSAGE);
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	//Admin bütün rezervasyon bilgilerini getirmek istiyor.
	// http://localhost:8080/reservations/admin/all
	@GetMapping("/admin/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ReservationDTO>> getAllReservations() {
		List<ReservationDTO> reservations = reservationService.getAllReservations();
		return ResponseEntity.ok(reservations);
	}

	// Admin bir rezervasyon id ile rezervasyon bilgisini döndürmek için kullanıyor.
	// http://localhost:8080/reservations/1/admin
	@GetMapping("/{id}/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
		ReservationDTO reservationDTO = reservationService.findById(id);
		return ResponseEntity.ok(reservationDTO);
	}

	// Bir user id ile gelerek user'a ait olan tüm rezervasyonları döndürüyor.
	@GetMapping("/admin/auth/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ReservationDTO>> getAllUserReservations(@RequestParam(value = "userId") Long userId) {
		List<ReservationDTO> reservationList = reservationService.findAllUserId(userId);
		return ResponseEntity.ok(reservationList);
	}

	// Customer yada Admin rolüne sahip bir kullanıcı kendisine ait olan bir rezervasyon bilgisini getiriyor.
	@GetMapping("/{id}/auth")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public ResponseEntity<ReservationDTO> getUserReservationById(HttpServletRequest request, @PathVariable Long id) {
		Long userId = (Long) request.getAttribute("id");
		ReservationDTO reservationDTO = reservationService.findByIdAndUserId(id, userId);
		return ResponseEntity.ok(reservationDTO);
	}
	
	// Customer yada Admin rolüne sahip bir kullanıcı kendisine ait olan bütün rezervasyon bilgilerini getiriyor.
	@GetMapping("/auth/all")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public ResponseEntity<List<ReservationDTO>> getUserReservationsByUserId(HttpServletRequest request) {
		Long userId = (Long) request.getAttribute("id");
		List<ReservationDTO> reservationDTO = reservationService.findAllUserId(userId);
		return ResponseEntity.ok(reservationDTO);
	}
	

}
