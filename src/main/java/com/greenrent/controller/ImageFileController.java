package com.greenrent.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.greenrent.domain.ImageFile;
import com.greenrent.dto.ImageFileDTO;
import com.greenrent.dto.response.GRResponse;
import com.greenrent.dto.response.ImageSavedResponse;
import com.greenrent.dto.response.ResponseMessage;
import com.greenrent.service.ImageFileService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class ImageFileController {

	private ImageFileService service;
	
	@PostMapping("/upload")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> uploadFile(@RequestParam("file") MultipartFile file ){
		String imageId = service.saveImage(file);
		ImageSavedResponse response = new ImageSavedResponse();
		response.setImageId(imageId);
		response.setMessage(ResponseMessage.IMAGE_SAVED_RESPONSE_MESSAGE);
		response.setSuccess(true);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<byte []> getImageFile(@PathVariable String id){
		ImageFile imageFile = service.getImageById(id);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, 
				"attachment; filename="+imageFile.getName()).body(imageFile.getData());
	}
	
	@GetMapping("/display/{id}")
	public ResponseEntity<byte []> displayFile(@PathVariable String id){
		ImageFile imageFile = service.getImageById(id);

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.IMAGE_PNG);
		return new ResponseEntity<>(imageFile.getData(), header,HttpStatus.OK);
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ImageFileDTO>> getAllImages(){
		List<ImageFileDTO> imageList = service.getAllImages();
		
		// return ResponseEntity.ok(imageList);
		return ResponseEntity.status(HttpStatus.OK).body(imageList);
	}
	
	
}




