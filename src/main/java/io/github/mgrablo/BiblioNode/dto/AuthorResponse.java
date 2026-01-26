package io.github.mgrablo.BiblioNode.dto;

import io.github.mgrablo.BiblioNode.model.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AuthorResponse(
	@NotNull Long id,
	@NotBlank String name,
	String biography,
	List<Book> books
) {}
