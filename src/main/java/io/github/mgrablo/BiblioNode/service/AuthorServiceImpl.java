package io.github.mgrablo.BiblioNode.service;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.mapper.AuthorMapper;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.repository.AuthorRepository;
import io.github.mgrablo.BiblioNode.repository.BookRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository repository;
	private final BookRepository bookRepository;
	private final AuthorMapper mapper;

	@Override
	@Transactional
	public AuthorResponse saveAuthor(AuthorRequest authorRequest) {
		Optional<Author> existingAuthor = repository.findAuthorByName(authorRequest.name());
		if (existingAuthor.isPresent()) {
			return mapper.toResponse(existingAuthor.get());
		}

		Author author = mapper.toEntity(authorRequest);
		Author savedAuthor = repository.save(author);
		return mapper.toResponse(savedAuthor);
	}

	@Override
	@Transactional
	public AuthorResponse updateAuthor(Long id, AuthorRequest authorRequest) {
		Author author = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Author not found for id: " + id));

		author.setName(authorRequest.name());
		author.setBiography(authorRequest.biography());

		return mapper.toResponse(author);
	}

	@Override
	@Transactional(readOnly = true)
	public AuthorResponse findById(Long id) {
		return repository.findById(id)
			.map(mapper::toResponse)
			.orElseThrow(() -> new ResourceNotFoundException("Author not found for id: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public AuthorResponse findByName(String name) {
		return repository.findAuthorByName(name)
			.map(mapper::toResponse)
			.orElseThrow(() -> new ResourceNotFoundException("Author not found for name: " + name));
	}

	@Override
	@Transactional(readOnly = true)
	public List<AuthorResponse> getAll() {
		return repository.findAll().stream()
			.map(mapper::toResponse)
			.toList();
	}

	@Override
	@Transactional
	public void deleteAuthor(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Author not found for id: " + id);
		}

		if (bookRepository.existsByAuthorId(id)) {
			throw new DataIntegrityException("Cannot delete author with assigned books");
		}

		repository.deleteById(id);
	}
}
