package com.pewniaczekbet.other;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pewniaczekbet.model.dao.PredictionBetRepository;
import com.pewniaczekbet.model.dao.ScoreBetRepository;
import com.pewniaczekbet.model.dao.TeamRepository;
import com.pewniaczekbet.model.dao.UserPredictionBetRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.dao.UserScoreBetRepository;
import com.pewniaczekbet.model.dao.UserWinBetRepository;
import com.pewniaczekbet.model.dao.WinBetRepository;
import com.pewniaczekbet.model.entities.GameEntity;
import com.pewniaczekbet.model.entities.PredictionBetEntity;
import com.pewniaczekbet.model.entities.ScoreBetEntity;
import com.pewniaczekbet.model.entities.TeamEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.entities.UserPredictionBetEntity;
import com.pewniaczekbet.model.entities.UserScoreBetEntity;
import com.pewniaczekbet.model.entities.UserWinBetEntity;
import com.pewniaczekbet.model.entities.WinBetEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;

import lombok.RequiredArgsConstructor;

/**
 * DbIntervalHandler
 */
@Component
@RequiredArgsConstructor
public class DbIntervalHandler {

	private final PredictionBetRepository predictionBetRepository;
	private final WinBetRepository winBetRepository;
	private final ScoreBetRepository scoreBetRepository;

	private final UserPredictionBetRepository userPredictionBetRepository;
	private final UserWinBetRepository userWinBetRepository;
	private final UserScoreBetRepository userScoreBetRepository;

	private final TeamRepository teamRepository;
	private final UserRepository userRepository;

	private void handlePrediction(LocalDateTime date) {
		List<PredictionBetEntity> predictions = predictionBetRepository
				.findByStopDateBeforeAndEndedWithNotNullAndPaidFalse(date);
		System.out.println("predictions: " + predictions.size());

		for (PredictionBetEntity e : predictions) {
			List<UserPredictionBetEntity> users = userPredictionBetRepository.findAllByPredictionId(e.getId());
			Long pot = e.getPot();
			Long wins = e.getEndedWith() ? e.getTrueBets() : e.getFalseBets();
			e.setPaid(true);
			predictionBetRepository.save(e);
			if (wins == 0)
				continue;
			Long mult = pot / (e.getEndedWith() ? e.getTrueBetsAmount() : e.getFalseBetsAmount());

			for (UserPredictionBetEntity u : users) {
				if (u.getPredicted() != e.getEndedWith())
					continue;
				UserEntity user = u.getUser();
				user.setBalance(user.getBalance() + u.getAmount() * mult);
				userRepository.save(user);
			}
		}
	}

	private void handleScoreBet(LocalDateTime date) {
		List<ScoreBetEntity> scoreBets = scoreBetRepository.findByStopDateBeforeAndPaidFalse(date);
		System.out.println("score bets: " + scoreBets.size());
		for (ScoreBetEntity s : scoreBets) {
			System.out.println(s.getId());
			List<UserScoreBetEntity> best = userScoreBetRepository.findAllByBetId(s.getId());
			GameEntity game = s.getGame();
			s.setPaid(true);
			scoreBetRepository.save(s);

			for (UserScoreBetEntity us : best) {
				UserEntity user = us.getUser();
				if (game.getTeam1Score() != us.getTeam1Score() || game.getTeam2Score() != us.getTeam2Score())
					continue;
				user.setBalance(user.getBalance() + (long) Math.floor(us.getAmmount() * us.getMultiplyer()));
				userRepository.save(user);
			}
		}
	}

	private void handleWinBet(LocalDateTime date) {
		List<WinBetEntity> winBets = winBetRepository.findByStopDateBeforeAndPaidFalse(date);
		System.out.println("win bets: " + winBets.size());
		for (WinBetEntity w : winBets) {
			List<UserWinBetEntity> best = userWinBetRepository.findAllByBetId(w.getId());
			GameEntity game = w.getGame();
			TeamEntity team = teamRepository.findById(0L).orElseThrow(() -> new BadRequestException(""));
			w.setPaid(true);
			winBetRepository.save(w);

			if (game.getTeam1Score() > game.getTeam2Score())
				team = game.getTeam1();
			else if (game.getTeam1Score() < game.getTeam2Score())
				team = game.getTeam2();

			for (UserWinBetEntity uw : best) {
				if (uw.getTeam().getId() != team.getId())
					continue;
				UserEntity user = uw.getUser();

				user.setBalance(user.getBalance() + (long) Math.floor(uw.getAmmount() * uw.getMultiplyer()));
				userRepository.save(user);
			}
		}
	}

	private void updateScoreBets(LocalDateTime date) {

	}

	private void updateWinBets(LocalDateTime date) {

	}

	@Scheduled(fixedDelayString = "${DB_UPDATE_INTERVAL}")
	public void updateDatabase() {
		LocalDateTime date = LocalDateTime.now();
		System.out.println("[" + date + "] DB update ");
		handlePrediction(date);
		handleWinBet(date);
		handleScoreBet(date);
		updateWinBets(date);
		updateScoreBets(date);
	}
}
