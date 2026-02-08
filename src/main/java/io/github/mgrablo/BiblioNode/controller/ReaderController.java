package io.github.mgrablo.BiblioNode.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.service.ReaderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
class ReaderController {
	private final ReaderService readerService;

	@PostMapping
	public ResponseEntity<ReaderResponse> createReader(
			@Valid @RequestBody ReaderRequest request
	) {
		var response = readerService.createReader(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ReaderResponse> getReaderById(
			@PathVariable Long id
	) {
		var response = readerService.getReaderById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/email")
	public ResponseEntity<ReaderResponse> getReaderByEmail(
			@RequestParam String email
	) {
		var response = readerService.getReaderByEmail(email);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Page<ReaderResponse>> getAll(
			@ParameterObject Pageable pageable
	) {
		var response = readerService.getAll(pageable);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ReaderResponse> updateReader(
			@PathVariable Long id,
			@Valid @RequestBody ReaderRequest request
	) {
		var response = readerService.updateReader(id, request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReader(
			@PathVariable Long id
	) {
		readerService.deleteReader(id);
		return ResponseEntity.noContent().build();
	}
}
