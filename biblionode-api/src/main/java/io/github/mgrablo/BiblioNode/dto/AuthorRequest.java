package io.github.mgrablo.BiblioNode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequest(
		@NotBlank(message = "The author's name cannot be empty")
		@Size(min = 2, max = 100, message = "The name must be from 2 to 100 characters")
		String name,

		@Size(max = 500, message = "The biography must not exceed 500 characters")
		String biography
) { }
