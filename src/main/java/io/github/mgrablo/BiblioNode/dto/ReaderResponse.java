package io.github.mgrablo.BiblioNode.dto;

import java.util.List;

public record ReaderResponse(
		Long id,
		String fullName,
		String email,
		List<LoanResponse> loans
) {
	public ReaderResponse {
		if (loans == null) {
			loans = List.of();
		} else {
			loans = List.copyOf(loans);
		}
	}
}
