package io.github.mgrablo.BiblioNode.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(
	@NotBlank String name,
	String biography
) {
}
