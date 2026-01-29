package io.github.mgrablo.BiblioNode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import io.github.mgrablo.BiblioNode.dto.BookRequest;
import io.github.mgrablo.BiblioNode.dto.BookResponse;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.mapper.BookMapper;
import io.github.mgrablo.BiblioNode.model.Book;
import io.github.mgrablo.BiblioNode.repository.AuthorRepository;
import io.github.mgrablo.BiblioNode.repository.BookRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;
	private final BookMapper mapper;


	@Override
	@Transactional
	public BookResponse addBook(BookRequest bookRequest) {
		var author = authorRepository.findById(bookRequest.authorId())
				.orElseThrow(() -> new ResourceNotFoundException("Author not found"));

		Book book = mapper.toEntity(bookRequest);
		book.setAuthor(author);
		Book savedBook = bookRepository.save(book);
		return mapper.toResponse(savedBook);
	}

	@Override
	@Transactional
	public List<BookResponse> getAllBooks() {
		return bookRepository.findAll().stream()
				.map(mapper::toResponse).toList();
	}

	@Override
	@Transactional
	public BookResponse findBookById(Long bookId) {
		var book = bookRepository.findById(bookId);
		return book.map(mapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("Book not found for id: " + bookId));
	}

	@Override
	@Transactional
	public BookResponse findBookByTitle(String bookTitle) {
		var book = bookRepository.findBookByTitle(bookTitle);
		return book.map(mapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("Book not found with title: " + bookTitle));
	}

	@Override
	@Transactional
	public List<BookResponse> searchBooks(String bookTitle, String authorName) {
		var books = bookRepository.searchByTitleAndAuthor(bookTitle, authorName);
		return books.stream().map(mapper::toResponse).toList();
	}
}
