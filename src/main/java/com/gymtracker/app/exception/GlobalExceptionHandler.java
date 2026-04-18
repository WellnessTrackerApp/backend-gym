package com.gymtracker.app.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.gymtracker.app.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(value = {SignInException.class, SessionExpiredException.class})
    public ResponseEntity<ErrorResponse> handleSignInException(BaseKeyException e) {
        log.error("Authentication exception occurred", e);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                messageSource.getMessage(e.getKey(), e.getArgs(), LocaleContextHolder.getLocale())
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ignored) {
        log.error("User already exists exception occurred");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage("submitted-data-invalid", null, LocaleContextHolder.getLocale())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Method argument validation exception occurred", e);

        String errorMessage = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : messageSource.getMessage("validation-exception", null, LocaleContextHolder.getLocale());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.error("Validation exception occurred", e);

        String errorMessage = e.getParameterValidationResults().stream()
                .flatMap(parameterValidationResult -> parameterValidationResult.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(messageSource.getMessage("validation-exception", null, LocaleContextHolder.getLocale()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Constraint violation exception occurred", e);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestBodyJson(HttpMessageNotReadableException e) {
        log.error("Malformed JSON request", e);
        if (e.getCause() instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
                String acceptedValues = Arrays.toString(ife.getTargetType().getEnumConstants());
                ErrorResponse errorResponse = new ErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("invalid-category", new Object[] {acceptedValues}, LocaleContextHolder.getLocale())
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage("malformed-json-request", null, LocaleContextHolder.getLocale())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(value = InvalidPeriodException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {
        log.error("Bad request exception occurred", e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBaseKeyException(BaseKeyException e) {
        log.error("Exception occurred", e);
        ErrorResponse errorResponse = new ErrorResponse(
                e.getStatus(),
                messageSource.getMessage(e.getKey(), e.getArgs(), LocaleContextHolder.getLocale())
        );
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("An unexpected error occurred", e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage("unexpected-error", null, LocaleContextHolder.getLocale())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
