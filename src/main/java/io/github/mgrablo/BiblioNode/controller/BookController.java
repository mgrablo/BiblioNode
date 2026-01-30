package io.github.mgrablo.BiblioNode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.github.mgrablo.BiblioNode.dto.BookRequest;
import io.github.mgrablo.BiblioNode.dto.BookResponse;
import io.github.mgrablo.BiblioNode.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
class BookController {
	private final BookService bookService;

	@PostMapping
	public ResponseEntity<BookResponse> addBook(
			@Valid
			@RequestBody
			BookRequest bookRequest
	) {
		var response = bookService.addBook(bookRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<BookResponse>> getAll() {
		var response = bookService.getAllBooks();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{bookId}")
	public ResponseEntity<BookResponse> getById(
			@PathVariable Long bookId
	) {
		var response = bookService.findBookById(bookId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/find")
	public ResponseEntity<BookResponse> findByTitle(
			@RequestParam String bookTitle
	) {
		var response = bookService.findBookByTitle(bookTitle);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	public ResponseEntity<List<BookResponse>> searchBooks(
			@RequestParam(required = false) String bookTitle,
			@RequestParam(required = false) String authorName
	) {
		var response = bookService.searchBooks(bookTitle, authorName);
		return ResponseEntity.ok(response);
	}
}
