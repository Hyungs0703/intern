package com.example.intern.user.service;

import com.example.intern.user.dto.LoginRequestDto;
import com.example.intern.user.dto.SignUpRequestDto;
import com.example.intern.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

	UserResponseDto signUp(SignUpRequestDto signUpRequestDto);

	String login(LoginRequestDto loginRequestDto, HttpServletResponse response);
}
