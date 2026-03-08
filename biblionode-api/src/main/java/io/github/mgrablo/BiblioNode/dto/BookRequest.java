package io.github.mgrablo.BiblioNode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
		@NotBlank(message = "The title cannot be empty")
		String title,

		@NotBlank(message = "The isbn cannot be empty")
		String isbn,

		@NotNull(message = "The author id cannot be empty")
		Long authorId
) { }
