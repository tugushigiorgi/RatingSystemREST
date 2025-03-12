package com.leverx.RatingSystemRest.Infrastructure;


import com.leverx.RatingSystemRest.Presentation.Dto.ErrorResponseDto;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Builder
public class ExceptionHandlerClass {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception exp) {
        exp.printStackTrace();
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponseDto.builder()
                                .Error("Internal Server Error")
                                .Message(exp.getMessage())
                                .build()
                );
    }
}

