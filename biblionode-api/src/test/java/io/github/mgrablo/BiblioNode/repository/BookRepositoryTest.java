package io.github.mgrablo.BiblioNode.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
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
	private TestEntityManager entityManager;

	@Test
	void searchByTitleAndAuthor_ShouldReturnPagedResults_WhenFiltersMatch() {
		// GIVEN
		Author author = persistAuthor("Author1");
		persistBook("Book 1", "1", author, true);
		persistBook("Book 2", "2", author, true);

		Pageable pageable = Pageable.ofSize(10);

		// WHEN
		Page<Book> result = bookRepository.searchByTitleAndAuthor("ok 1", "thor", pageable);

		// THEN
		assertEquals(1, result.getTotalElements());
		assertEquals("Book 1", result.getContent().getFirst().getTitle());
	}

	@Test
	void searchByTitleAndAuthor_ShouldReturnAll_WhenFiltersAreNull() {
		// GIVEN
		Author author = persistAuthor("Author1");
		persistBook("Book 1", "1", author, true);
		persistBook("Book 2", "2", author, true);

		Pageable pageable = Pageable.ofSize(10);

		// WHEN
		Page<Book> result = bookRepository.searchByTitleAndAuthor(null, null, pageable);

		// THEN
		assertEquals(2, result.getTotalElements());
		assertEquals("Book 1", result.getContent().getFirst().getTitle());
	}

	@Test
	void shouldSaveBookWithAvailabilityStatus() {
		// GIVEN
		Author author = persistAuthor("Author1");
		Book book = new Book(null, "Book 1", "1", author);
		book.setAvailable(false);

		// WHEN
		Book savedBook = bookRepository.save(book);

		// THEN
		assertFalse(savedBook.isAvailable());
	}

	private Author persistAuthor(String name) {
		Author author = new Author();
		author.setName(name);
		author.setBiography("Bio");
		return entityManager.persist(author);
	}

	private Book persistBook(String title, String isbn, Author author, boolean available) {
		Book book = new Book();
		book.setTitle(title);
		book.setIsbn(isbn);
		book.setAuthor(author);
		book.setAvailable(available);
		return entityManager.persist(book);
	}
}
