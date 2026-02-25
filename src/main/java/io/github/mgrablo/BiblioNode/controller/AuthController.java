package io.github.mgrablo.BiblioNode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.mgrablo.BiblioNode.dto.LoginRequest;
import io.github.mgrablo.BiblioNode.dto.LoginResponse;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.dto.RegisterRequest;
import io.github.mgrablo.BiblioNode.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {
	private final AuthService authService;

	@PostMapping("/register")
	@Operation(summary = "Register a new user", description = "Creates a user account and a reader profile.")
	@ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
	})
	public ResponseEntity<ReaderResponse> register(
			@Valid @RequestBody
			RegisterRequest request
	) {
		ReaderResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	@Operation(summary = "Authenticate user", description = "Returns a JWT token if credentials are valid.")
	public ResponseEntity<LoginResponse> login(
			@Valid @RequestBody
			LoginRequest request
	) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}
}
