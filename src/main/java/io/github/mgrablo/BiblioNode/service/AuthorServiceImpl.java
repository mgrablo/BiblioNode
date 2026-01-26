package io.github.mgrablo.BiblioNode.service;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository repository;

	@Autowired
	AuthorServiceImpl(AuthorRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public AuthorResponse saveAuthor(AuthorRequest authorRequest) {
		Optional<Author> existingAuthor = repository.findAuthorByName(authorRequest.name());
		if (existingAuthor.isPresent()) {
			return convertToDTO(existingAuthor.get());
		}

		Author author = convertToEntity(authorRequest);
		Author savedAuthor = repository.save(author);
		return convertToDTO(savedAuthor);
	}

	@Override
	@Transactional(readOnly = true)
	public AuthorResponse findById(Long id) {
		return repository.findById(id)
			.map(this::convertToDTO)
			.orElseThrow(() -> new ResourceNotFoundException("Author not found for id: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public AuthorResponse findByName(String name) {
		return repository.findAuthorByName(name)
			.map(this::convertToDTO)
			.orElseThrow(() -> new ResourceNotFoundException("Author not found for name: " + name));
	}

	@Override
	@Transactional(readOnly = true)
	public List<AuthorResponse> findAll() {
		return repository.findAll().stream()
			.map(this::convertToDTO)
			.toList();
	}

	private Author convertToEntity(AuthorRequest request) {
		return new Author(null, request.name(), request.biography(), null);
	}

	private AuthorResponse convertToDTO(Author author) {
		return new AuthorResponse(author.getId(), author.getName(), author.getBiography(), author.getBooks());
	}
}
