package io.github.mgrablo.BiblioNode.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.github.mgrablo.BiblioNode.dto.ErrorResponse;
import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
@Tag(name = "Readers", description = "Endpoints for managing library readers")
class ReaderController {
	private final ReaderService readerService;

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get reader by ID", description = "Returns a single reader by their unique identifier.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found the reader"),
			@ApiResponse(responseCode = "404", description = "Reader not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<ReaderResponse> getReaderById(
			@PathVariable Long id
	) {
		var response = readerService.getReaderById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/email")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get reader by email", description = "Finds a reader by their email address.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found the reader"),
			@ApiResponse(responseCode = "404", description = "Reader not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<ReaderResponse> getReaderByEmail(
			@RequestParam String email
	) {
		var response = readerService.getReaderByEmail(email);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all readers", description = "Returns a paginated list of all registered readers.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved list of readers")
	public ResponseEntity<Page<ReaderResponse>> getAll(
			@ParameterObject Pageable pageable
	) {
		var response = readerService.getAll(pageable);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update a reader", description = "Updates an existing reader's information.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Reader updated successfully"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Reader not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	public ResponseEntity<ReaderResponse> updateReader(
			@PathVariable Long id,
			@Valid @RequestBody ReaderRequest request
	) {
		var response = readerService.updateReader(id, request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete a reader", description = "Removes a reader from the library system.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Reader deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Reader not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Void> deleteReader(
			@PathVariable Long id
	) {
		readerService.deleteReader(id);
		return ResponseEntity.noContent().build();
	}
}
