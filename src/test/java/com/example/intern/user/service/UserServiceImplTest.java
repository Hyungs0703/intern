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
}
