package com.leverx.RatingSystemRest.Infrastructure;

import jakarta.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global exception handler for the entire application.
 * This class captures and handles exceptions thrown across all controllers using
 * Spring's {@link RestControllerAdvice}. It provides customized responses for common
 * exception types such as validation errors, IO exceptions, database exceptions, and
 * more.
 */
@RestControllerAdvice
@Builder
public class GlobalExceptionHandler {

  /**
   * Handles validation errors thrown when request parameters fail validation.
   *
   * @param ex the {@link MethodArgumentNotValidException} containing validation details
   * @return a map of field names to validation error messages
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }
    return ResponseEntity.badRequest().body(errors);
  }

  /**
   * Handles general uncaught exceptions.
   *
   * @param ex the exception thrown
   * @return a generic error message with HTTP 500 status
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + ex.getMessage());
  }

  /**
   * Handles exceptions thrown manually using {@link ResponseStatusException}.
   *
   * @param ex the exception containing status and reason
   * @return a response with the status code and reason message
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
    return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
  }

  /**
   * Handles exceptions that occur during email sending.
   *
   * @param ex the {@link MessagingException}
   * @return a response indicating email failure
   */
  @ExceptionHandler(MessagingException.class)
  public ResponseEntity<String> handleMessagingException(MessagingException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An error occurred while sending the email: " + ex.getMessage());
  }

  /**
   * Handles IO-related exceptions (e.g., file saving issues).
   *
   * @param ex the {@link IOException}
   * @return a response indicating file-related failure
   */
  @ExceptionHandler(IOException.class)
  public ResponseEntity<String> handleFileException(IOException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An error occurred while saving file: " + ex.getMessage());
  }

  /**
   * Handles file-not-found exceptions.
   *
   * @param ex the {@link FileNotFoundException}
   * @return a response indicating the file was not found
   */
  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<String> handleFileNotFoundExceptionException(FileNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ex.getMessage());
  }

  /**
   * Handles SQL/database-related exceptions.
   *
   * @param ex the {@link SQLException}
   * @return a response indicating a database error occurred
   */
  @ExceptionHandler(SQLException.class)
  public ResponseEntity<String> sqlExceptionHelperExceptionException(SQLException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ex.getMessage());
  }
}
