package io.github.mgrablo.BiblioNode.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static java.util.Collections.emptyList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import io.github.mgrablo.BiblioNode.dto.BookRequest;
import io.github.mgrablo.BiblioNode.dto.BookResponse;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.mapper.BookMapper;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.model.Book;
import io.github.mgrablo.BiblioNode.repository.AuthorRepository;
import io.github.mgrablo.BiblioNode.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
	@Mock
	private BookRepository bookRepository;

	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private BookMapper mapper;

	@InjectMocks
	private BookServiceImpl bookService;

	@Test
	void addBook_ShouldReturnSuccess_WhenAuthorExists() {
		BookRequest request = new BookRequest("TestTitle", "111", 1L);
		Author author = createTestAuthor(1L, "TestAuthor");
		Book book = createTestBook(null, "TestTitle", "111", author);
		Book savedBook = createTestBook(100L, "TestTitle", "111", author);
		BookResponse expectedResponse = createTestBookResponse(100L, "TestTitle", "111", author);

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(mapper.toEntity(request)).thenReturn(book);
		when(bookRepository.save(book)).thenReturn(savedBook);
		when(mapper.toResponse(savedBook)).thenReturn(expectedResponse);

		BookResponse result = bookService.addBook(request);

		assertNotNull(result);
		assertEquals("TestTitle", result.title());
		verify(bookRepository, times(1)).save(any());
	}

	@Test
	void addBook_ShouldThrowException_WhenAuthorDoesNotExist() {
		BookRequest request = new BookRequest("TestTitle", "111", 99L);
		when(authorRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.addBook(request);
		});

		verify(bookRepository, never()).save(any());
	}

	@Test
	void addBook_ShouldSetAuthor() {
		BookRequest request = new BookRequest("TestTitle", "111", 1L);
		Author author = createTestAuthor(1L, "TestAuthor");
		Book book = createTestBook(null, "TestTitle", "111", author);
		Book savedBook = createTestBook(100L, "TestTitle", "111", author);
		BookResponse expectedResponse = createTestBookResponse(100L, "TestTitle", "111", author);

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(mapper.toEntity(request)).thenReturn(book);
		when(bookRepository.save(book)).thenReturn(savedBook);
		when(mapper.toResponse(savedBook)).thenReturn(expectedResponse);

		ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);

		bookService.addBook(request);

		verify(bookRepository).save(bookArgumentCaptor.capture());
		Book savedBookInternal = bookArgumentCaptor.getValue();

		assertEquals(author, savedBookInternal.getAuthor());
	}

	@Test
	void findBookById_ShouldReturnBook_WhenExists() {
		Long bookId = 1L;
		Author author = createTestAuthor(1L, "TestAuthor");
		Book book = createTestBook(bookId, "TestTitle", "111", author);
		BookResponse expectedResponse = createTestBookResponse(bookId, "TestTitle", "111", author);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(mapper.toResponse(book)).thenReturn(expectedResponse);

		BookResponse result = bookService.findBookById(bookId);

		assertEquals(expectedResponse, result);
	}

	@Test
	void findBookById_ShouldThrowException_WhenBookNotFound() {
		Long bookId = 9L;
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.findBookById(bookId);
		});
	}

	@Test
	void findBookByTitle_ShouldReturnBook_WhenExists() {
		String title = "ABC";
		Author author = createTestAuthor(1L, "TestAuthor");
		Book book = createTestBook(1L, title, "111", author);
		BookResponse expectedResponse = createTestBookResponse(1L, title, "111", author);

		when(bookRepository.findBookByTitle(title)).thenReturn(Optional.of(book));
		when(mapper.toResponse(book)).thenReturn(expectedResponse);

		BookResponse result = bookService.findBookByTitle(title);

		assertEquals(expectedResponse, result);
	}

	@Test
	void findBookByTitle_ShouldThrowException_WhenBookNotFound() {
		String title = "ABC";
		when(bookRepository.findBookByTitle(title)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.findBookByTitle(title);
		});
	}

	@Test
	void getAllBooks_ShouldReturnPageOfResponses() {
		Pageable pageable = Pageable.ofSize(10);
		Author author = createTestAuthor(1L, "TestAuthor");
		Book book = createTestBook(1L, "TestTitle", "111", author);
		Page<Book> bookPage = new PageImpl<>(List.of(book));
		BookResponse response = createTestBookResponse(1L, "TestTitle", "111", author);

		when(bookRepository.findAll(pageable)).thenReturn(bookPage);
		when(mapper.toResponse(book)).thenReturn(response);

		Page<BookResponse> result = bookService.getAllBooks(pageable);

		assertFalse(result.isEmpty());
		assertEquals(1, result.getTotalElements());
		assertEquals(response, result.getContent().getFirst());
		verify(bookRepository).findAll(pageable);
	}

	@Test
	void getAllBooks_ShouldReturnEmptyList_WhenNoBookExist() {
		Pageable pageable = Pageable.ofSize(10);
		when(bookRepository.findAll(pageable)).thenReturn(Page.empty());

		Page<BookResponse> result = bookService.getAllBooks(pageable);

		assertTrue(result.isEmpty());
		verify(bookRepository).findAll(pageable);
	}

	@Test
	void searchBooks_ShouldReturnMatches() {
		String title = "TestTitle";
		String authorName = "TestAuthor";
		Pageable pageable = Pageable.ofSize(10);
		Author author = createTestAuthor(1L, authorName);
		Book book = createTestBook(1L, title, "111", author);
		BookResponse response = createTestBookResponse(1L, title, "111", author);
		Page<Book> bookPage = new PageImpl<>(List.of(book));

		when(bookRepository.searchByTitleAndAuthor(title, authorName, pageable)).thenReturn(bookPage);
		when(mapper.toResponse(book)).thenReturn(response);

		Page<BookResponse> result = bookService.searchBooks(title, authorName, pageable);

		assertEquals(1, result.getTotalElements());
		verify(bookRepository).searchByTitleAndAuthor(title, authorName, pageable);
	}

	@Test
	void searchBooks_ShouldReturnEmptyList_WhenNoMatchesFound() {
		String title = "aaa";
		String authorName = "bbb";
		Pageable pageable = Pageable.ofSize(10);
		when(bookRepository.searchByTitleAndAuthor(title, authorName, pageable)).thenReturn(Page.empty());

		Page<BookResponse> result = bookService.searchBooks(title, authorName, pageable);

		assertTrue(result.isEmpty());
		verify(bookRepository).searchByTitleAndAuthor(title, authorName, pageable);
	}

	@Test
	void updateBook_ShouldReturnUpdatedBook_WhenBookExists_SameAuthor() {
		Long id = 1L;
		BookRequest request = new BookRequest("NewTitle", "222", 1L);
		Author author = createTestAuthor(1L, "Name");
		Book book = createTestBook(id, "OldTitle", "111", author);
		BookResponse expectedReponse = createTestBookResponse(id, "NewTitle", "222", author);

		when(bookRepository.findById(id)).thenReturn(Optional.of(book));
		when(mapper.toResponse(book)).thenReturn(expectedReponse);

		BookResponse result = bookService.updateBook(id, request);

		assertEquals(expectedReponse, result);
		assertEquals("NewTitle", book.getTitle());
		assertEquals("222", book.getIsbn());
		assertEquals(1L, book.getAuthor().getId());
	}

	@Test
	void updateBook_ShouldReturnUpdatedBook_WhenBookExists_ChangedAuthor() {
		Long id = 1L;
		BookRequest request = new BookRequest("NewTitle", "111", 2L);
		Author oldAuthor = createTestAuthor(1L, "Name");
		Author newAuthor = createTestAuthor(2L, "NewAuthorName");

		Book book = createTestBook(id, "OldTitle", "111", oldAuthor);
		BookResponse expectedReponse = createTestBookResponse(id, "NewTitle", "111", newAuthor);

		when(bookRepository.findById(id)).thenReturn(Optional.of(book));
		when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
		when(mapper.toResponse(book)).thenReturn(expectedReponse);

		BookResponse result = bookService.updateBook(id, request);

		assertEquals(expectedReponse, result);
		assertEquals("NewTitle", book.getTitle());
		assertEquals("111", book.getIsbn());
		assertEquals(2L, book.getAuthor().getId());
	}

	@Test
	void updateBook_ShouldThrowException_WhenBookNotFound() {
		Long id = 1L;
		BookRequest request = new BookRequest("OldTitle", "111", 2L);
		when(bookRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.updateBook(id, request);
		});
	}

	@Test
	void updateBook_ShouldThrowException_WhenNewAuthorDoesNotExist() {
		Long id = 1L;
		BookRequest request = new BookRequest("OldTitle", "111", 2L);
		Author oldAuthor = createTestAuthor(1L, "Name");

		Book book = createTestBook(id, "OldTitle", "111", oldAuthor);

		when(bookRepository.findById(id)).thenReturn(Optional.of(book));
		when(authorRepository.findById(2L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.updateBook(id, request);
		});

		// Book not updated
		assertEquals("OldTitle", book.getTitle());
		assertEquals("111", book.getIsbn());
		assertEquals(1L, book.getAuthor().getId());
	}

	@Test
	void deleteBook_ShouldDeleteBook_WhenBookExists() {
		Long id = 1L;
		when(bookRepository.existsById(id)).thenReturn(true);

		bookService.deleteBook(id);

		verify(bookRepository, times(1)).deleteById(id);
	}

	@Test
	void deleteBook_ShouldThrowException_WhenBookDoesNotExist() {
		Long id = 1L;
		when(bookRepository.existsById(id)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.deleteBook(id);
		});

		verify(bookRepository, never()).deleteById(id);
	}

	private Author createTestAuthor(Long id, String name) {
		Author author = new Author();
		author.setId(id);
		author.setName(name);
		author.setBiography("Bio");
		author.setBooks(emptyList());

		return author;
	}

	private Book createTestBook(Long id, String title, String isbn, Author author) {
		Book book = new Book();
		book.setId(id);
		book.setTitle(title);
		book.setIsbn(isbn);
		book.setAvailable(true);
		book.setAuthor(author);

		return book;
	}

	private BookResponse createTestBookResponse(Long id, String title, String isbn, Author author) {
		return new BookResponse(id, title, isbn, author.getName(), author.getId(), true, null, null);
	}
}
