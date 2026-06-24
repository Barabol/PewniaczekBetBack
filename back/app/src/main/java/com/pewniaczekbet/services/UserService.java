package com.pewniaczekbet.services;

import java.util.List;
import java.util.Optional;

import com.pewniaczekbet.dto.FollowDto;
import com.pewniaczekbet.dto.LoginUserDto;
import com.pewniaczekbet.dto.NewUserDto;
import com.pewniaczekbet.dto.PaymentDto;
import com.pewniaczekbet.dto.UserDto;
import com.pewniaczekbet.model.dao.FollowerRepository;
import com.pewniaczekbet.model.dao.PaymentRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.entities.FollowEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;
import com.pewniaczekbet.other.ApplicationLimitations;
import com.pewniaczekbet.other.FollowId;
import com.pewniaczekbet.other.PagePropertiesValidator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * UserService
 */
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final FollowerRepository followRepository;
	private final PaymentRepository paymentRepository;

	@PersistenceContext
	private EntityManager entityManager;

	// TODO: add regex validation

	public List<UserDto> getUsers() {
		List<UserEntity> users = userRepository.findAll();
		return UserDto.fromEntity(users);
	}

	public UserDto createUser(NewUserDto user) {
		UserEntity entity = user.toEntity();
		entity.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt()));
		entity.setWins(0L);
		entity.setLosses(0L);
		entity.setWinsAmount(0L);
		entity.setLossesAmount(0L);
		entity.setBalance(0L);
		entity.setFreeBetBalance(ApplicationLimitations.NewAccountFreeBet);
		entity.setPublic(true);
		entity.setAccountTypeId(0L);

		try {
			return UserDto.fromEntity(userRepository.save(entity));
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException("bad request");
		}
	}

	public UserDto login(LoginUserDto user) {
		UserEntity entity = userRepository.findByEmail(user.getEmail());
		if (entity == null)
			throw new BadRequestException("bad email or password");

		if (BCrypt.checkpw(user.getPassword(), entity.getPassword()))
			return UserDto.fromEntity(entity);
		else
			throw new BadRequestException("bad email or password");

	}

	public Page<UserDto> getFollowers(Long userId, int page, int pageSize) {
		PagePropertiesValidator.validate(page, pageSize);
		Page<FollowDto> entities = followRepository.findByFollowedId(userId, PageRequest.of(page, pageSize))
				.map(FollowDto::fromEntity);
		return entities.map(FollowDto::getFollower);
	}

	public Page<UserDto> getFollowed(Long userId, int page, int pageSize) {
		PagePropertiesValidator.validate(page, pageSize);
		Page<FollowDto> entities = followRepository.findByFollowerId(userId, PageRequest.of(page, pageSize))
				.map(FollowDto::fromEntity);
		return entities.map(FollowDto::getFollowed);
	}

	public void follow(Long followerId, Long followedId) {
		if (followerId == null || followedId == null || followerId == followedId)
			throw new BadRequestException("bad user id");

		FollowId followId = new FollowId();
		followId.setFollowed(followedId);
		followId.setFollower(followerId);

		if (followRepository.findById(followId).isPresent())
			throw new BadRequestException("you are already following this user");

		FollowEntity follow = new FollowEntity();
		follow.setFollowed(userRepository.findById(followedId).orElseThrow(() -> new BadRequestException("bad user id")));
		follow.setFollower(entityManager.find(UserEntity.class, followerId));

		followRepository.save(follow);
	}

	public void unfollow(Long followerId, Long followedId) {
		if (followerId == null || followedId == null || followerId == followedId)
			throw new BadRequestException("bad user id");

		FollowId followId = new FollowId();
		followId.setFollowed(followedId);
		followId.setFollower(followerId);

		if (followRepository.findById(followId).isPresent())
			followRepository.deleteById(followId);
		else
			throw new BadRequestException("unable to unfollow not followed user");
	}

	public UserDto toggleVisibility(Long userId) {
		Optional<UserEntity> user = userRepository.findById(userId);
		if (!user.isPresent())
			throw new BadRequestException("unable to find user");
		UserEntity entity = user.get();
		entity.setPublic(!entity.isPublic());
		userRepository.save(entity);
		return UserDto.fromEntity(entity);
	}

	public UserDto getDetails(Long requestId, Long userId) {
		if (userId == null)
			userId = requestId;
		Optional<UserEntity> user = userRepository.findById(userId);
		if (!user.isPresent())
			throw new BadRequestException("unable to find user");
		UserEntity entity = user.get();
		UserDto dto = UserDto.fromEntity(entity);
		if (userId == requestId)
			return dto;
		else if (!entity.isPublic()) {
			FollowId follow = new FollowId();
			follow.setFollower(userId);
			follow.setFollowed(requestId);

			if (followRepository.findById(follow).isPresent())
				return dto;

			dto.setBalance(0L);
			dto.setFreeBetBalance(0L);
			dto.setWinsAmount(0L);
			dto.setLossesAmount(0L);
			dto.setWins(0L);
			dto.setLosses(0L);
		}
		return dto;
	}

	public Page<PaymentDto> getUserPayments(Long userId, int page, int pageSize) {
		PagePropertiesValidator.validate(page, pageSize);
		return paymentRepository.findByUserId(userId, PageRequest.of(page, pageSize)).map(PaymentDto::fromEntity);
	}
}
