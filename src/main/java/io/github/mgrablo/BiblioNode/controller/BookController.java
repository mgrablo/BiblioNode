package io.github.mgrablo.BiblioNode.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.mgrablo.BiblioNode.dto.BookRequest;
import io.github.mgrablo.BiblioNode.dto.BookResponse;
import io.github.mgrablo.BiblioNode.dto.ErrorResponse;
import io.github.mgrablo.BiblioNode.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Endpoints for managing books in the library")
class BookController {
	private final BookService bookService;

	@PostMapping
	@Operation(summary = "Add a new book", description = "Creates a new book record in the library.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Book created successfully"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Author not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<BookResponse> addBook(
			@Valid
			@RequestBody
			BookRequest bookRequest
	) {
		var response = bookService.addBook(bookRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@Operation(summary = "Get all books", description = "Returns a paginated list of all books in the library.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved list of books")
	public ResponseEntity<Page<BookResponse>> getAll(
			@ParameterObject Pageable pageable
			) {
		var response = bookService.getAllBooks(pageable);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{bookId}")
	@Operation(summary = "Get book by ID", description = "Returns a single book by its unique identifier.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found the book"),
			@ApiResponse(responseCode = "404", description = "Book not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<BookResponse> getById(
			@PathVariable Long bookId
	) {
		var response = bookService.findBookById(bookId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/find")
	@Operation(summary = "Find book by title", description = "Returns a book matching the exact title.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found the book"),
			@ApiResponse(responseCode = "404", description = "Book not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<BookResponse> findByTitle(
			@RequestParam String bookTitle
	) {
		var response = bookService.findBookByTitle(bookTitle);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	@Operation(summary = "Search books", description = "Searches for books by title and/or author name. Both parameters are optional.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved list of books")
	public ResponseEntity<Page<BookResponse>> searchBooks(
			@RequestParam(required = false) String bookTitle,
			@RequestParam(required = false) String authorName,
			@ParameterObject Pageable pageable
	) {
		var response = bookService.searchBooks(bookTitle, authorName, pageable);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a book", description = "Updates an existing book's information.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Book updated successfully"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Book or Author not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<BookResponse> updateBook(
			@PathVariable Long id,
			@Valid @RequestBody BookRequest bookRequest
	) {
		var response = bookService.updateBook(id, bookRequest);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a book", description = "Removes a book from the library system.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Book deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Book not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
		bookService.deleteBook(id);
		return ResponseEntity.noContent().build();
	}
}
