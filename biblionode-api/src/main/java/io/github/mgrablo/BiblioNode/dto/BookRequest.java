package io.github.mgrablo.BiblioNode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(
		@NotBlank(message = "The title cannot be empty")
		String title,

		@NotBlank(message = "The isbn cannot be empty")
		String isbn,

		@NotNull(message = "The author id cannot be empty")
		Long authorId,

		@Size(max = 1000, message = "The cover URL cannot exceed 1000 characters")
		String coverUrl,

		String description
) {
	public BookRequest(String title, String isbn, Long authorId) {
		this(title, isbn, authorId, null, null);
	}
}
