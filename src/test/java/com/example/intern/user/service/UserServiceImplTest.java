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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("회원가입 성공")
	void signUp_CreateUser(){
		// given
		SignUpRequestDto signUpRequestDto = new SignUpRequestDto("testuser", "password", "nickname");
		given(userRepository.findByUsername(signUpRequestDto.getUsername())).willReturn(Optional.empty());
		given(passwordEncoder.encode(signUpRequestDto.getPassword())).willReturn("encodedPassword");

		// when
		UserResponseDto result = userService.signUp(signUpRequestDto);

		// then
		assertNotNull(result);
		assertEquals("testuser", result.getUsername());
		assertEquals("nickname", result.getNickname());

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());

		User savedUser = userCaptor.getValue();
		assertEquals("testuser", savedUser.getUsername());
		assertEquals("encodedPassword", savedUser.getPassword());
		assertEquals(UserRole.USER, savedUser.getUserRole());
	}

	@Test
	@DisplayName("이미 존재하는 사용자 명으로 예외 발생")
	void signUp_UsernameExists() {
		// given
		SignUpRequestDto signUpRequestDto = new SignUpRequestDto("testuser", "password", "nickname");
		given(userRepository.findByUsername(signUpRequestDto.getUsername())).willReturn(Optional.of(User.builder()
			.username("testuser")
			.password("password")
			.nickname("nickname")
			.userRole(UserRole.USER)
			.build()));

		// when & then
		GlobalException exception = assertThrows(GlobalException.class, () -> userService.signUp(signUpRequestDto));
		assertEquals(ErrorCode.ALREADY_USERNAME, exception.getErrorCode());
	}

	@Test
	@DisplayName("로그인 성공 테스트 코드")
	void login_Success() {
		// given
		LoginRequestDto loginRequestDto = new LoginRequestDto("testuser", "password");
		User user = User.builder()
			.username("testuser")
			.password("encodedPassword")
			.userRole(UserRole.USER)
			.build();

		given(userRepository.findByUsername(loginRequestDto.getUsername())).willReturn(Optional.of(user));
		given(passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())).willReturn(true);
		given(jwtUtil.issueTokens(any(User.class), any(HttpServletResponse.class))).willReturn("token");

		// when
		String token = userService.login(loginRequestDto, mock(HttpServletResponse.class));

		// then
		assertNotNull(token);
		assertEquals("token", token);
	}

	@Test
	@DisplayName("존재하지 않는 사용자명으로 로그인 시도시 예외 발생")
	void login_NotFoundUsername() {
		// given
		LoginRequestDto loginRequestDto = new LoginRequestDto("testuser", "password");
		given(userRepository.findByUsername(loginRequestDto.getUsername())).willReturn(Optional.empty());

		// when & then
		GlobalException exception = assertThrows(GlobalException.class, () -> userService.login(loginRequestDto, mock(HttpServletResponse.class)));
		assertEquals(ErrorCode.NOT_FOUND_USERNAME, exception.getErrorCode());
	}

	@Test
	@DisplayName("잘못된 비밀번호로 로그인을 시도시 예외 발생")
	void login_validateUserPassword() {
		// given
		LoginRequestDto loginRequestDto = new LoginRequestDto("testuser", "password");
		User user = User.builder()
			.username("testuser")
			.password("encodedPassword")
			.userRole(UserRole.USER)
			.build();
		given(userRepository.findByUsername(loginRequestDto.getUsername())).willReturn(Optional.of(user));
		given(passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())).willReturn(false);

		// when & then
		GlobalException exception = assertThrows(GlobalException.class, () -> userService.login(loginRequestDto, mock(HttpServletResponse.class)));
		assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
	}
}
