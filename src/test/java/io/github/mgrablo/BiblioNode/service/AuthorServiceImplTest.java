package io.github.mgrablo.BiblioNode.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.mapper.AuthorMapper;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.repository.AuthorRepository;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceImplTest {
	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private AuthorMapper mapper;

	@InjectMocks
	private AuthorServiceImpl authorService;

	@Test
	void saveAuthor_ShouldReturnSuccess(){
		AuthorRequest authorRequest = new AuthorRequest("AAA", "Bio");
		Author author = new Author(null, "AAA", "Bio", null);
		Author savedAuthor = new Author(1L, "AAA", "Bio", null);
		AuthorResponse expectedResponse = new AuthorResponse(1L, "AAA", "Bio", null);

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
	void saveAuthor_ShouldReturnAuthor_WhenExists(){
		AuthorRequest authorRequest = new AuthorRequest("AAA", "Bio");
		Author savedAuthor = new Author(1L, "AAA", "Bio", null);
		AuthorResponse expectedResponse = new AuthorResponse(1L, "AAA", "Bio", null);

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
		Author author = new Author(1L, "TestAuthor", "Bio", null);
		AuthorResponse expectedResponse = new AuthorResponse(1L, "TestAuthor", "Bio", null);

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
		Author author = new Author(1L, name, "Bio", null);
		AuthorResponse expectedResponse = new AuthorResponse(1L, name, "Bio", null);

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
		Author author = new Author(1L, "AAA", "Bio", null);
		AuthorResponse expectedResponse = new AuthorResponse(1L, "AAA", "Bio", null);

		when(authorRepository.findAll()).thenReturn(List.of(author));
		when(mapper.toResponse(author)).thenReturn(expectedResponse);

		List<AuthorResponse> result = authorService.getAll();

		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertEquals(expectedResponse, result.getFirst());
	}

	@Test
	void getAll_ShouldReturnEmptyList_WhenNoAuthorExist() {
		when(authorRepository.findAll()).thenReturn(Collections.emptyList());

		List<AuthorResponse> result = authorService.getAll();

		assertTrue(result.isEmpty());
	}
}
