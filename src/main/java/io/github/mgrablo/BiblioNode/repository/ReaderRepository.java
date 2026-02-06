package io.github.mgrablo.BiblioNode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import io.github.mgrablo.BiblioNode.model.Reader;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
	boolean existsByEmail(String email);

	Optional<Reader> findByEmail(String email);
}
