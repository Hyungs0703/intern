package com.example.intern.jwt;

import com.example.intern.global.exception.ErrorCode;
import com.example.intern.global.exception.GlobalException;
import com.example.intern.user.entity.UserRole;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

	@InjectMocks
	private JwtUtil jwtUtil;

	@BeforeEach
	void setUp() {
		String key = "my-secret-key-which-needs-to-be-long-enough";
		String secretKey = Base64.getEncoder().encodeToString(key.getBytes());
		jwtUtil = new JwtUtil();
		jwtUtil.secretKey = secretKey;
		jwtUtil.accessTokenExpireTime = 600000L;
		jwtUtil.refreshTokenExpireTime = 1209600000L;
		jwtUtil.init();

	}

	@Test
	@DisplayName("유효한 사용자 정보로 Access Token 발행")
	void createAccessToken_ShouldReturnValidToken() {
		// given
		String username = "testuser";
		UserRole userRole = UserRole.USER;

		// when
		String token = jwtUtil.createAccessToken(username, userRole);

		// then
		assertNotNull(token);
	}

	@Test
	@DisplayName("유효한 사용자 정보로 Refresh Token 발행")
	void createRefreshToken_ShouldReturnValidToken() {
		// given
		String username = "testuser";

		// when
		String token = jwtUtil.createRefreshToken(username);

		// then
		assertNotNull(token);
	}

	@Test
	@DisplayName("유효한 Access Token의 검증")
	void validateToken_ShouldReturnTrueForValidToken() {
		// given
		String token = jwtUtil.createAccessToken("testuser", UserRole.USER);

		// when
		boolean isValid = jwtUtil.validateToken(token);

		// then
		assertTrue(isValid);
	}


	@Test
	@DisplayName("잘못된 Refresh Token 검증 시 예외 발생")
	void refreshAccessToken_ShouldThrowExceptionForInvalidRefreshToken() {
		// given
		String invalidToken = "invalid.token.here";

		// when & then
		GlobalException exception = assertThrows(GlobalException.class, () -> jwtUtil.refreshAccessToken(invalidToken));
		assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
	}

	@Test
	@DisplayName("헤더에서 토큰 추출 테스트")
	void getTokenFromHeader_ShouldReturnTokenWithoutBearerPrefix() {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		String token = "token";
		request.addHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + token);

		// when
		String extractedToken = jwtUtil.getTokenFromHeader(JwtUtil.AUTHORIZATION_HEADER, request);

		// then
		assertEquals(token, extractedToken);
	}

	@Test
	@DisplayName("헤더에 토큰이 없으면 null 반환")
	void getTokenFromHeader_ShouldReturnNullIfNoTokenPresent() {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();

		// when
		String extractedToken = jwtUtil.getTokenFromHeader(JwtUtil.AUTHORIZATION_HEADER, request);

		// then
		assertNull(extractedToken);
	}

	@Test
	@DisplayName("Access Token을 헤더에 추가")
	void addJwtToHeader_ShouldAddAccessTokenToHeader() {
		// given
		MockHttpServletResponse response = new MockHttpServletResponse();
		String token = "test.token";

		// when
		jwtUtil.addJwtToHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + token, response);

		// then
		assertEquals(JwtUtil.BEARER_PREFIX + token, response.getHeader(JwtUtil.AUTHORIZATION_HEADER));
	}

	@Test
	@DisplayName("Refresh Token을 쿠키에 추가")
	void addRefreshTokenCookie_ShouldAddRefreshTokenToCookie() {
		// given
		MockHttpServletResponse response = new MockHttpServletResponse();
		String refreshToken = "refresh.token";

		// when
		jwtUtil.addRefreshTokenCookie(response, refreshToken);

		// then
		assertTrue(response.getHeaders("Set-Cookie").stream().anyMatch(cookie -> cookie.contains(refreshToken)));
	}

	@Test
	@DisplayName("로그아웃 시 Access Token과 Refresh Token 삭제")
	void clearAuthToken_ShouldClearTokensFromResponse() {
		// given
		MockHttpServletResponse response = new MockHttpServletResponse();

		// when
		jwtUtil.clearAuthToken(response);

		// then
		assertEquals("", response.getHeader(JwtUtil.AUTHORIZATION_HEADER));
	}
}
