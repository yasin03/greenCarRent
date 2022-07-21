package com.greenrent.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greenrent.domain.Car;
import com.greenrent.domain.Reservation;
import com.greenrent.domain.User;
import com.greenrent.domain.enums.ReservationStatus;
import com.greenrent.dto.ReservationDTO;
import com.greenrent.dto.mapper.ReservationMapper;
import com.greenrent.dto.request.ReservationRequest;
import com.greenrent.dto.request.ReservationUpdateRequest;
import com.greenrent.exeption.BadRequestException;
import com.greenrent.exeption.ResourceNotFoundExeption;
import com.greenrent.exeption.message.ErrorMessage;
import com.greenrent.repository.CarRepository;
import com.greenrent.repository.ReservationRepository;
import com.greenrent.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReservationService {

	private ReservationRepository reservationRepository;
	private CarRepository carRepository;
	private UserRepository userRepository;
	private ReservationMapper reservationMapper;

	@Transactional(readOnly = true)
	public List<ReservationDTO> getAllReservations() {
		return reservationRepository.findAllBy();
	}

	@Transactional(readOnly = true)
	public ReservationDTO findById(Long id) {
		return reservationRepository.findDTOById(id).orElseThrow(
				() -> new ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));
	}
	
	@Transactional(readOnly = true)
	public List<ReservationDTO> findAllUserId(Long userId){
		
		User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundExeption(
				String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, userId)));
		return reservationRepository.findAllByUserId(user);
		
	}
	
	public ReservationDTO findByIdAndUserId(Long id, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(()-> new 
				ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, userId)));
		return reservationRepository.findByIdAndUserId(id, user).orElseThrow(()-> new 
				 ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));

	}

	public void createReservation(ReservationRequest reservationRequest, Long userId, Long carId) {
		checkReservationTimeIsCorrect(reservationRequest.getPickUpTime(), reservationRequest.getDropOffTime());

		Car car = carRepository.findById(carId).orElseThrow(
				() -> new ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, carId)));

		boolean carStatus = checkCarAvailability(carId, reservationRequest.getPickUpTime(),
				reservationRequest.getDropOffTime());
		User user = userRepository.findById(userId).orElseThrow(
				() -> new ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, userId)));

		Reservation reservation = reservationMapper.reservationRequestToReservation(reservationRequest);

		if (!carStatus) {
			reservation.setStatus(ReservationStatus.CREATED);
		} else {
			throw new BadRequestException(ErrorMessage.CAR_NOT_AVAILABLE_MESSAGE);
		}
		reservation.setCarId(car);
		reservation.setUserId(user);

		Double totalPrice = getTotalPrice(carId, reservation.getPickUpTime(), reservation.getDropOffTime());

		reservation.setTotalPrice(totalPrice);
		reservationRepository.save(reservation);

	}
	
	public void updateReservation(Long reservationId, Long carId, ReservationUpdateRequest reservationUpdateRequest) {
		Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
				() -> new ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, reservationId)));
		
		checkReservationTimeIsCorrect(reservationUpdateRequest.getPickUpTime(), reservationUpdateRequest.getDropOffTime());

		Car car = carRepository.findById(carId).orElseThrow(
				() -> new ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, carId)));

		boolean carStatus = checkCarAvailability(carId, reservationUpdateRequest.getPickUpTime(),
				reservationUpdateRequest.getDropOffTime());
		
		if(reservationUpdateRequest.getPickUpTime().compareTo(reservation.getPickUpTime())==0 && 
				reservationUpdateRequest.getDropOffTime().compareTo(reservation.getDropOffTime())==0 &&
				car.getId().equals(reservation.getCarId().getId())) {
			reservation.setStatus(reservationUpdateRequest.getStatus());
		}else if (carStatus) {
			throw new BadRequestException(ErrorMessage.CAR_NOT_AVAILABLE_MESSAGE); 
		}
		
		Double totalPrice = getTotalPrice(carId, reservationUpdateRequest.getPickUpTime(), reservationUpdateRequest.getDropOffTime());
		reservation.setTotalPrice(totalPrice);
		reservation.setCarId(car);
		reservation.setPickUpTime(reservationUpdateRequest.getPickUpTime());
		reservation.setDropOffTime(reservationUpdateRequest.getDropOffTime());
		reservation.setPickUpLocation(reservation.getPickUpLocation());
		reservation.setDropOffLocation(reservationUpdateRequest.getDropOffLocation());
		
		reservationRepository.save(reservation);
	
	}

	public void removeById(Long id) {
		boolean exist = reservationRepository.existsById(id);
		
		if(!exist) {
			throw new ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id));
		}
		reservationRepository.deleteById(id);
	}
	
	public Double getTotalPrice(Long carId, LocalDateTime pickUpTime, LocalDateTime dropOffTime) {

		Car car = carRepository.findById(carId).orElseThrow(()-> new 
				ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, carId)));
		Long hours = (new Reservation()).getTotalHour(pickUpTime, dropOffTime);
		return car.getPricePerHour() * hours;
	}

	// reservasyon zamanları şuandan öncemi diye kontrol eden method
	private void checkReservationTimeIsCorrect(LocalDateTime pickUpTime, LocalDateTime dropOffTime) {
		LocalDateTime now = LocalDateTime.now();
		if (pickUpTime.isBefore(now)) {
			throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
		}
		boolean isEqual = pickUpTime.equals(dropOffTime) ? true : false;
		boolean isBefore = pickUpTime.isBefore(dropOffTime) ? true : false;

		if (isEqual || !isBefore) {
			throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
		}
	}

	public boolean checkCarAvailability(Long carId, LocalDateTime pickUpTime, LocalDateTime dropOffTime) {
		ReservationStatus[] status = { ReservationStatus.CANCELLED, ReservationStatus.DONE };
		List<Reservation> existReservations = reservationRepository.checkCarStatus(carId, pickUpTime, dropOffTime,
				status);
		return !existReservations.isEmpty();
	}
}
