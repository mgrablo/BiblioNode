package io.github.mgrablo.BiblioNode.service;

import io.github.mgrablo.BiblioNode.dto.LoanRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;

public interface LoanService {
	LoanResponse borrowBook(LoanRequest request);
	LoanResponse returnBook(Long loanId);
}
