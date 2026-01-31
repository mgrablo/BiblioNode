package io.github.mgrablo.BiblioNode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;
import io.github.mgrablo.BiblioNode.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
class AuthorController {
	private final AuthorService authorService;

	@PostMapping
	ResponseEntity<AuthorResponse> addAuthor(
			@Valid @RequestBody AuthorRequest request
	) {
		var response = authorService.saveAuthor(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	ResponseEntity<List<AuthorResponse>> getAll() {
		var response = authorService.getAll();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	ResponseEntity<AuthorResponse> getById(
			@PathVariable Long id
	) {
		var response = authorService.findById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	ResponseEntity<AuthorResponse> searchByName(
			@RequestParam String name
	) {
		var response = authorService.findByName(name);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	ResponseEntity<AuthorResponse> updateAuthor(
			@PathVariable Long id,
			@RequestBody AuthorRequest authorRequest
	) {
		var response = authorService.updateAuthor(id, authorRequest);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
		authorService.deleteAuthor(id);
		return ResponseEntity.noContent().build();
	}
}
