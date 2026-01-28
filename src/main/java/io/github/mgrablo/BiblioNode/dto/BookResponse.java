package io.github.mgrablo.BiblioNode.dto;

public record BookResponse(
		Long id,
		String title,
		String isbn,
		String authorName,
		Long authorId
) { }
