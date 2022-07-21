package com.greenrent.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greenrent.domain.Car;
import com.greenrent.domain.ImageFile;
import com.greenrent.dto.CarDTO;
import com.greenrent.dto.mapper.CarMapper;
import com.greenrent.exeption.BadRequestException;
import com.greenrent.exeption.ResourceNotFoundExeption;
import com.greenrent.exeption.message.ErrorMessage;
import com.greenrent.repository.CarRepository;
import com.greenrent.repository.ImageFileRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CarService {

	private CarRepository carRepository;

	private ImageFileRepository imageFileRepository;

	private CarMapper carMapper;

	// http://localhost:8080/car/visitors/all
	@Transactional(readOnly = true)
	public List<CarDTO> getAllCars() {
		List<Car> carList = carRepository.findAll();
		return carMapper.map(carList);
	}

	// http://localhost:8080/car/visitors/2
	@Transactional(readOnly = true)
	public CarDTO findById(Long id) {
		Car car = carRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));
		return carMapper.carToCarDTO(car);
	}

	// http://localhost:8080/car/admin/69ced9ea-e921-43a2-a598-9f81876c705e/add
	public void saveCar(CarDTO carDTO, String imageId) {
		ImageFile imgFile = imageFileRepository.findById(imageId).orElseThrow(
				() -> new ResourceNotFoundExeption(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE, imageId)));

		// Car car = CarMapper.INSTANCE.carDTOToCar(carDTO);
		Car car = carMapper.carDTOToCar(carDTO);

		Set<ImageFile> imageFile = new HashSet<>();
		imageFile.add(imgFile);
		car.setImage(imageFile);

		carRepository.save(car);
	}

	@Transactional(readOnly = true)
	public Page<CarDTO> findAllWithPage(Pageable pageable){
		return carRepository.findAllCarWithPage(pageable);
	}
	
	/**
	 * This method is used to update a car.
	 * 
	 * @param id -> This is car id that will be updated
	 * @param imageId -> This is image id 
	 * @param carDTO -> This is CarDTO to keep data about the car.
	 */
	
	@Transactional(readOnly = true)
	public void updateCar(Long id, String imageId, CarDTO carDTO) {
		Car foundCar = carRepository.findById(id).orElseThrow(()->new 
				ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));
		ImageFile imgFile = imageFileRepository.findById(imageId).orElseThrow(()-> new 
				ResourceNotFoundExeption(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE, imageId)));
		
		if(foundCar.getBuiltIn()) {
			throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
		}
		
		Set<ImageFile> imgs = foundCar.getImage();
		imgs.add(imgFile);
		
		Car car = carMapper.carDTOToCar(carDTO);
		car.setId(foundCar.getId());
		car.setImage(imgs);
		
		carRepository.save(car);
		
	}
	
	public void removeCarById(Long id) {
		Car foundCar = carRepository.findById(id).orElseThrow(()->new 
				ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));
	
		if(foundCar.getBuiltIn()) {
			throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
		}
		
		carRepository.deleteById(id);
	
	}
}















