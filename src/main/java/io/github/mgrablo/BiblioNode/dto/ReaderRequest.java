package io.github.mgrablo.BiblioNode.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReaderRequest(
		@NotBlank String fullName,
		@Email @NotBlank String email
) { }
