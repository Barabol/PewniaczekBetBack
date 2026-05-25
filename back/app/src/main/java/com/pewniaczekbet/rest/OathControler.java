package com.pewniaczekbet.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.pewniaczekbet.dto.OathDto;
import com.pewniaczekbet.services.OathServie;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * OathControler
 */
@RestController
@RequestMapping("/social/")
@RequiredArgsConstructor
public class OathControler {

	private final OathServie oathServie;

	@GetMapping("github/initiate")
	public RedirectView initiateOathGithub(HttpSession session) {
		Long userId = UserControler.getUserId(session);
		return new RedirectView(oathServie.getRedirectGithub(userId));
	}

	@GetMapping("github/callback")
	public ResponseEntity<String> callbackOathGithub(@RequestParam(required = true) String code, HttpSession session) {
		Long userId = UserControler.getUserId(session);
		oathServie.getCallbackGithub(code, userId);
		return ResponseEntity.ok("OK");
	}

	@DeleteMapping("github")
	public ResponseEntity<String> deleteOathGithub(HttpSession session) {
		Long userId = UserControler.getUserId(session);
		oathServie.deleteGithub(userId);
		return ResponseEntity.ok("OK");
	}

	@GetMapping("all")
	public ResponseEntity<List<OathDto>> getOath(@RequestParam(required = true) Long userId) {
		return ResponseEntity.ok().body(oathServie.getOath(userId));
	}
}
