package io.github.mgrablo.BiblioNode.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record BookResponse(
		Long id,
		String title,
		String isbn,
		String authorName,
		Long authorId,
		boolean available,

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime createdAt,

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime modifiedAt
) { }
