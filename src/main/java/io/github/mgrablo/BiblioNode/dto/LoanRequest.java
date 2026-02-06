package io.github.mgrablo.BiblioNode.dto;

public record LoanRequest(
		Long bookId,
		Long readerId
) { }
