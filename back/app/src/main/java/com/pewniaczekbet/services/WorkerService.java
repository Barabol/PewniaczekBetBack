package com.pewniaczekbet.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pewniaczekbet.dto.GameDto;
import com.pewniaczekbet.dto.GameScoreChangeDto;
import com.pewniaczekbet.dto.PredictionBetDto;
import com.pewniaczekbet.dto.ScoreBetDto;
import com.pewniaczekbet.dto.WinBetDto;
import com.pewniaczekbet.model.dao.GameRepository;
import com.pewniaczekbet.model.dao.LogRepository;
import com.pewniaczekbet.model.dao.PredictionBetRepository;
import com.pewniaczekbet.model.dao.ScoreBetRepository;
import com.pewniaczekbet.model.dao.SportRepository;
import com.pewniaczekbet.model.dao.TeamRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.dao.WinBetRepository;
import com.pewniaczekbet.model.entities.GameEntity;
import com.pewniaczekbet.model.entities.LogEntity;
import com.pewniaczekbet.model.entities.PredictionBetEntity;
import com.pewniaczekbet.model.entities.ScoreBetEntity;
import com.pewniaczekbet.model.entities.SportEntity;
import com.pewniaczekbet.model.entities.TeamEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.entities.WinBetEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * WorkerService
 */
@Service
@RequiredArgsConstructor
public class WorkerService {

	private final GameRepository gameRepository;
	private final LogRepository logRepository;
	private final UserRepository userRepository;
	private final PredictionBetRepository predictionBetRepository;
	private final SportRepository sportRepository;
	private final TeamRepository teamRepository;
	private final ScoreBetRepository scoreBetRepository;
	private final WinBetRepository winBetRepository;

	private void createLog(Long userId, String content) {
		Optional<UserEntity> user = userRepository.findById(userId);
		if (!user.isPresent())
			return;
		LogEntity log = new LogEntity();
		log.setUser(user.get());
		log.setTime(LocalDateTime.now());
		log.setValue(content);
		logRepository.save(log);
	}

	@Transactional
	private SportEntity getOrCreateSport(String name) {
		return sportRepository.findByName(name)
				.orElseGet(() -> {
					SportEntity entity = new SportEntity();
					entity.setName(name);
					return sportRepository.save(entity);
				});
	}

	@Transactional
	private TeamEntity getOrCreateTeam(String name) {
		return teamRepository.findByName(name)
				.orElseGet(() -> {
					TeamEntity entity = new TeamEntity();
					entity.setName(name);
					return teamRepository.save(entity);
				});
	}

	@Transactional
	private GameEntity gameToEntity(GameDto gameDto) {
		GameEntity entity = new GameEntity();
		entity.setName(gameDto.getName());
		SportEntity sportEntity = getOrCreateSport(gameDto.getSport());
		TeamEntity team1Entity = getOrCreateTeam(gameDto.getTeam1());
		TeamEntity team2Entity = getOrCreateTeam(gameDto.getTeam2());
		entity.setTeam1(team1Entity);
		entity.setTeam2(team2Entity);
		entity.setSport(sportEntity);
		entity.setStartDate(gameDto.getStartDate());
		entity.setTeam1Score(gameDto.getTeam1Score());
		entity.setTeam2Score(gameDto.getTeam2Score());
		return entity;
	}

	@Transactional
	private WinBetEntity winBetToEntity(WinBetDto dto) {
		WinBetEntity entity = new WinBetEntity();
		entity.setStopDate(dto.getStopDate());
		entity.setName(dto.getName());
		entity.setCurrentMultiplier(dto.getCurrentMultiplier());
		entity.setPaid(false);
		Optional<GameEntity> gameEntity = gameRepository.findByName(dto.getGame().getName());
		if (gameEntity.isPresent())
			entity.setGame(gameEntity.get());
		else
			entity.setGame(gameRepository.save(gameToEntity(dto.getGame())));
		return entity;
	}

	@Transactional
	private ScoreBetEntity scoreBetToEntity(ScoreBetDto dto) {
		ScoreBetEntity entity = new ScoreBetEntity();
		entity.setStopDate(dto.getStopDate());
		entity.setName(dto.getName());
		entity.setCurrentMultiplier(dto.getCurrentMultiplier());
		entity.setPaid(false);
		Optional<GameEntity> gameEntity = gameRepository.findByName(dto.getGame().getName());
		if (gameEntity.isPresent())
			entity.setGame(gameEntity.get());
		else
			entity.setGame(gameRepository.save(gameToEntity(dto.getGame())));
		return entity;
	}

	public void updateGameScore(GameScoreChangeDto score, Long userId) {
		GameEntity game = gameRepository.findById(score.getGameId())
				.orElseThrow(() -> new BadRequestException("game with this id does not exists"));
		createLog(userId,
				"chamge of game(" + score.getGameId() + ") score from " + game.getTeam1Score() + ":" + game.getTeam2Score()
						+ " to "
						+ score.getTeam1Score() + ":" + score.getTeam2Score());
		game.setTeam1Score(score.getTeam1Score());
		game.setTeam2Score(score.getTeam2Score());
		gameRepository.save(game);
	}

	public void predictionSet(Long userId, Long betId, Boolean value) {
		PredictionBetEntity prediction = predictionBetRepository.findById(betId)
				.orElseThrow(() -> new BadRequestException("unable to fid bet"));
		createLog(userId, "set prediction \"" + betId + "\" value to: " + value);
		if (prediction.getEndedWith() == null)
			new BadRequestException("bet was paid, you are not allowed to change outcome");
		prediction.setEndedWith(value);
		predictionBetRepository.save(prediction);
	}

	public void savePredictionBet(PredictionBetDto predictionBet, Long userId) {
		createLog(userId, "creating prediction bet: " + predictionBet.getName());
		predictionBetRepository.save(predictionBet.toEntity());
	}

	public void saveScoreBet(ScoreBetDto scoreBet, Long userId) {
		createLog(userId, "creating score bet: " + scoreBet.getName());
		ScoreBetEntity entity = scoreBetToEntity(scoreBet);
		scoreBetRepository.save(entity);
	}

	public void saveWinBet(WinBetDto winBet, Long userId) {
		createLog(userId, "creating win bet: " + winBet.getName());
		WinBetEntity entity = winBetToEntity(winBet);
		winBetRepository.save(entity);
	}

}
