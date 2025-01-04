package com.ttogal.domain.refresh.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refershToken", timeToLive=60 * 60 * 24 * 14)
public class RefreshToken {
  @Id
  private String refreshToken;

  @Indexed
  private String authKey;

  @Builder
  public RefreshToken(String refreshToken, String authKey) {
    this.refreshToken = refreshToken;
    this.authKey = authKey;
  }
}
