package io.github.mgrablo.BiblioNode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.mgrablo.BiblioNode.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
