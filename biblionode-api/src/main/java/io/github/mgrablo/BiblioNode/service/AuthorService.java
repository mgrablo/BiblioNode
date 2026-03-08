package io.github.mgrablo.BiblioNode.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;

public interface AuthorService {
	AuthorResponse saveAuthor(AuthorRequest authorRequest);

	AuthorResponse findById(Long id);

	AuthorResponse findByName(String name);

	AuthorResponse updateAuthor(Long id, AuthorRequest authorRequest);

	void deleteAuthor(Long id);

	Page<AuthorResponse> getAll(Pageable pageable);
}
