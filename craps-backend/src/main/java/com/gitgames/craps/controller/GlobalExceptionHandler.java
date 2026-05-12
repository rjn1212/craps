package com.gitgames.craps.controller;

import com.gitgames.craps.service.GameNotFoundException;
import com.gitgames.craps.service.InvalidActionException;
import com.gitgames.craps.service.InvalidBetException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleGameNotFound(GameNotFoundException e) {
        return Map.of("error", "GAME_NOT_FOUND", "message", e.getMessage());
    }

    @ExceptionHandler(InvalidBetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidBet(InvalidBetException e) {
        return Map.of("error", "INVALID_BET", "message", e.getMessage());
    }

    @ExceptionHandler(InvalidActionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidAction(InvalidActionException e) {
        return Map.of("error", "INVALID_ACTION", "message", e.getMessage());
    }
}
