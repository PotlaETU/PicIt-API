package com.picit.iam.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleUsernameException(RuntimeException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleExpiredJwtException(ExpiredJwtException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({UserNotFound.class, PostNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleUserNotFound(UserNotFound e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleConflictException(ConflictException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({IllegalArgumentException.class, MalformedJwtException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleSignatureException(SignatureException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(ImageCreationError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleImageCreationError(ImageCreationError e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

}
