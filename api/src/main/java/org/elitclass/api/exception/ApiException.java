package org.elitclass.api.exception;

public class ApiException extends RuntimeException {
  public ApiException(String message) {
    super(message);
  }
}
