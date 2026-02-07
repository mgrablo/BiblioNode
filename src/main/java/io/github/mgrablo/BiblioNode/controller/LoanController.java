package io.github.mgrablo.BiblioNode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.mgrablo.BiblioNode.dto.LoanRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
class LoanController {
	private final LoanService loanService;

	@PostMapping("/borrow")
	public ResponseEntity<LoanResponse> borrowBook(
			@Valid @RequestBody LoanRequest request
	) {
		var response = loanService.borrowBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PatchMapping("/{id}/return")
	public ResponseEntity<LoanResponse> returnBook(
			@PathVariable Long id
	) {
		var response = loanService.returnBook(id);
		return ResponseEntity.ok(response);
	}
}
