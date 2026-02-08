package io.github.mgrablo.BiblioNode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.config.annotation.web.PortMapperDsl;

import java.time.LocalDateTime;

import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
	@EntityGraph(attributePaths = { "book", "book.author", "reader" })
	Page<Loan> findByReaderId(Long readerId, Pageable pageable);

	@EntityGraph(attributePaths = { "book", "book.author", "reader" })
	Page<Loan> findByBookId(Long bookId, Pageable pageable);

	@EntityGraph(attributePaths = { "book", "book.author", "reader" })
	Page<Loan> findAllByReturnDateIsNull(Pageable pageable);

	@EntityGraph(attributePaths = { "book", "book.author", "reader" })
	Page<Loan> findAllByReturnDateIsNullAndReaderId(Long readerId, Pageable pageable);

	@EntityGraph(attributePaths = { "book", "book.author", "reader" })
	Page<Loan> findAllByReturnDateIsNullAndDueDateBefore(LocalDateTime now, Pageable pageable);
}
