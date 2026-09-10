package com.aau.satellite.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    IllegalArgumentException.class,
    IllegalStateException.class,
    SecurityException.class
  })
  public String handle(RuntimeException exception, Model model) {
    model.addAttribute("error", exception.getMessage());
    return "error";
  }
}
