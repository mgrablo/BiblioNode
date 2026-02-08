package io.github.mgrablo.BiblioNode.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@GetMapping
	public ResponseEntity<Page<LoanResponse>> getLoans(
			@RequestParam(required = false) Long readerId,
			@RequestParam(required = false) Long bookId,
			@RequestParam(defaultValue = "false") boolean activeOnly,
			@ParameterObject Pageable pageable
	) {
		if (readerId != null && activeOnly) {
			return ResponseEntity.ok(loanService.getActiveLoansByReaderId(readerId, pageable));
		} else if (readerId != null) {
			return ResponseEntity.ok(loanService.getLoansByReaderId(readerId, pageable));
		} else if (bookId != null) {
			return ResponseEntity.ok(loanService.getLoansByBookId(bookId, pageable));
		} else if (activeOnly) {
			return ResponseEntity.ok(loanService.getActiveLoans(pageable));
		} else {
			return ResponseEntity.ok(loanService.getAllLoans(pageable));
		}
	}

	@GetMapping("/overdue")
	public ResponseEntity<Page<LoanResponse>> getOverdueLoans(
			@ParameterObject Pageable pageable
	) {
		return ResponseEntity.ok(loanService.getOverdueLoans(pageable));
	}
}
