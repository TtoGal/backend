package com.ttogal.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttogal.api.controller.user.dto.request.LoginRequestDto;
import com.ttogal.common.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;


@Slf4j
public class LoginFilter extends AbstractAuthenticationProcessingFilter {
  private final static String REQUEST_URL = "/api/v1/users/login";
  private final static String HTTP_METHOD = "POST";

  private static final AntPathRequestMatcher PATH_REQUEST_MATCHER = new AntPathRequestMatcher(REQUEST_URL, HTTP_METHOD);

  private final ObjectMapper objectMapper;
  private final JwtService jwtService;

  public LoginFilter(ObjectMapper objectMapper, JwtService jwtService) {
    super(PATH_REQUEST_MATCHER);
    this.objectMapper = objectMapper;
    this.jwtService = jwtService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
    LoginRequestDto dto = objectMapper.readValue(request.getInputStream(),
            LoginRequestDto.class);
    log.info("인증 시도 유저:{}", dto.email());
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
    return this.getAuthenticationManager().authenticate(authenticationToken);
  }

}
