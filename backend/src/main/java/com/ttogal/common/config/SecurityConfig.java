package com.ttogal.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttogal.api.service.user.CustomUserDetailsService;
import com.ttogal.common.filter.JwtFilter;
import com.ttogal.common.filter.LoginFilter;
import com.ttogal.common.handler.login.LoginFailureHandler;
import com.ttogal.common.handler.login.LoginSuccessHandler;
import com.ttogal.common.util.JwtService;
import com.ttogal.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final ObjectMapper objectMapper;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http
            .cors((AbstractHttpConfigurer::disable))
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .formLogin(FormLoginConfigurer<HttpSecurity>::disable)
            .httpBasic(HttpBasicConfigurer<HttpSecurity>::disable)
            .headers(it -> it.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .sessionManagement(it ->
                    it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(
                    authorize -> authorize
                            .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                            .requestMatchers("/static/**", "/templates/**").permitAll()
                            .requestMatchers("/h2-console/**").permitAll()
                            //user
                            .requestMatchers("/api/v1/users/login", "/api/v1/users/register","api/v1/users/validate-nickname","api/v1/users/validate-email").permitAll()
                            .requestMatchers("/api/v1/users/**").authenticated()
                            //email
                            .requestMatchers("/api/v1/email/**").permitAll()
                            //admin
                            .requestMatchers("/admin").hasRole("ADMIN")
                            .anyRequest().permitAll()
            )
            .addFilterBefore(new JwtFilter(jwtService,userRepository), UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(loginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }


  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(customUserDetailsService);
    return new ProviderManager(provider);
  }


  @Bean
  LoginFilter loginAuthenticationFilter() {
    LoginFilter loginFilter
            = new LoginFilter(objectMapper, jwtService);
    loginFilter.setAuthenticationManager(authenticationManager());
    loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
    loginFilter.setAuthenticationFailureHandler(new LoginFailureHandler());
    return loginFilter;
  }

  @Bean
  LoginSuccessHandler loginSuccessHandler() {
    return new LoginSuccessHandler(jwtService);
  }

  @Bean
  LoginFailureHandler loginFailureHandler() {
    return new LoginFailureHandler();
  }
}
