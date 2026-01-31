package io.github.mgrablo.BiblioNode.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import io.github.mgrablo.BiblioNode.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				e.getMessage(),
				request.getRequestURI()
		);

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(
			MethodArgumentNotValidException e
	) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityException(DataIntegrityException e, HttpServletRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.CONFLICT.value(),
				"Conflict",
				e.getMessage(),
				request.getRequestURI()
		);

		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error",
				"There was an unexpected system error.",
				request.getRequestURI()
		);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
