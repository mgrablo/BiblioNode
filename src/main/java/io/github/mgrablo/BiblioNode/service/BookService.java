package io.github.mgrablo.BiblioNode.service;

import java.util.List;

import io.github.mgrablo.BiblioNode.dto.BookRequest;
import io.github.mgrablo.BiblioNode.dto.BookResponse;

public interface BookService {
	BookResponse addBook(BookRequest bookRequest);

	BookResponse findBookById(Long bookId);

	BookResponse findBookByTitle(String bookTitle);

	List<BookResponse> getAllBooks();
	List<BookResponse> searchBooks(String bookTitle, String authorName);
}
