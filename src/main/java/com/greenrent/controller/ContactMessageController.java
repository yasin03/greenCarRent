package com.greenrent.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greenrent.domain.ContactMessage;
import com.greenrent.service.ContactMessageService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/contactmessage")
@AllArgsConstructor
public class ContactMessageController {

	@Autowired
	private ContactMessageService contactMessageService;
	
	
	@PostMapping("/visitor")
	public ResponseEntity<Map<String, String>> createMessage(@Valid @RequestBody ContactMessage contactMessage){
		
		contactMessageService.createContactMessage(contactMessage);
		
		Map<String, String> map = new HashMap<>();
		
		map.put("message", "Contact Message succesfully created!");
		map.put("status", "true");
		
		return new ResponseEntity<Map<String,String>>(map, HttpStatus.CREATED);
	}
	
	@GetMapping
	public ResponseEntity<List<ContactMessage>> getAllContactMessage(){
		List<ContactMessage> list = contactMessageService.getAll();
		return ResponseEntity.ok(list);
	}
	
	
	
}
