package com.greenrent.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.greenrent.domain.Role;
import com.greenrent.domain.User;
import com.greenrent.domain.enums.RoleType;
import com.greenrent.dto.RegisterRequest;
import com.greenrent.exeption.ConflictException;
import com.greenrent.exeption.ResourceNotFoundExeption;
import com.greenrent.exeption.message.ErrorMessage;
import com.greenrent.repository.RoleRepository;
import com.greenrent.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

	
	private UserRepository userRepository;
	
	private RoleRepository roleRepository;
	
	private PasswordEncoder passwordEncoder;
	
	
	public void register(RegisterRequest registerRequest) {
		
		if ( userRepository.existsByEmail(registerRequest.getEmail())) {
			throw new ConflictException(String.format(ErrorMessage.EMAİL_ALREADY_EXIST, registerRequest.getEmail()));
		}
		
		String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
		
		Role role = roleRepository.findByName(RoleType.ROLE_CUSTOMER).orElseThrow(()-> new 
				ResourceNotFoundExeption(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, RoleType.ROLE_CUSTOMER.name())));
		
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		
		User user = new User();
		user.setFirstName(registerRequest.getFirstName());
		user.setLastName(registerRequest.getLastName());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(encodedPassword);
		user.setAddress(registerRequest.getAddress());
		user.setPhoneNumber(registerRequest.getPhoneNumber());
		user.setZipCode(registerRequest.getZipCode());
		user.setRoles(roles);
		
		userRepository.save(user);
	}
	
}
