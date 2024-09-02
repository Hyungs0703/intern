package com.example.intern.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {
	USER(Authority.USER);

	private final String authority;

	UserRole(String authority) {
		this.authority = authority;
	}

	public static class Authority {
		public static final String USER = "ROLE_USER";
	}
}

