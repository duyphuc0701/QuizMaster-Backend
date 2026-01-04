package com.example.quizmaster.exception;

import com.example.quizmaster.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<MessageResponse> handleApiException(ApiException ex) {
        return new ResponseEntity<>(new MessageResponse(ex.getMessage()), ex.getStatus());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<MessageResponse> handleWebClientException(WebClientResponseException ex) {
        // Forward the status code from Keycloak (or other external services)
        return new ResponseEntity<>(new MessageResponse(ex.getResponseBodyAsString()), ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(new MessageResponse("An unexpected error occurred: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
