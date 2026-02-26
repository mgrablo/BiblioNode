package io.github.mgrablo.BiblioNode.dto;

import java.time.LocalDateTime;

public record UserProfileResponse(
		String email,
		String name,
		LocalDateTime memberSince,
		Long activeLoansCount
) {
}
