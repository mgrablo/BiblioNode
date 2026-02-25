package io.github.mgrablo.BiblioNode.dto;

import jakarta.validation.constraints.NotNull;

public record BorrowRequest(
		@NotNull Long bookId
) { }
