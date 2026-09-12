package com.aau.satellite.web;

import com.aau.satellite.service.InvalidStateTransitionException;
import java.time.format.DateTimeParseException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
          IllegalArgumentException.class,
          IllegalStateException.class,
          SecurityException.class,
          InvalidStateTransitionException.class,
          DateTimeParseException.class
  })
  public String handle(RuntimeException exception, Model model) {
    model.addAttribute("error", exception.getMessage());
    return "error";
  }
}