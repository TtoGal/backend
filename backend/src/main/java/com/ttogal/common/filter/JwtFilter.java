package com.ttogal.common.filter;

import com.ttogal.api.controller.user.dto.response.CustomUserDetails;
import com.ttogal.common.exception.ExceptionCode;
import com.ttogal.common.util.JwtService;
import com.ttogal.domain.user.entity.User;
import com.ttogal.domain.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
  private final static String NO_CHECK_URL="/api/v1/users/login";
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      if (isRequestAuthenticated(request)) {
        filterChain.doFilter(request, response);
        return;
      }
      authenticateAccessToken(request, response, filterChain);
    }catch (ExpiredJwtException e){
      jwtExceptionHandler(response,ExceptionCode.JWT_TOKEN_EXPIRED);
    }catch(UnsupportedJwtException e){
      jwtExceptionHandler(response,ExceptionCode.JWT_TOKEN_UNSUPPORTED);
    }catch(MalformedJwtException e){
      jwtExceptionHandler(response,ExceptionCode.JWT_TOKEN_MALFORMED);
    }catch(SignatureException e){
      jwtExceptionHandler(response,ExceptionCode.JWT_SIGNATURE_INVALID);
    }catch (IllegalArgumentException e) {
      jwtExceptionHandler(response, ExceptionCode.JWT_ARGUMENT_INVALID);
    } catch(JwtException e){
      jwtExceptionHandler(response,ExceptionCode.JWT_GENERAL_ERROR);
    }catch(Exception e){
      log.error("예기치 않은 오류 발생: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setCharacterEncoding("UTF-8");
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write("{\"error\":\"예기치 않은 오류가 발생했습니다.\"}");
    }
  }
  private void jwtExceptionHandler(HttpServletResponse response,ExceptionCode exceptionCode) throws IOException {
    log.error(">>>> [ JWT 토큰 인증 중 Error 발생 : {} ] <<<<",exceptionCode.getMessage());

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setCharacterEncoding("utf-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write("{\"error\":\"" + exceptionCode.getMessage() + "\"}");
  }
  private void authenticateAccessToken(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {
    log.info("인증 시도");
    jwtService.extractAccessToken(request)
            .filter(jwtService::isTokenValid)
            .flatMap(jwtService::getUsername)
            .flatMap(userRepository::findByEmail)
            .ifPresent(this::saveAuthentication);

    filterChain.doFilter(request, response);
  }

  private void saveAuthentication(User user) {
    CustomUserDetails customUserDetails = new CustomUserDetails(user);
    Authentication authToken=new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);
    log.info("인증 완료 유저: {}", user.getEmail());
  }

  public boolean isRequestAuthenticated(HttpServletRequest request) {
  Optional<String>accessToken=jwtService.extractAccessToken(request);
  return accessToken.isEmpty()||isLoginRequest(request);
}

  public boolean isLoginRequest(HttpServletRequest request) {
    return request.getRequestURI().equals(NO_CHECK_URL)&&"POST".equalsIgnoreCase(
            request.getMethod());
  }


}
