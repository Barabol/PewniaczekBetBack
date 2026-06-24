package com.pewniaczekbet.rest;

import java.util.List;

import com.pewniaczekbet.dto.LoginUserDto;
import com.pewniaczekbet.dto.NewUserDto;
import com.pewniaczekbet.dto.UserDto;
import com.pewniaczekbet.model.exceptions.BadPermissionException;
import com.pewniaczekbet.model.exceptions.NotLoggedInException;
import com.pewniaczekbet.services.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserControler
 */
@RestController
@RequestMapping("/api/user")
public class UserControler {

	public static Long getUserId(HttpSession session) {
		Long userId = (Long) session.getAttribute("id");
		if (userId == null)
			throw new NotLoggedInException();
		return userId;
	}

	public static Long isAdmin(HttpSession session) {
		Long userId = (Long) session.getAttribute("id");
		if (userId == null)
			throw new NotLoggedInException();
		Long permission = (Long) session.getAttribute("type");
		if (permission != 2)
			throw new BadPermissionException();
		return userId;
	}

	public static Long isWorker(HttpSession session) {
		Long userId = (Long) session.getAttribute("id");
		if (userId == null)
			throw new NotLoggedInException();
		Long permission = (Long) session.getAttribute("type");
		if (permission != 2 || permission != 1)
			throw new BadPermissionException();
		return userId;
	}

	private final UserService userService;

	public UserControler(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/all") // TODO: remove
	public ResponseEntity<List<UserDto>> getUsers() {
		return ResponseEntity.ok(userService.getUsers());
	}

	@PostMapping("/register")
	public ResponseEntity<UserDto> createUser(HttpSession session, @RequestBody @Validated NewUserDto user) {
		ResponseEntity<UserDto> ret = ResponseEntity.status(HttpStatus.OK).body(userService.createUser(user));
		session.setAttribute("id", ret.getBody().getId());
		session.setAttribute("type", ret.getBody().getAccountTypeId());
		return ret;
	}

	@PostMapping("/login")
	public ResponseEntity<UserDto> loginUser(HttpSession session, @RequestBody @Validated LoginUserDto user) {
		ResponseEntity<UserDto> ret = ResponseEntity.status(HttpStatus.OK).body(userService.login(user));
		session.setAttribute("id", ret.getBody().getId());
		session.setAttribute("type", ret.getBody().getAccountTypeId());
		return ret;
	}

	@GetMapping("/logout")
	public ResponseEntity<String> logoutUsers(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok("OK");
	}

	@PostMapping("/follow")
	public ResponseEntity<String> followUser(HttpSession session, @RequestBody @Validated Long userId) {
		Long followerId = UserControler.getUserId(session);
		userService.follow(followerId, userId);
		return ResponseEntity.status(HttpStatus.OK).body("Ok");
	}

	@DeleteMapping("/follow")
	public ResponseEntity<String> unfollowUser(HttpSession session, @RequestParam(required = true) Long userId) {
		Long followerId = UserControler.getUserId(session);
		userService.unfollow(followerId, userId);
		return ResponseEntity.status(HttpStatus.OK).body("Ok");
	}

	@GetMapping("/followers")
	public ResponseEntity<Page<UserDto>> followers(HttpSession session, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize) {
		Long userId = UserControler.getUserId(session);
		return ResponseEntity.status(HttpStatus.OK).body(userService.getFollowers(userId, page, pageSize));
	}

	@GetMapping("/followed")
	public ResponseEntity<Page<UserDto>> followerd(HttpSession session, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int pageSize) {
		Long userId = UserControler.getUserId(session);
		return ResponseEntity.status(HttpStatus.OK).body(userService.getFollowed(userId, page, pageSize));
	}

	@GetMapping("/toggleVisibility")
	public ResponseEntity<UserDto> visibilty(HttpSession session) {
		Long userId = UserControler.getUserId(session);
		return ResponseEntity.status(HttpStatus.OK).body(userService.toggleVisibility(userId));
	}

	@GetMapping("/details")
	public ResponseEntity<UserDto> userDetails(HttpSession session, @RequestParam(required = false) Long userId) {
		Long followerId = UserControler.getUserId(session);
		return ResponseEntity.status(HttpStatus.OK).body(userService.getDetails(followerId, userId));
	}

	@GetMapping("/auth")
	public ResponseEntity<Boolean> isLoggedIn(HttpSession session) {
		try {
			UserControler.getUserId(session);
		} catch (NotLoggedInException e) {
			return ResponseEntity.status(HttpStatus.OK).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
}
