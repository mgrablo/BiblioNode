package io.github.mgrablo.BiblioNode.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
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
		Author author = new Author(1L, "TestAuthor", "Bio", null);
		Book book = new Book(null, "TestTitle", "111", author);
		Book savedBook = new Book(100L, "TestTitle", "111", author);
		BookResponse expectedResponse = new BookResponse(100L, "TestTitle", "111", "TestAuthor", 1L, null, null);

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
		Author author = new Author(1L, "TestAuthor", "Bio", null);
		Book book = new Book(null, "TestTitle", "111", author);
		Book savedBook = new Book(100L, "TestTitle", "111", author);
		BookResponse expectedResponse = new BookResponse(100L, "TestTitle", "111", "TestAuthor", 1L, null, null);

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
		Author author = new Author(1L, "TestAuthor", "Bio", null);
		Book book = new Book(bookId, "TestTitle", "111", author);
		BookResponse expectedResponse = new BookResponse(bookId, "TestTitle", "111", "TestAuthor", 1L, null, null);

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
		Author author = new Author(1L, "TestAuthor", "Bio", null);
		Book book = new Book(1L, title, "111", author);
		BookResponse expectedResponse = new BookResponse(1L, title, "111", "TestAuthor", 1L, null, null);

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
	void getAllBooks_ShouldReturnListOFResponses() {
		Author author = new Author(1L, "TestAuthor", "Bio", null);
		Book book = new Book(1L, "TestTitle", "111", author);
		BookResponse response = new BookResponse(1L, "TestTitle", "111", "TestAuthor", 1L, null, null);

		when(bookRepository.findAll()).thenReturn(List.of(book));
		when(mapper.toResponse(book)).thenReturn(response);

		List<BookResponse> result = bookService.getAllBooks();

		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertEquals(response, result.getFirst());
	}

	@Test
	void getAllBooks_ShouldReturnEmptyList_WhenNoBookExist() {
		when(bookRepository.findAll()).thenReturn(Collections.emptyList());

		List<BookResponse> result = bookService.getAllBooks();

		assertTrue(result.isEmpty());
	}

	@Test
	void searchBooks_ShouldReturnMatches() {
		String title = "TestTitle";
		String authorName = "TestAuthor";
		Author author = new Author(1L, authorName, "Bio", null);
		Book book = new Book(1L, title, "111", author);
		BookResponse response = new BookResponse(1L, title, "111", authorName, 1L, null, null);

		when(bookRepository.searchByTitleAndAuthor(title, authorName)).thenReturn(List.of(book));
		when(mapper.toResponse(book)).thenReturn(response);

		List<BookResponse> result = bookService.searchBooks(title, authorName);

		assertEquals(1, result.size());
		verify(bookRepository).searchByTitleAndAuthor(title, authorName);
	}

	@Test
	void searchBooks_ShouldReturnEmptyList_WhenNoMatchesFound() {
		String title = "aaa";
		String authorName = "bbb";
		when(bookRepository.searchByTitleAndAuthor(title, authorName)).thenReturn(Collections.emptyList());

		List<BookResponse> result = bookService.searchBooks(title, authorName);

		assertTrue(result.isEmpty());
		verify(bookRepository).searchByTitleAndAuthor(title, authorName);
	}

	@Test
	void updateBook_ShouldReturnUpdatedBook_WhenBookExists_SameAuthor() {
		Long id = 1L;
		BookRequest request = new BookRequest("NewTitle", "222", 1L);
		Book book = new Book(id, "OldTitle", "111", new Author(1L, "Name", "", null));
		BookResponse expectedReponse = new BookResponse(id, "NewTitle", "222", "Name", 1L, null, null);

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
		Author oldAuthor = new Author(1L, "Name", "", null);
		Author newAuthor = new Author(2L, "NewAuthorName", "", null);

		Book book = new Book(id, "OldTitle", "111", oldAuthor);
		BookResponse expectedReponse = new BookResponse(id, "NewTitle", "111", "NewAuthorName", 2L, null, null);

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
		Author oldAuthor = new Author(1L, "Name", "", null);

		Book book = new Book(id, "OldTitle", "111", oldAuthor);

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
}
