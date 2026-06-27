package com.pewniaczekbet.rest;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pewniaczekbet.dto.GameScoreChangeDto;
import com.pewniaczekbet.dto.PredictionBetDto;
import com.pewniaczekbet.dto.PredictionBetPlaceDto;
import com.pewniaczekbet.dto.ScoreBetDto;
import com.pewniaczekbet.dto.ScoreBetPlaceDto;
import com.pewniaczekbet.dto.SportListDto;
import com.pewniaczekbet.dto.UserBetPredictionDto;
import com.pewniaczekbet.dto.UserScoreBetDto;
import com.pewniaczekbet.dto.UserWinBetDto;
import com.pewniaczekbet.dto.WinBetDto;
import com.pewniaczekbet.dto.WinBetPlaceDto;
import com.pewniaczekbet.services.BetService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * BetControler
 */
@RestController
@RequestMapping("/api/bet")
@RequiredArgsConstructor
public class BetControler {

	private final BetService betService;

	@GetMapping("/win/all")
	Page<WinBetDto> getWinAll(@RequestParam(required = false) String sport, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize) {
		return betService.getWinAll(sport, page, pageSize);
	}

	@GetMapping("/win/sports")
	List<SportListDto> getWinSports() {
		return betService.getWinSports();
	}

	@GetMapping("/win/curent")
	Page<WinBetDto> getWinCurent(@RequestParam(required = false) String sport,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize) {
		return betService.getWinCurent(sport, page, pageSize);
	}

	@PostMapping("/win/place")
	ResponseEntity<String> placeWinBet(HttpSession session, @RequestBody @Validated WinBetPlaceDto bet) {
		Long userId = UserControler.getUserId(session);
		betService.placeWinBet(bet, userId);
		return ResponseEntity.ok("OK");
	}

	@GetMapping("/score/all")
	Page<ScoreBetDto> getScoreAll(@RequestParam(required = false) String sport,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize) {
		return betService.getScoreAll(sport, page, pageSize);
	}

	@GetMapping("/score/sports")
	List<SportListDto> getScoreSports() {
		return betService.getScoreSports();
	}

	@GetMapping("/score/curent")
	Page<ScoreBetDto> getScoreCurent(@RequestParam(required = false) String sport,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize) {
		return betService.getScoreCurent(sport, page, pageSize);
	}

	@PostMapping("/score/place")
	ResponseEntity<String> placeScoreBet(HttpSession session, @RequestBody @Validated ScoreBetPlaceDto bet) {
		Long userId = UserControler.getUserId(session);
		betService.placeScoreBet(bet, userId);
		return ResponseEntity.ok("OK");
	}

	@GetMapping("/prediction/all")
	Page<PredictionBetDto> getPredictionAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize) {
		return betService.getPredictionAll(page, pageSize);
	}

	@GetMapping("/prediction/curent")
	Page<PredictionBetDto> getPredictionCurent(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize) {
		return betService.getPredictionCurent(page, pageSize);
	}

	@PostMapping("/prediction/place")
	ResponseEntity<String> placePredictionBet(HttpSession session, @RequestBody @Validated PredictionBetPlaceDto bet) {
		Long userId = UserControler.getUserId(session);
		betService.placePredictionBet(bet, userId);
		return ResponseEntity.ok("OK");
	}

	@GetMapping("/prediction/history")
	ResponseEntity<Page<UserBetPredictionDto>> predictionBetHistory(HttpSession session,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(required = false) Long user,
			@RequestParam(required = false) Boolean findEnded) {
		Long userId = UserControler.getUserId(session);
		if (findEnded == null)
			return ResponseEntity.ok(betService.getUserPredictionsAll(userId, user, page, pageSize));
		if (findEnded)
			return ResponseEntity.ok(betService.getUserPredictionsEnded(userId, user, page, pageSize));
		return ResponseEntity.ok(betService.getUserPredictionsNotEnded(userId, user, page, pageSize));
	}

	@GetMapping("/score/history")
	ResponseEntity<Page<UserScoreBetDto>> scoreBetHistory(HttpSession session,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(required = false) Long user,
			@RequestParam(required = false) String sport,
			@RequestParam(required = false) Boolean findEnded) {
		Long userId = UserControler.getUserId(session);
		return ResponseEntity.ok(betService.getUserScore(userId, user, findEnded, sport, page, pageSize));
	}

	@GetMapping("/win/history")
	ResponseEntity<Page<UserWinBetDto>> winBetHistory(HttpSession session,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(required = false) Long user,
			@RequestParam(required = false) String sport,
			@RequestParam(required = false) Boolean findEnded) {
		Long userId = UserControler.getUserId(session);
		return ResponseEntity.ok(betService.getUserWin(userId, user, findEnded, sport, page, pageSize));
	}

}
