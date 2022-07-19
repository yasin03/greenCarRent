package com.greenrent.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.greenrent.domain.ImageFile;
import com.greenrent.dto.ImageFileDTO;
import com.greenrent.exeption.ImageFileException;
import com.greenrent.exeption.ResourceNotFoundExeption;
import com.greenrent.exeption.message.ErrorMessage;
import com.greenrent.repository.ImageFileRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ImageFileService {

	private ImageFileRepository repository;
	
	public String saveImage(MultipartFile file) {
		String filename= StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
		ImageFile imageFile=null;
		try {
			imageFile = new ImageFile(filename, file.getContentType(), file.getBytes());
		} catch (IOException e) {
			throw new ImageFileException(e.getMessage());
		}
		repository.save(imageFile);
		return imageFile.getId();
	}
	
	public ImageFile getImageById(String id) {
		ImageFile imageFile = repository.findById(id).orElseThrow(()->new 
				ResourceNotFoundExeption(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE, id)));
		return imageFile;
	}
	
	public List<ImageFileDTO> getAllImages(){
		List<ImageFile> imageList = repository.findAll();
		
		List<ImageFileDTO> files= imageList.stream().map(imFile->{
			String imageUri= ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/display/").
			path(imFile.getId()).toUriString();
		
		return new ImageFileDTO(imFile.getName(), imageUri, imFile.getType(), imFile.getData().length);		
		}).collect(Collectors.toList());
		
		return files;
	}
}



