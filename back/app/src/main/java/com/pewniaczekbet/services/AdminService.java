package com.pewniaczekbet.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.pewniaczekbet.dto.LogDto;
import com.pewniaczekbet.model.dao.LogRepository;
import com.pewniaczekbet.other.PagePropertiesValidator;

import lombok.RequiredArgsConstructor;

/**
 * AdminService
 */
@Service
@RequiredArgsConstructor
public class AdminService {
	private final LogRepository logRepository;

	public Page<LogDto> getLogs(int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		return logRepository.findAll(PageRequest.of(pageNumber, pageSize)).map(LogDto::fromEntity);
	}
}
