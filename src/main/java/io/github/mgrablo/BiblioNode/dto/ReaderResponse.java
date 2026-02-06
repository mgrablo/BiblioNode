package io.github.mgrablo.BiblioNode.dto;

import java.util.List;

public record ReaderResponse(
		Long id,
		String fullName,
		String email,
		List<LoanResponse> loans
) { }
