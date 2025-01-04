package com.ttogal.domain.refresh.repository;

import com.ttogal.domain.refresh.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
