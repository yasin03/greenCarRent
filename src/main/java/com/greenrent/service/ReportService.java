package com.greenrent.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.greenrent.domain.User;
import com.greenrent.helper.ExcelReportHelper;
import com.greenrent.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReportService {
	
	UserRepository userRepository;
	
	
	public ByteArrayInputStream getUserReport() throws IOException {
		List<User> users= userRepository.findAll();
		return ExcelReportHelper.getUsersExcelReport(users);
	}

}
