package com.pewniaczekbet.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.pewniaczekbet.dto.OathDto;
import com.pewniaczekbet.model.dao.OathRepository;
import com.pewniaczekbet.model.dao.OathServiceRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.entities.OathEntity;
import com.pewniaczekbet.model.entities.OathServiceEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;
import com.pewniaczekbet.model.exceptions.InternalServerErrorException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * OathServie
 */
@Service
@RequiredArgsConstructor
public class OathServie {

	private final UserRepository userRepository;
	private final OathRepository oathRepository;
	private final OathServiceRepository oathServiceRepository;

	@Value("${OATH_CLIENT_ID}")
	private String clientId;

	@Value("${OATH_CLIENT_SECRET}")
	private String clientSecret;

	@Value("${OATH_REDIRECT}")
	private String redirectUri;

	private String scope = "user:email,read:user";

	public List<OathDto> getOath(Long userId) {
		return oathRepository.findByUserId(userId).stream().map(OathDto::fromEntity).toList();
	}

	public String getRedirectGithub(Long userId) {
		Optional<OathEntity> oathTest = oathRepository.findOneByUserIdAndServiceName(userId, "github");
		if (oathTest.isPresent())
			throw new BadRequestException("you alredy are linked to github");

		return "https://github.com/login/oauth/authorize?" +
				"client_id=" + clientId + "&redirect_uri=" + redirectUri + "github/callback"
				+ "&response_type=code&scope=" + scope;
	}

	@Transactional
	public void getCallbackGithub(String code, Long userId) {

		Optional<UserEntity> user = userRepository.findById(userId);
		if (!user.isPresent())// NOTE: kinda strange edge case
			throw new InternalServerErrorException("unable to find user");

		GetTokenRequest request = new GetTokenRequest();
		request.setCode(code);
		request.setClient_id(clientId);
		request.setRedirect_uri(redirectUri + "github/callback");
		request.setClient_secret(clientSecret);

		RestClient client = RestClient.create();
		ResponseEntity<GithubTokenEntity> es = client.post().uri("https://github.com/login/oauth/access_token")
				.header("Accept", "application/json")
				.body(request).retrieve().toEntity(GithubTokenEntity.class);

		if (es.getStatusCode().value() != 200)
			throw new InternalServerErrorException("unable to communicate to github api");

		GithubTokenEntity entity = es.getBody();
		if (entity.error != null)
			throw new BadRequestException(entity.error_description);

		Optional<OathServiceEntity> service = oathServiceRepository.findByName("github");
		if (!service.isPresent())// NOTE: service should exist
			throw new InternalServerErrorException("unable to find service");

		client = RestClient.create();
		ResponseEntity<GetGithubInfo> res = client.get().uri("https://api.github.com/user")
				.header("Authorization", "Bearer " + entity.getAccess_token())
				.header("Accept", "application/vnd.github+json")
				.retrieve().toEntity(GetGithubInfo.class);
		if (res.getStatusCode().value() != 200)
			throw new BadRequestException("bad github token");

		GetGithubInfo info = res.getBody();

		OathEntity oath = new OathEntity();
		oath.setUser(user.get());
		oath.setService(service.get());
		oath.setToken(entity.getAccess_token());
		oath.setLogin(info.getLogin());
		oath.setEmail(info.getEmail());
		oath.setAvatarUrl(info.getAvatar_url());
		oath.setUrl(info.getUrl());
		oathRepository.save(oath);
	}

	public void deleteGithub(Long userId) {
		Optional<OathEntity> entity = oathRepository.findOneByUserIdAndServiceName(userId, "github");
		if (!entity.isPresent())
			throw new BadRequestException("you are not linked to github");
		oathRepository.deleteById(entity.get().getId());
	}

	private static class GetGithubInfo {
		private String login;
		private String url;
		private String avatar_url;
		private String email;

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getUrl() {
			return url;
		}

		public String getAvatar_url() {
			return avatar_url;
		}

		public void setAvatar_url(String avatar_url) {
			this.avatar_url = avatar_url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	private class GetTokenRequest {
		private String client_id;
		private String client_secret;
		private String code;
		private String redirect_uri;

		public String getClient_id() {
			return client_id;
		}

		public void setClient_id(String client_id) {
			this.client_id = client_id;
		}

		public String getClient_secret() {
			return client_secret;
		}

		public void setClient_secret(String client_secret) {
			this.client_secret = client_secret;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getRedirect_uri() {
			return redirect_uri;
		}

		public void setRedirect_uri(String redirect_uri) {
			this.redirect_uri = redirect_uri;
		}
	}

	public static class GithubTokenEntity {
		private String access_token;
		private String token_type;
		private String scope;

		private String error;
		private String error_description;

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

		public String getError_description() {
			return error_description;
		}

		public void setError_description(String error_description) {
			this.error_description = error_description;
		}

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public String getToken_type() {
			return token_type;
		}

		public void setToken_type(String token_type) {
			this.token_type = token_type;
		}

		public String getScope() {
			return scope;
		}

		public void setScope(String scope) {
			this.scope = scope;
		}

		@Override
		public String toString() {
			return "token: " + this.access_token + "\ntokenType: " + this.token_type + "\nscope: " + this.scope
					+ "\nError?: " + this.error + "\nError Description: " + this.error_description;
		}
	}
}
