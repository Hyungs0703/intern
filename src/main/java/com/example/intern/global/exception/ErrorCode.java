package com.example.intern.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// Authorization errors
	ACCESS_DENIED(403, "접근 권한이 없습니다."),
	INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
	INVALID_REFRESH_TOKEN(401, "유효하지 않은 리프레시 토큰입니다."),
	EXPIRED_REFRESH_TOKEN(403, "토큰이 만료되었습니다."),

	// General error
	FAIL(500, "실패하였습니다.");

	private final int status;
	private final String msg;
}

