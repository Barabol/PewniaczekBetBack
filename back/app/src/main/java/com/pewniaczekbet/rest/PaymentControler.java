package com.pewniaczekbet.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.pewniaczekbet.services.PaymentService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * PaymentControler
 */
@RestController
@RequestMapping("/pay/")
@RequiredArgsConstructor
public class PaymentControler {

	private final PaymentService paymentService;

	@PostMapping("/send")
	RedirectView sendPayment(HttpSession session, @RequestBody(required = true) Long amount) {
		Long userId = UserControler.getUserId(session);
		return paymentService.createPayment(amount, userId);
	}

	@GetMapping("/redirect")
	RedirectView redirectPayment(@RequestParam(required = true) Long paymentId) {
		return paymentService.handleSuccess(paymentId);
	}

	@GetMapping("/reload_all")
	String reload() {// TODO: remove
		paymentService.reload();
		return "OK";
	}
}
