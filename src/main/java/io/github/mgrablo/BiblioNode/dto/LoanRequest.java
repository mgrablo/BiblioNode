package io.github.mgrablo.BiblioNode.dto;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(
		@NotNull Long bookId,
		@NotNull Long readerId
) { }
