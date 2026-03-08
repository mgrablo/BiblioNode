package io.github.mgrablo.BiblioNode.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import io.github.mgrablo.BiblioNode.model.Reader;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
	boolean existsByUserEmail(String email);

	@EntityGraph(attributePaths = {"user"})
	Optional<Reader> findByUserEmail(String email);
}
