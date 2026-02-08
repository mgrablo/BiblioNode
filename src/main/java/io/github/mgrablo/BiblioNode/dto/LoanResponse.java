package io.github.mgrablo.BiblioNode.dto;

import java.time.LocalDateTime;

public record LoanResponse(
		Long id,
		Long bookId,
		String bookTitle,
		String bookAuthorName,
		String bookIsbn,
		Long readerId,
		LocalDateTime loanDate,
		LocalDateTime dueDate,
		LocalDateTime returnDate
) { }
