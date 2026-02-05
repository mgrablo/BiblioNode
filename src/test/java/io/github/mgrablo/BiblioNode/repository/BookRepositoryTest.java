package io.github.mgrablo.BiblioNode.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.mgrablo.BiblioNode.config.JpaConfig;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.model.Book;

@DataJpaTest
@Import(JpaConfig.class)
public class BookRepositoryTest {
	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private AuthorRepository authorRepository;

	@Test
	void searchByTitleAndAuthor_ShouldReturnPagedResults_WhenFiltersMatch() {
		Author author = authorRepository.save(new Author(null, "Author1", "Bio", null));
		bookRepository.save(new Book(null, "Book 1", "1", author, true));
		bookRepository.save(new Book(null, "Book 2", "2", author, true));

		Pageable pageable = Pageable.ofSize(10);

		Page<Book> result = bookRepository.searchByTitleAndAuthor("ok 1", "thor", pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals("Book 1", result.getContent().getFirst().getTitle());
	}

	@Test
	void searchByTitleAndAuthor_ShouldReturnAll_WhenFiltersAreNull() {
		Author author = authorRepository.save(new Author(null, "Author1", "Bio", null));
		bookRepository.save(new Book(null, "Book 1", "1", author, true));
		bookRepository.save(new Book(null, "Book 2", "2", author, true));

		Pageable pageable = Pageable.ofSize(10);

		Page<Book> result = bookRepository.searchByTitleAndAuthor(null, null, pageable);

		assertEquals(2, result.getTotalElements());
		assertEquals("Book 1", result.getContent().getFirst().getTitle());
	}

	@Test
	void shouldSaveBookWithAvailabilityStatus() {
			Author author = authorRepository.save(new Author(null, "Author1", "Bio", null));
			Book book = new Book(null, "Book 1", "1", author);
			book.setAvailable(false);

			Book savedBook = bookRepository.save(book);

			assertFalse(savedBook.isAvailable());
	}
}
