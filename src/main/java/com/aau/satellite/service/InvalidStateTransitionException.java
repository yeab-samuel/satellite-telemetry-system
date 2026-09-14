package com.aau.satellite.service;

public class InvalidStateTransitionException extends RuntimeException {
  public InvalidStateTransitionException(String m) {
    super(m);
  }
}
