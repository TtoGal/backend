package com.ttogal.api.controller.user;

import com.ttogal.api.controller.user.dto.request.LoginRequestDto;
import com.ttogal.api.controller.user.dto.request.UserRegisterRequestDto;
import com.ttogal.api.controller.user.dto.request.ValidateEmailRequestDto;
import com.ttogal.api.controller.user.dto.request.ValidateNicknameRequestDto;
import com.ttogal.api.controller.user.dto.response.UserRegisterResponseDto;
import com.ttogal.api.controller.user.dto.response.UserResponseDto;
import com.ttogal.api.service.user.UserService;
import com.ttogal.common.handler.user.UserResponseHandler;
import com.ttogal.common.util.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

  private final UserService userService;
  private final UserResponseHandler userResponseHandler;
  private final JwtService jwtService;

  @PostMapping("/login")
  @Operation(
          summary = "로그인",
          description = "이메일과 비밀번호로 사용자를 인증합니다.",
          responses = {
                  @ApiResponse(responseCode = "200",description = "로그인 성공"),
                  @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
          }
  )
  public void login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
  }


  @PostMapping("/register")
  @Operation(
          summary = "회원가입",
          description = "새로운 사용자를 등록합니다.",
          responses = {
                  @ApiResponse(responseCode = "200",description = "회원가입 성공"),
                  @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
          }
  )
  public ResponseEntity<UserRegisterResponseDto> register(
          @RequestBody @Valid UserRegisterRequestDto dto) {
    Long userId = userService.register(dto);
    UserRegisterResponseDto responseDto = userResponseHandler.createRegisterResponse(userId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @PostMapping("/validate-email")
  @Operation(
          summary = "이메일 유효성 검증",
          description = "사용자가 입력한 이메일의 유효성을 검증합니다.",
          responses = {
                  @ApiResponse(responseCode = "200",description = "이메일 검증 성공"),
                  @ApiResponse(responseCode = "400",description = "잘못된 이메일 형식")
          }
  )
  public ResponseEntity<UserResponseDto> validateEmail(
          @RequestBody @Valid ValidateEmailRequestDto requestDto) {
    userService.validateEmail(requestDto.email());
    UserResponseDto responseDto = userResponseHandler.createUserResponse("이메일 검증이 완료되었습니다.");
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @PostMapping("/validate-nickname")
  @Operation(
          summary = "닉네임 유효성 검증",
          description = "사용자가 입력한 닉네임의 유효성을 검증합니다.",
          responses = {
                  @ApiResponse(responseCode = "200",description = "닉네임 검증 성공"),
                  @ApiResponse(responseCode = "400",description = "잘못된 닉네임 형식")
          }
  )
  public ResponseEntity<UserResponseDto> validateNickname(
          @RequestBody @Valid ValidateNicknameRequestDto requestDto) {
    userService.validateNickname(requestDto.nickname());
    UserResponseDto responseDto = userResponseHandler.createUserResponse("사용가능한 닉네임입니다.");
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<Void> getUser(@PathVariable("userId") Long userId){
    System.out.println("userId: " + userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/reissue")
  public ResponseEntity<Void> reissueAccessToken(@CookieValue(value = "Authorization-refresh",defaultValue = "") String refreshToken, HttpServletResponse response){
    jwtService.reIssueAccessToken(response, refreshToken);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
