package io.github.mgrablo.BiblioNode.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.mgrablo.BiblioNode.dto.BorrowRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;

public interface LoanService {
	LoanResponse borrowBook(BorrowRequest request, String email);
	LoanResponse returnBook(Long loanId);

	Page<LoanResponse> getAllLoans(Pageable pageable);
	Page<LoanResponse> getLoansByReaderId(Long readerId, Pageable pageable);
	Page<LoanResponse> getLoansByBookId(Long bookId, Pageable pageable);

	Page<LoanResponse> getActiveLoans(Pageable pageable);
	Page<LoanResponse> getActiveLoansByReaderId(Long readerId, Pageable pageable);

	Page<LoanResponse> getOverdueLoans(Pageable pageable);

	Page<LoanResponse> getLoansByReaderEmail(String email, Pageable pageable);
}
