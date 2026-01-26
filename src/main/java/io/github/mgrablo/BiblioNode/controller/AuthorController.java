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
			@RequestParam @Valid String name,
			@RequestParam(required = false) String biography
	) {
		AuthorRequest request = new AuthorRequest(name, biography);
		AuthorResponse response = authorService.saveAuthor(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	ResponseEntity<List<AuthorResponse>> getAll() {
		List<AuthorResponse> response = authorService.findAll();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	ResponseEntity<AuthorResponse> getById(
			@PathVariable Long id
	) {
		AuthorResponse response = authorService.findById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	ResponseEntity<AuthorResponse> searchByName(
			@RequestParam String name
	) {
		AuthorResponse response = authorService.findByName(name);
		return ResponseEntity.ok(response);
	}
}
