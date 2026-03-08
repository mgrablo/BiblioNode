package io.github.mgrablo.BiblioNode.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.mgrablo.BiblioNode.dto.BookRequest;
import io.github.mgrablo.BiblioNode.dto.BookResponse;

public interface BookService {
	BookResponse addBook(BookRequest bookRequest);

	BookResponse updateBook(Long id, BookRequest bookRequest);

	void deleteBook(Long id);

	BookResponse findBookById(Long id);

	BookResponse findBookByTitle(String title);

	Page<BookResponse> getAllBooks(Pageable pageable);
	Page<BookResponse> searchBooks(String bookTitle, String authorName, Pageable pageable);
}
