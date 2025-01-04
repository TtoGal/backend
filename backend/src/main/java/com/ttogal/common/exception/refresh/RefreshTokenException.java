package com.ttogal.common.exception.refresh;

import com.ttogal.common.exception.ExceptionCode;
import com.ttogal.common.exception.TtogalException;

public class RefreshTokenException extends TtogalException {
  public RefreshTokenException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
