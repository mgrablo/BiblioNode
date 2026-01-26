package io.github.mgrablo.BiblioNode.service;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;

import java.util.List;

public interface AuthorService {
	AuthorResponse saveAuthor(AuthorRequest authorRequest);
	AuthorResponse findById(Long id);
	AuthorResponse findByName(String name);
	List<AuthorResponse> findAll();
}
