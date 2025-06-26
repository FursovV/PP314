package ru.kata.spring.boot_security.demo.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<UserIncorrectData> handleException(NoSuchUserException ex) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @ExceptionHandler
    public ResponseEntity<UserIncorrectData> handleException(Exception ex) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

}
