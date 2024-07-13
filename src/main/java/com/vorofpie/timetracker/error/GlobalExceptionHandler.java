package com.vorofpie.timetracker.error;

import com.vorofpie.timetracker.dto.error.AppError;
import com.vorofpie.timetracker.dto.error.AppErrorCustom;
import com.vorofpie.timetracker.error.exception.AccessDeniedException;
import com.vorofpie.timetracker.error.exception.DuplicateResourceException;
import com.vorofpie.timetracker.error.exception.InvalidStatusTransitionException;
import com.vorofpie.timetracker.error.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Перехватывает исключения и возвращает структурированные ответы с ошибками.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "No message available";

    /**
     * Обрабатывает исключения ResourceNotFoundException.
     * Возвращает ответ с кодом 404 (NOT_FOUND).
     *
     * @param e исключение ResourceNotFoundException
     * @return объект AppError с деталями ошибки
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppError handleResourceNotFound(ResourceNotFoundException e) {
        return AppError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Обрабатывает исключения MethodArgumentNotValidException.
     * Возвращает ответ с кодом 400 (BAD_REQUEST) и деталями ошибок полей.
     *
     * @param e исключение MethodArgumentNotValidException
     * @return объект AppErrorCustom с деталями ошибки и ошибками полей
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppErrorCustom handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), DEFAULT_ERROR_MESSAGE)
                ));
        return AppErrorCustom.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    /**
     * Обрабатывает исключения ConstraintViolationException.
     * Возвращает ответ с кодом 400 (BAD_REQUEST) и деталями нарушений ограничений.
     *
     * @param e исключение ConstraintViolationException
     * @return объект AppErrorCustom с деталями ошибки и нарушениями ограничений
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppErrorCustom handleConstraintViolation(ConstraintViolationException e) {
        Map<String, String> errors = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
        return AppErrorCustom.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    /**
     * Обрабатывает исключения DuplicateResourceException.
     * Возвращает ответ с кодом 409 (CONFLICT).
     *
     * @param e исключение DuplicateResourceException
     * @return объект AppError с деталями ошибки
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public AppError handleDuplicateResourceException(DuplicateResourceException e) {
        return AppError.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Обрабатывает исключения InvalidStatusTransitionException.
     * Возвращает ответ с кодом 400 (BAD_REQUEST).
     *
     * @param e исключение InvalidStatusTransitionException
     * @return объект AppError с деталями ошибки
     */
    @ExceptionHandler(InvalidStatusTransitionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleInvalidOrderStatusException(InvalidStatusTransitionException e) {
        return AppError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Обрабатывает исключения AccessDeniedException.
     * Возвращает ответ с кодом 403 (FORBIDDEN).
     *
     * @param e исключение AccessDeniedException
     * @return объект AppError с деталями ошибки
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AppError handleAccessDeniedException(AccessDeniedException e) {
        return AppError.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
