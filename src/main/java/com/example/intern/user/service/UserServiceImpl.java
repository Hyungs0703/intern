package com.example.intern.user.service;

import com.example.intern.global.exception.ErrorCode;
import com.example.intern.global.exception.GlobalException;
import com.example.intern.jwt.JwtUtil;
import com.example.intern.user.dto.LoginRequestDto;
import com.example.intern.user.dto.SignUpRequestDto;
import com.example.intern.user.dto.UserResponseDto;
import com.example.intern.user.entity.User;
import com.example.intern.user.entity.UserRole;
import com.example.intern.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public UserResponseDto signUp(SignUpRequestDto signUpRequestDto) {
		checkUsername(signUpRequestDto);
		User user = createUser(signUpRequestDto);
		userRepository.save(user);

		return new UserResponseDto(user);
	}

	@Override
	public String login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
		User user = validateLoginRequest(loginRequestDto);
		return jwtUtil.issueTokens(user, response);
	}

	private void checkUsername(SignUpRequestDto signUpRequestDto) {
		if (userRepository.findByUsername(signUpRequestDto.getUsername()).isPresent()) {
			throw new GlobalException(ErrorCode.ALREADY_USERNAME);
		}
	}

	private User createUser(SignUpRequestDto signUpRequestDto) {
		return User.builder()
			.username(signUpRequestDto.getUsername())
			.password(passwordEncoder.encode(signUpRequestDto.getPassword()))
			.nickname(signUpRequestDto.getNickname())
			.userRole(UserRole.USER)
			.build();
	}

	private User validateLoginRequest(LoginRequestDto loginRequestDto) {
		User user = userRepository.findByUsername(loginRequestDto.getUsername())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));
		validateUserPassword(loginRequestDto.getPassword(), user.getPassword());
		return user;
	}

	private void validateUserPassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new GlobalException(ErrorCode.INVALID_PASSWORD);
		}
	}
}

