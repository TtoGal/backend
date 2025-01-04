package com.ttogal.common.util;


import com.ttogal.common.exception.ExceptionCode;
import com.ttogal.common.exception.refresh.RefreshTokenException;
import com.ttogal.common.exception.user.UserException;
import com.ttogal.domain.refresh.entity.RefreshToken;
import com.ttogal.domain.refresh.repository.RefreshTokenRepository;
import com.ttogal.domain.user.entity.User;
import com.ttogal.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.text.DefaultEditorKit;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class JwtService {
  @Value("${jwt.secretKey}")
  private String secretKey;

  @Value("${jwt.access.expiration}")
  private Long accessTokenExpirationPeriod;

  @Value("${jwt.refresh.expiration}")
  private Long refreshTokenExpirationPeriod;

  @Value("${jwt.access.header}")
  private String accessHeader;

  @Value("${jwt.refresh.header}")
  private String refreshHeader;

  private static final String BEARER = "Bearer ";

  private final UserRepository userRepository;

  private final RefreshTokenRepository refreshTokenRepository;

  public String createAccessToken(String username, String role) {
    return Jwts.builder()
            .claim("category","AccessToken")
            .claim("username",username)
            .claim("role",role)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis()+accessTokenExpirationPeriod))
            .signWith(getSecretKey())
            .compact();
  }

  @Transactional
  public String createRefreshToken(String username, String role) {
   String refreshToken=generateRefreshToken(username, role);
    saveRefreshToken(username, refreshToken);
    return refreshToken;
  }

  @Transactional(readOnly = true)
  public void reIssueAccessToken(HttpServletResponse response,String refreshToken) {
    log.info("Access Token 재발급 시도: {}", refreshToken);
    refreshTokenRepository.findByRefreshToken(refreshToken)
            .ifPresentOrElse(token->{
              String username = token.getAuthKey();
              User user= userRepository.findByEmail(username)
                      .orElseThrow(()-> new UserException(ExceptionCode.NOT_FOUND_USER));
              String newAccessToken = this.createAccessToken(username, user.getRole().toString());
              this.setAccessTokenHeader(response,newAccessToken);

              log.info("Access Token 재발급 성공");
            },()->{
              log.warn("Access Token 재발급 실패");
              throw new RefreshTokenException(ExceptionCode.NOT_FOUND_REFRESH_TOKEN);
            });
  }

  public Optional<String> getUsername(String token){
    return Optional.ofNullable(getClaimsJws(token).getPayload().get("username", String.class));
  }

  public String getRole(String token){
    return getClaimsJws(token).getPayload().get("role", String.class);
  }

  public boolean isTokenValid(String token) {
    getClaimsJws(token);
    return true;
  }


  public Optional<String> extractAccessToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(accessHeader))
            .filter(accessToken -> accessToken.startsWith(BEARER))
            .map(accessToken -> accessToken.replace(BEARER, ""));
  }

  public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
    response.setStatus(HttpServletResponse.SC_OK);
    setAccessTokenHeader(response,accessToken);
    setRefreshTokenCookie(response,refreshToken);
  }

  private void saveRefreshToken(String username, String token) {
    RefreshToken refreshToken = RefreshToken.builder()
            .refreshToken(token)
            .authKey(username)
            .build();
    refreshTokenRepository.save(refreshToken);
  }

  private String generateRefreshToken(String username, String role) {
    return Jwts.builder()
            .claim("category", "RefreshToken")
            .claim("username", username)
            .claim("role", role)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod))
            .compact();
  }

  private SecretKey getSecretKey(){
    return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
  }


  private Jws<Claims> getClaimsJws(String token) {
    return Jwts.parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token);
  }


  private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
    response.addHeader(accessHeader, BEARER + accessToken);
  }

  private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    Cookie cookie = new Cookie(refreshHeader, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (refreshTokenExpirationPeriod / 1000));
    response.addCookie(cookie);
  }
}
