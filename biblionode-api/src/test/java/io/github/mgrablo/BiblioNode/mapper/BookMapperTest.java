package io.github.mgrablo.BiblioNode.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import io.github.mgrablo.BiblioNode.dto.BookResponse;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.model.Book;

public class BookMapperTest {
	private final BookMapper mapper = Mappers.getMapper(BookMapper.class);

	@Test
	void shouldMapBookToBookResponse() {
		Author author = new Author(10L, "AAA", "Bio", null);
		Book book = new Book(1L, "BBB", "111", author, true);

		BookResponse response = mapper.toResponse(book);

		assertEquals(10L, response.authorId());
		assertEquals("AAA", response.authorName());
		assertTrue(response.available());
		assertEquals(1L, response.id());
		assertEquals("BBB", response.title());
		assertEquals("111", response.isbn());
	}
}
