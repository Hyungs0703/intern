package com.example.intern.user.controller;

import com.example.intern.user.config.TestSecurityConfig;
import com.example.intern.user.dto.LoginRequestDto;
import com.example.intern.user.dto.SignUpRequestDto;
import com.example.intern.user.dto.UserResponseDto;
import com.example.intern.user.entity.User;
import com.example.intern.user.entity.UserRole;
import com.example.intern.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.Matchers.is;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void signUp_shouldReturnCreatedStatus() throws Exception {
		// given
		SignUpRequestDto signUpRequestDto = new SignUpRequestDto("username", "password", "nickname");

		User user = User.builder()
			.id(1L)
			.username(signUpRequestDto.getUsername())
			.password(signUpRequestDto.getPassword())
			.nickname(signUpRequestDto.getNickname())
			.userRole(UserRole.USER)
			.build();

		UserResponseDto userResponseDto = new UserResponseDto(user);

		given(userService.signUp(Mockito.any(SignUpRequestDto.class))).willReturn(userResponseDto);

		// when & then
		mockMvc.perform(post("/api/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signUpRequestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.statusCode", is(HttpStatus.CREATED.value())))
			.andExpect(jsonPath("$.msg", is("회원가입이 완료되었습니다.")))
			.andExpect(jsonPath("$.data.username", is("username")))
			.andExpect(jsonPath("$.data.nickname", is("nickname")))
			.andExpect(jsonPath("$.data.authority", is("ROLE_USER")));
	}

	@Test
	void login_shouldReturnOkStatus() throws Exception {
		// given
		LoginRequestDto loginRequestDto = new LoginRequestDto("username", "password");
		String token = "mocked-jwt-token";

		given(userService.login(Mockito.any(LoginRequestDto.class), Mockito.any())).willReturn(token);

		// when & then
		mockMvc.perform(post("/api/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.statusCode", is(HttpStatus.OK.value())))
			.andExpect(jsonPath("$.msg", is("로그인이 완료되었습니다.")))
			.andExpect(jsonPath("$.data", is(token)));
	}
}
