package com.example.intern.user.controller;

import com.example.intern.global.dto.CommonResponseDto;
import com.example.intern.user.dto.LoginRequestDto;
import com.example.intern.user.dto.SignUpRequestDto;
import com.example.intern.user.dto.UserResponseDto;
import com.example.intern.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<CommonResponseDto<UserResponseDto>> signUp(
		@RequestBody @Valid SignUpRequestDto signUpRequestDto
	) {
		UserResponseDto userResponseDto = userService.signUp(signUpRequestDto);
		return ResponseEntity.ok().body(new CommonResponseDto<>
			(HttpStatus.CREATED.value(), "회원가입이 완료되었습니다.", userResponseDto));
	}

	@PostMapping("/login")
	public ResponseEntity<CommonResponseDto<String>> login(
		@RequestBody @Valid LoginRequestDto loginRequestDto,
		HttpServletResponse response
	) {
		String token = userService.login(loginRequestDto, response);
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "로그인이 완료되었습니다.", token));
	}
}
