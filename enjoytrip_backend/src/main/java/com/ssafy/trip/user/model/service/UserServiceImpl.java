package com.ssafy.trip.user.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.trip.exception.AuthorizationFailedException;
import com.ssafy.trip.exception.DatabaseRequestFailedException;
import com.ssafy.trip.exception.DuplicateUserException;
import com.ssafy.trip.exception.InvalidInputException;
import com.ssafy.trip.exception.ResourceNotFoundException;
import com.ssafy.trip.exception.util.BaseResponseCode;
import com.ssafy.trip.jwt.model.service.JwtService;
import com.ssafy.trip.user.model.LoginResponse;
import com.ssafy.trip.user.model.RefreshTokenDto;
import com.ssafy.trip.user.model.UserDto;
import com.ssafy.trip.user.model.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

	private UserMapper userDao;
	private JwtService jwtService;
	public UserServiceImpl(UserMapper userDao, JwtService jwtService) {
		super();
		this.userDao = userDao;
		this.jwtService = jwtService;
	}

	@Override
	@Transactional
	public void regi(UserDto user) {
		// 이미 아이디가 같은 유저가 있다면 안됨
		UserDto alreadyUser = getUser(user.getUserId());
		if (alreadyUser == null) {
			int result = userDao.regi(user);
			if (result == 0) {
				throw new DatabaseRequestFailedException(BaseResponseCode.DATABASE_REQUEST_FAILED);
			}
		} else {
			throw new DuplicateUserException(BaseResponseCode.DUPLICATE_USER);
		}
	}

	@Override
	@Transactional
	public LoginResponse login(UserDto user) {
		UserDto login = userDao.login(user);
		if (login == null) {
			throw new ResourceNotFoundException(BaseResponseCode.RESOURCE_NOT_FOUND);
		} else {
			// 성공 시 토큰 발급
			String accessToken = jwtService.createAccessToken("userid", login.getUserId());// key, data
			String refreshToken = jwtService.createRefreshToken("userid", login.getUserId());// key, data
			
			//refresh 토큰 재설정
			userDao.deleteRefreshToken(login.getId());
			userDao.saveRefreshToken(RefreshTokenDto.builder()
					.userId(login.getId())
					.refreshToken(refreshToken)
					.build());
			
			//리턴
			return LoginResponse.builder()
					.id(login.getId())
					.userId(login.getUserId())
					.nickname(login.getNickName())
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build();
		}
	}
	
	@Override
	@Transactional
	public void logout(String userId) {
		UserDto user = getUser(userId);
		int result = userDao.deleteRefreshToken(user.getId());
		if (result == 0) {
			throw new InvalidInputException(BaseResponseCode.INVALID_INPUT);
		}
	}

	@Override
	public UserDto loginNaver(UserDto user) {
		UserDto login = userDao.loginNaver(user);
		if (login == null) {
			throw new ResourceNotFoundException(BaseResponseCode.RESOURCE_NOT_FOUND);
		} else {
			return login;
		}
	}

	@Override
	public UserDto getUser(String userId) {
		UserDto user = userDao.getUser(userId);
		return user;
	}

	@Override
	public void updateUser(UserDto user) {
		userDao.updateUser(user);
	}

	@Override
	public void deleteUser(String userId) {
		userDao.deleteUser(userId);
	}

	@Override
	public String getPassword(String userId) {
		return userDao.getPassword(userId);
	}

	@Override
	@Transactional
	public String reAccessToken(String userId, String refreshToken) {
		String accessToken = null;
		
		UserDto user = getUser(userId);
		
		if (refreshToken.equals(userDao.getRefreshToken(user.getId()))) {
			accessToken = jwtService.createAccessToken("userid", user.getUserId());
		} else {
			throw new AuthorizationFailedException(BaseResponseCode.AUTHORIZATION_FAILED);
		}
		return accessToken;
	}
}
