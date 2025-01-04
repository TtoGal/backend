package com.ttogal.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

  //USER ERROR
  NOT_FOUND_USER(404, "존재하지 않는 멤버입니다."),
  DUPLICATED_USER_EMAIL(400, "이미 존재하는 이메일입니다."),
  DUPLICATED_USER_NICKNAME(400, "이미 존재하는 닉네임입니다."),
  INVALID_PASSWORD(400, "일치하지 않는 패스워드입니다."),

  //EMAIL ERROR
  NOT_FOUND_EMAIL(404,"이메일이 존재하지 않습니다."),
  NOT_FOUND_KEY(404,"Redis에 존재하지 않는 키입니다."),
  EMAIL_SENDING_ERROR(500, "메일 발송 중 오류가 발생했습니다."),

  // JWT ERROR
  JWT_TOKEN_EXPIRED(401, "JWT 토큰이 만료되었습니다."),
  JWT_TOKEN_UNSUPPORTED(401, "지원하지 않는 JWT 토큰입니다."),
  JWT_TOKEN_MALFORMED(401, "형식이 잘못된 JWT 토큰입니다."),
  JWT_SIGNATURE_INVALID(401, "JWT 서명이 유효하지 않습니다."),
  JWT_ARGUMENT_INVALID(401, "JWT 인자가 잘못되었습니다."),
  JWT_GENERAL_ERROR(401, "JWT 처리 중 오류가 발생했습니다."),

  // REFRESH TOKEN ERROR
  NOT_FOUND_REFRESH_TOKEN(404, "존재하지 않는 리프레시 토큰입니다.");


  private final int code;
  private final String message;
}
