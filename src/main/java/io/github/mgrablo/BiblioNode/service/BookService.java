package io.github.mgrablo.BiblioNode.service;

import java.util.List;

import io.github.mgrablo.BiblioNode.dto.BookRequest;
import io.github.mgrablo.BiblioNode.dto.BookResponse;

public interface BookService {
	BookResponse addBook(BookRequest bookRequest);

	BookResponse updateBook(Long id, BookRequest bookRequest);

	void deleteBook(Long id);

	BookResponse findBookById(Long id);

	BookResponse findBookByTitle(String title);

	List<BookResponse> getAllBooks();
	List<BookResponse> searchBooks(String bookTitle, String authorName);
}
