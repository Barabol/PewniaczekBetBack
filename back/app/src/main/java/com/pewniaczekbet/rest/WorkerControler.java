package com.pewniaczekbet.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pewniaczekbet.dto.GameScoreChangeDto;
import com.pewniaczekbet.dto.PredictionBetDto;
import com.pewniaczekbet.dto.ScoreBetDto;
import com.pewniaczekbet.dto.WinBetDto;
import com.pewniaczekbet.services.WorkerService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * WorkerControler
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/worker")
public class WorkerControler {

	private final WorkerService workerService;

	@PostMapping("/game/update")
	ResponseEntity<String> updateGame(HttpSession session,
			@RequestBody(required = true) @Validated GameScoreChangeDto score) {
		Long userId = UserControler.isWorker(session);
		workerService.updateGameScore(score, userId);
		return ResponseEntity.ok("Ok");
	}

	@PostMapping("/win/add")
	ResponseEntity<String> addWinBet(HttpSession session, @RequestBody @Validated WinBetDto bet) {
		Long userId = UserControler.isWorker(session);
		workerService.saveWinBet(bet, userId);
		return ResponseEntity.ok("OK");
	}

	@PostMapping("/score/add")
	ResponseEntity<String> addScoreBet(HttpSession session, @RequestBody @Validated ScoreBetDto bet) {
		Long userId = UserControler.isWorker(session);
		workerService.saveScoreBet(bet, userId);
		return ResponseEntity.ok("OK");
	}

	@PostMapping("/prediction/add")
	ResponseEntity<String> addPredictionBet(HttpSession session, @RequestBody @Validated PredictionBetDto bet) {
		Long userId = UserControler.isWorker(session);
		workerService.savePredictionBet(bet, userId);
		return ResponseEntity.ok("OK");
	}

	@PostMapping("/prediction/end")
	ResponseEntity<String> predictionSet(HttpSession session, @RequestParam Long betId, @RequestParam Boolean value) {
		Long userId = UserControler.isWorker(session);
		workerService.predictionSet(userId, betId, value);
		return ResponseEntity.ok("ok");
	}
}
