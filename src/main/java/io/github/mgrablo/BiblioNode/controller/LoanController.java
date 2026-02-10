package io.github.mgrablo.BiblioNode.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.mgrablo.BiblioNode.dto.ErrorResponse;
import io.github.mgrablo.BiblioNode.dto.LoanRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Endpoints for managing book borrowings, returns, and overdue tracking")
class LoanController {
	private final LoanService loanService;

	@PostMapping("/borrow")
	@Operation(
			summary = "Borrow a book",
			description = "Creates a new loan record. Validates if the book is available and if the reader exists."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Book successfully borrowed"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Book or Reader not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Book is already borrowed by someone else",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<LoanResponse> borrowBook(
			@Valid @RequestBody LoanRequest request
	) {
		var response = loanService.borrowBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PatchMapping("/{id}/return")
	@Operation(
			summary = "Return a book",
			description = "Registers the return of a book by loan ID and updates book availability."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Book successfully returned"),
			@ApiResponse(responseCode = "404", description = "Loan record not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Book was already returned",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<LoanResponse> returnBook(
			@PathVariable Long id
	) {
		var response = loanService.returnBook(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	@Operation(
			summary = "Get loans with filters",
			description = "Retrieves a paginated list of loans. Can be filtered by reader, book, or active status."
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved list of loans")
	public ResponseEntity<Page<LoanResponse>> getLoans(
			@Parameter(description = "Filter by Reader ID") @RequestParam(required = false) Long readerId,
			@Parameter(description = "Filter by Book ID") @RequestParam(required = false) Long bookId,
			@Parameter(description = "Show only non-returned loans") @RequestParam(defaultValue = "false") boolean activeOnly,
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
	@Operation(
			summary = "Get overdue loans",
			description = "Retrieves a list of loans where the due date has passed and the book has not been returned."
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved list of overdue loans")
	public ResponseEntity<Page<LoanResponse>> getOverdueLoans(
			@ParameterObject Pageable pageable
	) {
		return ResponseEntity.ok(loanService.getOverdueLoans(pageable));
	}
}
