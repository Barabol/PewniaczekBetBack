package com.pewniaczekbet.rest;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pewniaczekbet.dto.LogDto;
import com.pewniaczekbet.services.AdminService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * AdminControler
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminControler {

	public final AdminService adminService;

	@GetMapping("/logs")
	public Page<LogDto> getLogs(HttpSession session, @RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "5") int pageSize) {
		UserControler.isAdmin(session);
		return adminService.getLogs(pageNumber, pageSize);
	}
}
