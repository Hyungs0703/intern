package com.example.intern.user.dto;

import com.example.intern.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

	private final String username;
	private final String nickname;
	private final String authority;

	public UserResponseDto(User user) {
		this.username = user.getUsername();
		this.nickname = user.getNickname();
		this.authority = user.getUserRole().getAuthority();
	}
}
