package com.ttogal.common.handler.login;

import com.ttogal.api.controller.user.dto.response.CustomUserDetails;
import com.ttogal.common.util.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final JwtService jwtService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    String username = extractUsername(authentication);
    String role = extractRole(authentication);

    String accessToken = jwtService.createAccessToken(username, role);
    String refreshToken = jwtService.createRefreshToken(username, role);

    jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

    log.info("로그인 성공: {}", username);
    log.info("Access & refresh 토큰 생성");
  }

  private String extractUsername(Authentication authentication) {
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    return customUserDetails.getUsername();
  }

  private String extractRole(Authentication authentication) {
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();
    return role;
  }
}
