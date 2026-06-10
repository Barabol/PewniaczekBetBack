package com.pewniaczekbet.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.pewniaczekbet.dto.GameDto;
import com.pewniaczekbet.dto.PredictionBetDto;
import com.pewniaczekbet.dto.PredictionBetPlaceDto;
import com.pewniaczekbet.dto.ScoreBetDto;
import com.pewniaczekbet.dto.ScoreBetPlaceDto;
import com.pewniaczekbet.dto.UserBetPredictionDto;
import com.pewniaczekbet.dto.UserScoreBetDto;
import com.pewniaczekbet.dto.UserWinBetDto;
import com.pewniaczekbet.dto.WinBetDto;
import com.pewniaczekbet.dto.WinBetPlaceDto;
import com.pewniaczekbet.model.dao.FollowerRepository;
import com.pewniaczekbet.model.dao.GameRepository;
import com.pewniaczekbet.model.dao.PredictionBetRepository;
import com.pewniaczekbet.model.dao.ScoreBetRepository;
import com.pewniaczekbet.model.dao.SportRepository;
import com.pewniaczekbet.model.dao.TeamRepository;
import com.pewniaczekbet.model.dao.UserPredictionBetRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.dao.UserScoreBetRepository;
import com.pewniaczekbet.model.dao.UserWinBetRepository;
import com.pewniaczekbet.model.dao.WinBetRepository;
import com.pewniaczekbet.model.entities.FollowEntity;
import com.pewniaczekbet.model.entities.GameEntity;
import com.pewniaczekbet.model.entities.PredictionBetEntity;
import com.pewniaczekbet.model.entities.ScoreBetEntity;
import com.pewniaczekbet.model.entities.SportEntity;
import com.pewniaczekbet.model.entities.TeamEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.entities.UserPredictionBetEntity;
import com.pewniaczekbet.model.entities.UserScoreBetEntity;
import com.pewniaczekbet.model.entities.UserWinBetEntity;
import com.pewniaczekbet.model.entities.WinBetEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;
import com.pewniaczekbet.model.exceptions.InternalServerErrorException;
import com.pewniaczekbet.model.exceptions.NotFoundException;
import com.pewniaczekbet.other.ApplicationLimitations;
import com.pewniaczekbet.other.PagePropertiesValidator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * BetService
 */
@Service
@RequiredArgsConstructor
public class BetService {
	private final WinBetRepository winBetRepository;
	private final UserWinBetRepository userWinBetRepository;
	private final UserScoreBetRepository userScoreBetRepository;
	private final ScoreBetRepository scoreBetRepository;
	private final SportRepository sportRepository;
	private final TeamRepository teamRepository;
	private final GameRepository gameRepository;
	private final UserRepository userRepository;
	private final PredictionBetRepository predictionBetRepository;
	private final UserPredictionBetRepository userPredictionBetRepository;
	private final FollowerRepository followerRepository;

	private boolean isPublic(Long userId, Long user) {
		UserEntity usr = userRepository.findById(user)
				.orElseThrow(() -> new BadRequestException("unable to find user with this id"));
		if (usr.isPublic())
			return true;
		Optional<FollowEntity> follow = followerRepository.findByFollowedIdAndFollowerId(userId, user);
		if (follow.isPresent())
			return true;
		return false;
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

	/*--Win-Bet--*/

	@Transactional
	private WinBetEntity winBetToEntity(WinBetDto dto) {
		WinBetEntity entity = new WinBetEntity();
		entity.setStopDate(dto.getStopDate());
		entity.setName(dto.getName());
		entity.setCurrentMultiplier(dto.getCurrentMultiplier());
		Optional<GameEntity> gameEntity = gameRepository.findByName(dto.getGame().getName());
		if (gameEntity.isPresent())
			entity.setGame(gameEntity.get());
		else
			entity.setGame(gameRepository.save(gameToEntity(dto.getGame())));
		return entity;
	}

	@Transactional
	public void placeWinBet(WinBetPlaceDto bet, Long userId) {
		Optional<WinBetEntity> entity = winBetRepository.findById(bet.getBetId());
		if (!entity.isPresent())
			throw new NotFoundException("unable to find provided bet");
		WinBetEntity winBet = entity.get();
		if (winBet.getStopDate().isBefore(LocalDateTime.now()))
			throw new BadRequestException("unable to place bet on cloased bed");

		if (bet.getAmmount() < ApplicationLimitations.MinBetAmount)
			throw new BadRequestException("To low bet amount, must be at least " + ApplicationLimitations.MinBetAmount);

		Optional<UserEntity> user = userRepository.findById(userId);

		if (!user.isPresent())// NOTE: could happen but SHOULDN'T
			throw new InternalServerErrorException("internal server error");

		UserEntity userEntity = user.get();

		if (bet.getIsFreeBet()) {
			if (userEntity.getFreeBetBalance() < bet.getAmmount())
				throw new BadRequestException("Unable to place bet due to insuficient balance");
			userEntity.setFreeBetBalance(userEntity.getFreeBetBalance() - bet.getAmmount());
		} else {
			if (userEntity.getBalance() < bet.getAmmount())
				throw new BadRequestException("Unable to place bet due to insuficient balance");
			userEntity.setBalance(userEntity.getBalance() - bet.getAmmount());
		}

		UserWinBetEntity placed = new UserWinBetEntity();

		placed.setUser(user.get());
		placed.setAmmount(bet.getAmmount());

		if (bet.getTeam())
			placed.setTeam(winBet.getGame().getTeam2());
		else
			placed.setTeam(winBet.getGame().getTeam1());

		userRepository.save(userEntity);

		placed.setMultiplyer(winBet.getCurrentMultiplier());
		placed.setBet(winBet);

		try {
			userWinBetRepository.save(placed);
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException("unable to find bet");
		}
	}

	public void saveWinBet(WinBetDto winBet) {
		WinBetEntity entity = winBetToEntity(winBet);
		winBetRepository.save(entity);
	}

	public Page<WinBetDto> getWinAll(String sport, int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		if (sport == null)
			return winBetRepository.findAll(PageRequest.of(pageNumber, pageSize)).map(WinBetDto::fromEntity);
		return winBetRepository.findByGameSportName(sport, PageRequest.of(pageNumber, pageSize))
				.map(WinBetDto::fromEntity);
	}

	public Page<WinBetDto> getWinCurent(String sport, int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		if (sport == null)
			return winBetRepository.findByStopDateAfter(LocalDateTime.now(), PageRequest.of(pageNumber, pageSize))
					.map(WinBetDto::fromEntity);
		return winBetRepository.findByStopDateAfterAndGameSportName(LocalDateTime.now(), sport,
				PageRequest.of(pageNumber, pageSize)).map(WinBetDto::fromEntity);
	}

	/*--Score-Bet--*/

	@Transactional
	private ScoreBetEntity scoreBetToEntity(ScoreBetDto dto) {
		ScoreBetEntity entity = new ScoreBetEntity();
		entity.setStopDate(dto.getStopDate());
		entity.setName(dto.getName());
		entity.setCurrentMultiplier(dto.getCurrentMultiplier());
		Optional<GameEntity> gameEntity = gameRepository.findByName(dto.getGame().getName());
		if (gameEntity.isPresent())
			entity.setGame(gameEntity.get());
		else
			entity.setGame(gameRepository.save(gameToEntity(dto.getGame())));
		return entity;
	}

	public void placeScoreBet(ScoreBetPlaceDto bet, Long userId) {

		System.out.println("score bet");
		if (bet.getTeam1Score() < 0 || bet.getTeam2Score() < 0)
			throw new BadRequestException("unable to place bet with negative score");

		Optional<ScoreBetEntity> entity = scoreBetRepository.findById(bet.getBetId());
		if (!entity.isPresent())
			throw new NotFoundException("unable to find provided bet");
		ScoreBetEntity scoreBet = entity.get();

		if (scoreBet.getStopDate().isBefore(LocalDateTime.now()))
			throw new BadRequestException("unable to place bet on cloased bed");

		if (bet.getAmmount() < ApplicationLimitations.MinBetAmount)
			throw new BadRequestException("To low bet amount, must be at least " + ApplicationLimitations.MinBetAmount);

		Optional<UserEntity> user = userRepository.findById(userId);

		if (!user.isPresent())// NOTE: could happen but SHOULDN'T
			throw new InternalServerErrorException("internal server error");

		UserEntity userEntity = user.get();

		if (bet.getIsFreeBet()) {
			if (userEntity.getFreeBetBalance() < bet.getAmmount())
				throw new BadRequestException("Unable to place bet due to insuficient balance");
			userEntity.setFreeBetBalance(userEntity.getFreeBetBalance() - bet.getAmmount());
		} else {
			if (userEntity.getBalance() < bet.getAmmount())
				throw new BadRequestException("Unable to place bet due to insuficient balance");
			userEntity.setBalance(userEntity.getBalance() - bet.getAmmount());
		}

		UserScoreBetEntity placed = new UserScoreBetEntity();

		placed.setUser(user.get());
		placed.setAmmount(bet.getAmmount());

		placed.setTeam1Score(bet.getTeam1Score());
		placed.setTeam2Score(bet.getTeam2Score());

		userRepository.save(userEntity);

		placed.setMultiplyer(scoreBet.getCurrentMultiplier());
		placed.setBet(scoreBet);

		userScoreBetRepository.save(placed);
	}

	public void saveScoreBet(ScoreBetDto scoreBet) {
		ScoreBetEntity entity = scoreBetToEntity(scoreBet);
		scoreBetRepository.save(entity);
	}

	public Page<ScoreBetDto> getScoreAll(String sport, int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		if (sport == null)
			return scoreBetRepository.findAll(PageRequest.of(pageNumber, pageSize)).map(ScoreBetDto::fromEntity);
		return scoreBetRepository.findByGameSportName(sport, PageRequest.of(pageNumber, pageSize))
				.map(ScoreBetDto::fromEntity);
	}

	public Page<ScoreBetDto> getScoreCurent(String sport, int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		if (sport == null)
			return scoreBetRepository.findByStopDateAfter(LocalDateTime.now(), PageRequest.of(pageNumber, pageSize))
					.map(ScoreBetDto::fromEntity);
		return scoreBetRepository.findByStopDateAfterAndGameSportName(LocalDateTime.now(), sport,
				PageRequest.of(pageNumber, pageSize)).map(ScoreBetDto::fromEntity);
	}

	/*--Prediction-Bet--*/

	@Transactional
	public void placePredictionBet(PredictionBetPlaceDto bet, Long userId) {

		Optional<PredictionBetEntity> entity = predictionBetRepository.findById(bet.getId());
		if (!entity.isPresent())
			throw new NotFoundException("unable to find provided bet");
		PredictionBetEntity predictionBet = entity.get();

		if (predictionBet.getStopDate().isBefore(LocalDateTime.now()))
			throw new BadRequestException("unable to place bet on cloased bed");

		if (bet.getAmount() < ApplicationLimitations.MinBetAmount)
			throw new BadRequestException("To low bet amount, must be at least " + ApplicationLimitations.MinBetAmount);

		Optional<UserEntity> user = userRepository.findById(userId);

		if (!user.isPresent())// NOTE: could happen but SHOULDN'T
			throw new InternalServerErrorException("internal server error");

		UserEntity userEntity = user.get();

		if (bet.getIsFreeBet()) {
			if (userEntity.getFreeBetBalance() < bet.getAmount())
				throw new BadRequestException("Unable to place bet due to insuficient balance");
			userEntity.setFreeBetBalance(userEntity.getFreeBetBalance() - bet.getAmount());
		} else {
			if (userEntity.getBalance() < bet.getAmount())
				throw new BadRequestException("Unable to place bet due to insuficient balance");
			userEntity.setBalance(userEntity.getBalance() - bet.getAmount());
		}
		predictionBet.setPot(predictionBet.getPot() + bet.getAmount());

		UserPredictionBetEntity placed = new UserPredictionBetEntity();

		placed.setUser(user.get());
		placed.setAmount(bet.getAmount());

		placed.setPredicted(bet.getPrediction());
		placed.setPrediction(predictionBet);

		if (bet.getPrediction()) {
			predictionBet.setTrueBets(predictionBet.getTrueBets() + 1);
			predictionBet.setTrueBetsAmount(predictionBet.getTrueBetsAmount() + bet.getAmount());
		} else {
			predictionBet.setFalseBets(predictionBet.getFalseBets() + 1);
			predictionBet.setFalseBetsAmount(predictionBet.getFalseBetsAmount() + bet.getAmount());
		}

		userRepository.save(userEntity);
		predictionBetRepository.save(predictionBet);

		try {
			userPredictionBetRepository.save(placed);
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException("unable to find bet");
		}
	}

	public void savePredictionBet(PredictionBetDto predictionBet) {
		predictionBetRepository.save(predictionBet.toEntity());
	}

	public Page<PredictionBetDto> getPredictionAll(int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		return predictionBetRepository.findAll(PageRequest.of(pageNumber, pageSize)).map(PredictionBetDto::fromEntity);
	}

	public Page<PredictionBetDto> getPredictionCurent(int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		return predictionBetRepository.findByStopDateAfter(LocalDateTime.now(), PageRequest.of(pageNumber, pageSize))
				.map(PredictionBetDto::fromEntity);
	}

	public Page<UserBetPredictionDto> getUserPredictionsAll(Long userId, Long user, int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		if (user == null || user == userId)
			return userPredictionBetRepository.findAllByUserIdOrderByIdDesc(userId, PageRequest.of(pageNumber, pageSize))
					.map(UserBetPredictionDto::fromEntity);
		if (!isPublic(userId, user))
			throw new BadRequestException("this account is private");
		return userPredictionBetRepository.findAllByUserIdOrderByIdDesc(user, PageRequest.of(pageNumber, pageSize))
				.map(UserBetPredictionDto::fromEntity);
	}

	public Page<UserBetPredictionDto> getUserPredictionsEnded(Long userId, Long user, int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		if (user == null || user == userId)
			return userPredictionBetRepository
					.findAllByUserIdAndPredictionEndedWithIsNotNullOrderByIdDesc(userId,
							PageRequest.of(pageNumber, pageSize))
					.map(UserBetPredictionDto::fromEntity);
		if (!isPublic(userId, user))
			throw new BadRequestException("this account is private");
		return userPredictionBetRepository
				.findAllByUserIdAndPredictionEndedWithIsNotNullOrderByIdDesc(user,
						PageRequest.of(pageNumber, pageSize))
				.map(UserBetPredictionDto::fromEntity);
	}

	public Page<UserBetPredictionDto> getUserPredictionsNotEnded(Long userId, Long user, int pageNumber, int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		if (user == null || user == userId)
			return userPredictionBetRepository
					.findAllByUserIdAndPredictionEndedWithIsNullOrderByIdDesc(userId,
							PageRequest.of(pageNumber, pageSize))
					.map(UserBetPredictionDto::fromEntity);
		if (!isPublic(userId, user))
			throw new BadRequestException("this account is private");
		return userPredictionBetRepository
				.findAllByUserIdAndPredictionEndedWithIsNullOrderByIdDesc(user,
						PageRequest.of(pageNumber, pageSize))
				.map(UserBetPredictionDto::fromEntity);
	}

	public Page<UserScoreBetDto> getUserScore(Long userId, Long user, Boolean ended, String sport, int pageNumber,
			int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		LocalDateTime date = LocalDateTime.now();
		PageRequest pageable = PageRequest.of(pageNumber, pageSize);
		if (user == null || user == userId) {
			if (ended == null) {
				if (sport == null) {
					return userScoreBetRepository.findAllByUserId(userId, pageable)
							.map(UserScoreBetDto::fromEntity);
				} else
					return userScoreBetRepository.findAllByUserIdAndBetGameSportNameOrderByIdDesc(userId, sport, pageable)
							.map(UserScoreBetDto::fromEntity);
			}
			if (sport == null) {
				if (ended == true)
					return userScoreBetRepository.findAllByUserIdAndBetStopDateBeforeOrderByIdDesc(userId, date, pageable)
							.map(UserScoreBetDto::fromEntity);
				else
					return userScoreBetRepository.findAllByUserIdAndBetStopDateAfterOrderByIdDesc(userId, date, pageable)
							.map(UserScoreBetDto::fromEntity);
			} else {
				if (ended == true)
					return userScoreBetRepository
							.findAllByUserIdAndBetGameSportNameAndBetStopDateBeforeOrderByIdDesc(userId, sport, date, pageable)
							.map(UserScoreBetDto::fromEntity);
				else
					return userScoreBetRepository
							.findAllByUserIdAndBetGameSportNameAndBetStopDateAfterOrderByIdDesc(userId, sport, date, pageable)
							.map(UserScoreBetDto::fromEntity);
			}
		}
		if (!isPublic(userId, user))
			throw new BadRequestException("this account is private");
		if (ended == null) {
			if (sport == null)
				return userScoreBetRepository.findAllByUserId(user, pageable)
						.map(UserScoreBetDto::fromEntity);
			else
				return userScoreBetRepository.findAllByUserIdAndBetGameSportNameOrderByIdDesc(user, sport, pageable)
						.map(UserScoreBetDto::fromEntity);
		}
		if (sport == null) {
			if (ended == true)
				return userScoreBetRepository.findAllByUserIdAndBetStopDateBeforeOrderByIdDesc(user, date, pageable)
						.map(UserScoreBetDto::fromEntity);
			else
				return userScoreBetRepository.findAllByUserIdAndBetStopDateAfterOrderByIdDesc(user, date, pageable)
						.map(UserScoreBetDto::fromEntity);
		} else {
			if (ended == true)
				return userScoreBetRepository
						.findAllByUserIdAndBetGameSportNameAndBetStopDateBeforeOrderByIdDesc(user, sport, date, pageable)
						.map(UserScoreBetDto::fromEntity);
			else
				return userScoreBetRepository
						.findAllByUserIdAndBetGameSportNameAndBetStopDateAfterOrderByIdDesc(user, sport, date, pageable)
						.map(UserScoreBetDto::fromEntity);
		}
	}

	public Page<UserWinBetDto> getUserWin(Long userId, Long user, Boolean ended, String sport, int pageNumber,
			int pageSize) {
		PagePropertiesValidator.validate(pageNumber, pageSize);
		LocalDateTime date = LocalDateTime.now();
		PageRequest pageable = PageRequest.of(pageNumber, pageSize);
		if (user == null || user == userId) {
			if (ended == null) {
				if (sport == null) {
					return userWinBetRepository.findAllByUserId(userId, pageable)
							.map(UserWinBetDto::fromEntity);
				} else
					return userWinBetRepository.findAllByUserIdAndBetGameSportNameOrderByIdDesc(userId, sport, pageable)
							.map(UserWinBetDto::fromEntity);
			}
			if (sport == null) {
				if (ended == true)
					return userWinBetRepository.findAllByUserIdAndBetStopDateBeforeOrderByIdDesc(userId, date, pageable)
							.map(UserWinBetDto::fromEntity);
				else
					return userWinBetRepository.findAllByUserIdAndBetStopDateAfterOrderByIdDesc(userId, date, pageable)
							.map(UserWinBetDto::fromEntity);
			} else {
				if (ended == true)
					return userWinBetRepository
							.findAllByUserIdAndBetGameSportNameAndBetStopDateBeforeOrderByIdDesc(userId, sport, date, pageable)
							.map(UserWinBetDto::fromEntity);
				else
					return userWinBetRepository
							.findAllByUserIdAndBetGameSportNameAndBetStopDateAfterOrderByIdDesc(userId, sport, date, pageable)
							.map(UserWinBetDto::fromEntity);
			}
		}
		if (!isPublic(userId, user))
			throw new BadRequestException("this account is private");
		if (ended == null) {
			if (sport == null)
				return userWinBetRepository.findAllByUserId(user, pageable)
						.map(UserWinBetDto::fromEntity);
			else
				return userWinBetRepository.findAllByUserIdAndBetGameSportNameOrderByIdDesc(user, sport, pageable)
						.map(UserWinBetDto::fromEntity);
		}
		if (sport == null) {
			if (ended == true)
				return userWinBetRepository.findAllByUserIdAndBetStopDateBeforeOrderByIdDesc(user, date, pageable)
						.map(UserWinBetDto::fromEntity);
			else
				return userWinBetRepository.findAllByUserIdAndBetStopDateAfterOrderByIdDesc(user, date, pageable)
						.map(UserWinBetDto::fromEntity);
		} else {
			if (ended == true)
				return userWinBetRepository
						.findAllByUserIdAndBetGameSportNameAndBetStopDateBeforeOrderByIdDesc(user, sport, date, pageable)
						.map(UserWinBetDto::fromEntity);
			else
				return userWinBetRepository
						.findAllByUserIdAndBetGameSportNameAndBetStopDateAfterOrderByIdDesc(user, sport, date, pageable)
						.map(UserWinBetDto::fromEntity);
		}
	}
}
