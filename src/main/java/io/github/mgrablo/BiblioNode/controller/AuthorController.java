package io.github.mgrablo.BiblioNode.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;
import io.github.mgrablo.BiblioNode.dto.ErrorResponse;
import io.github.mgrablo.BiblioNode.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Authors", description = "Endpoints for managing authors")
class AuthorController {
	private final AuthorService authorService;

	@PostMapping
	@Operation(summary = "Add a new author", description = "Creates a new author record in the library system.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Author created successfully"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<AuthorResponse> addAuthor(
			@Valid @RequestBody AuthorRequest request
	) {
		var response = authorService.saveAuthor(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@Operation(summary = "Get all authors", description = "Returns a paginated list of all authors.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved list of authors")
	ResponseEntity<Page<AuthorResponse>> getAll(
			@ParameterObject Pageable pageable
	) {
		var response = authorService.getAll(pageable);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get author by ID", description = "Returns a single author by their unique identifier.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found the author"),
			@ApiResponse(responseCode = "404", description = "Author not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<AuthorResponse> getById(
			@PathVariable Long id
	) {
		var response = authorService.findById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	@Operation(summary = "Search author by name", description = "Finds an author by their name.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found the author"),
			@ApiResponse(responseCode = "404", description = "Author not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<AuthorResponse> searchByName(
			@RequestParam String name
	) {
		var response = authorService.findByName(name);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an author", description = "Updates an existing author's information.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Author updated successfully"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Author not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<AuthorResponse> updateAuthor(
			@PathVariable Long id,
			@Valid @RequestBody AuthorRequest authorRequest
	) {
		var response = authorService.updateAuthor(id, authorRequest);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete an author", description = "Removes an author from the library system.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Author deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Author not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Cannot delete author with assigned books",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
		authorService.deleteAuthor(id);
		return ResponseEntity.noContent().build();
	}
}
