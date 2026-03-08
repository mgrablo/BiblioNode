package io.github.mgrablo.BiblioNode.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static java.util.Collections.emptyList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.mapper.AuthorMapper;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.repository.AuthorRepository;
import io.github.mgrablo.BiblioNode.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceImplTest {
	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private AuthorMapper mapper;

	@InjectMocks
	private AuthorServiceImpl authorService;

	@Test
	void saveAuthor_ShouldReturnSuccess() {
		AuthorRequest authorRequest = new AuthorRequest("AAA", "Bio");
		Author author = createTestAuthor(null, "AAA", "Bio");
		Author savedAuthor = createTestAuthor(1L, "AAA", "Bio");
		AuthorResponse expectedResponse = createTestAuthorResponse(1L, "AAA", "Bio");

		when(authorRepository.findAuthorByName(authorRequest.name())).thenReturn(Optional.empty());
		when(mapper.toEntity(authorRequest)).thenReturn(author);
		when(authorRepository.save(author)).thenReturn(savedAuthor);
		when(mapper.toResponse(savedAuthor)).thenReturn(expectedResponse);

		AuthorResponse result = authorService.saveAuthor(authorRequest);

		assertNotNull(result);
		assertEquals("AAA", result.name());
		assertNull(result.books());
		verify(authorRepository, times(1)).save(any());
	}

	@Test
	void saveAuthor_ShouldReturnAuthor_WhenExists() {
		AuthorRequest authorRequest = new AuthorRequest("AAA", "Bio");
		Author savedAuthor = createTestAuthor(1L, "AAA", "Bio");
		AuthorResponse expectedResponse = createTestAuthorResponse(1L, "AAA", "Bio");

		when(authorRepository.findAuthorByName(authorRequest.name())).thenReturn(Optional.of(savedAuthor));
		when(mapper.toResponse(savedAuthor)).thenReturn(expectedResponse);

		AuthorResponse result = authorService.saveAuthor(authorRequest);

		assertNotNull(result);
		assertEquals("AAA", result.name());
		assertNull(result.books());
		verify(authorRepository, never()).save(any());
	}

	@Test
	void findById_ShouldReturnAuthor_WhenExists() {
		Long authorId = 1L;
		Author author = createTestAuthor(1L, "TestAuthor", "Bio");
		AuthorResponse expectedResponse = createTestAuthorResponse(1L, "TestAuthor", "Bio");

		when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
		when(mapper.toResponse(author)).thenReturn(expectedResponse);

		AuthorResponse result = authorService.findById(authorId);

		assertEquals(expectedResponse, result);
	}

	@Test
	void findById_ShouldThrowException_WhenAuthorNotFound() {
		Long authorId = 9L;
		when(authorRepository.findById(authorId)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			authorService.findById(authorId);
		});
	}

	@Test
	void findByName_ShouldReturnAuthor_WhenExists() {
		String name = "AAA";
		Author author = createTestAuthor(1L, name, "Bio");
		AuthorResponse expectedResponse = createTestAuthorResponse(1L, name, "Bio");

		when(authorRepository.findAuthorByName(name)).thenReturn(Optional.of(author));
		when(mapper.toResponse(author)).thenReturn(expectedResponse);

		AuthorResponse result = authorService.findByName(name);

		assertEquals(expectedResponse, result);
	}

	@Test
	void findByName_ShouldThrowException_WhenAuthorNotFound() {
		String name = "AAA";
		when(authorRepository.findAuthorByName(name)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			authorService.findByName(name);
		});
	}

	@Test
	void getAll_ShouldReturnListOfResponses() {
		Pageable pageable = Pageable.ofSize(10);
		Author author = createTestAuthor(1L, "AAA", "Bio");
		Page<Author> authorPage = new PageImpl<>(List.of(author));
		AuthorResponse expectedResponse = createTestAuthorResponse(1L, "AAA", "Bio");

		when(authorRepository.findAll(pageable)).thenReturn(authorPage);
		when(mapper.toResponse(author)).thenReturn(expectedResponse);

		Page<AuthorResponse> result = authorService.getAll(pageable);

		assertFalse(result.isEmpty());
		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
		verify(authorRepository).findAll(pageable);
	}

	@Test
	void getAll_ShouldReturnEmptyList_WhenNoAuthorExist() {
		Pageable pageable = Pageable.ofSize(10);
		when(authorRepository.findAll(pageable)).thenReturn(Page.empty());

		Page<AuthorResponse> result = authorService.getAll(pageable);

		assertTrue(result.isEmpty());
		verify(authorRepository).findAll(pageable);
	}

	@Test
	void updateAuthor_ShouldReturnUpdatedAuthor_WhenAuthorExists() {
		Long id = 1L;
		AuthorRequest request = new AuthorRequest("BBB", "Bio");
		Author oldAuthor = createTestAuthor(1L, "AAA", "New Bio");
		AuthorResponse expectedResponse = createTestAuthorResponse(1L, "BBB", "New Bio");

		when(authorRepository.findById(id)).thenReturn(Optional.of(oldAuthor));
		when(mapper.toResponse(oldAuthor)).thenReturn(expectedResponse);

		AuthorResponse result = authorService.updateAuthor(id, request);

		assertEquals(expectedResponse, result);
		verify(mapper, times(1)).toResponse(any());

	}

	@Test
	void updateAuthor_ShouldThrowException_WhenAuthorNotFound() {
		Long id = 9L;
		AuthorRequest request = new AuthorRequest("BBB", "Bio");
		when(authorRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			authorService.updateAuthor(id, request);
		});
	}

	@Test
	void deleteAuthor_ShouldDeleteAuthor_WhenAuthorExists() {
		Long id = 1L;
		when(authorRepository.existsById(id)).thenReturn(true);
		when(bookRepository.existsByAuthorId(id)).thenReturn(false);
		authorService.deleteAuthor(id);
		verify(authorRepository, times(1)).deleteById(id);
	}

	@Test
	void deleteAuthor_ShouldThrowException_WhenAuthorNotFound() {
		Long id = 1L;
		when(authorRepository.existsById(id)).thenReturn(false);
		assertThrows(ResourceNotFoundException.class, () -> {
			authorService.deleteAuthor(id);
		});

		verify(authorRepository, never()).deleteById(any());
	}

	@Test
	void deleteAuthor_ShouldThrowException_WhenAuthorHasBooks() {
		Long id = 1L;
		when(authorRepository.existsById(id)).thenReturn(true);
		when(bookRepository.existsByAuthorId(id)).thenReturn(true);
		assertThrows(DataIntegrityException.class, () -> {
			authorService.deleteAuthor(id);
		});

		verify(authorRepository, never()).deleteById(any());
	}

	private Author createTestAuthor(Long id, String name, String bio) {
		Author author = new Author();
		author.setId(id);
		author.setName(name);
		author.setBiography(bio);
		author.setBooks(emptyList());

		return author;
	}

	private AuthorResponse createTestAuthorResponse(Long id, String name, String bio) {
		return new AuthorResponse(id, name, bio, null, null, null);
	}
}
