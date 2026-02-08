package io.github.mgrablo.BiblioNode.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import io.github.mgrablo.BiblioNode.config.JpaConfig;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.model.Book;
import io.github.mgrablo.BiblioNode.model.Loan;
import io.github.mgrablo.BiblioNode.model.Reader;

@DataJpaTest
@Import(JpaConfig.class)
public class LoanRepositoryTest {
	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Clock fixedClock;
	private final Instant fixedInstant = Instant.parse("2026-01-01T12:00:00Z");
	private final ZoneId zoneId = ZoneId.of("UTC");

	@BeforeEach
	void setup() {
		fixedClock = Clock.fixed(fixedInstant, zoneId);
	}

	@Test
	public void shouldFindOverdueLoans() {
		// GIVEN
		Author author = new Author(null, "Author1", "Bio", null);
		entityManager.persist(author);

		Book book = new Book(null, "Book 1", "1", author, true);
		entityManager.persist(book);

		Reader reader = new Reader(null, "Reader1", "reader@email.com", null);
		entityManager.persist(reader);

		LocalDateTime now = LocalDateTime.now(fixedClock);

		Loan overdueLoan = new Loan();
		overdueLoan.setBook(book);
		overdueLoan.setReader(reader);
		overdueLoan.setLoanDate(now.minusDays(20));
		overdueLoan.setDueDate(now.minusDays(6));
		overdueLoan.setReturnDate(null);
		entityManager.persist(overdueLoan);

		Loan activeLoan = new Loan();
		activeLoan.setBook(book);
		activeLoan.setReader(reader);
		activeLoan.setLoanDate(now.minusDays(10));
		activeLoan.setDueDate(now.plusDays(4));
		activeLoan.setReturnDate(null);
		entityManager.persist(activeLoan);

		Loan returnedLoan = new Loan();
		returnedLoan.setBook(book);
		returnedLoan.setReader(reader);
		returnedLoan.setLoanDate(now.minusDays(15));
		returnedLoan.setDueDate(now.minusDays(1));
		returnedLoan.setReturnDate(now.minusDays(2));
		entityManager.persist(returnedLoan);

		entityManager.flush();

		// WHEN
		Page<Loan> overdueLoans = loanRepository.findAllByReturnDateIsNullAndDueDateBefore(now, Pageable.ofSize(10));

		// THEN
		assertEquals(1, overdueLoans.getTotalElements());
		assertEquals(overdueLoan.getId(), overdueLoans.getContent().getFirst().getId());
	}

	@Test
	public void shouldFindActiveLoansByReaderId() {
		// GIVEN
		Author author = new Author(null, "Author1", "Bio", null);
		entityManager.persist(author);

		Book book = new Book(null, "Book 1", "1", author, true);
		entityManager.persist(book);

		Reader reader1 = new Reader(null, "Reader1", "reader1@email.com", null);
		entityManager.persist(reader1);

		Reader reader2 = new Reader(null, "Reader2", "reader2@email.com", null);
		entityManager.persist(reader2);

		LocalDateTime now = LocalDateTime.now(fixedClock);

		Loan activeLoan1 = new Loan();
		activeLoan1.setBook(book);
		activeLoan1.setReader(reader1);
		activeLoan1.setLoanDate(now.minusDays(10));
		activeLoan1.setDueDate(now.plusDays(4));
		activeLoan1.setReturnDate(null);
		entityManager.persist(activeLoan1);

		Loan activeLoan2 = new Loan();
		activeLoan2.setBook(book);
		activeLoan2.setReader(reader2);
		activeLoan2.setLoanDate(now.minusDays(5));
		activeLoan2.setDueDate(now.plusDays(9));
		activeLoan2.setReturnDate(null);
		entityManager.persist(activeLoan2);

		Loan returnedLoan1 = new Loan();
		returnedLoan1.setBook(book);
		returnedLoan1.setReader(reader1);
		returnedLoan1.setLoanDate(now.minusDays(15));
		returnedLoan1.setDueDate(now.minusDays(1));
		returnedLoan1.setReturnDate(now.minusDays(2));
		entityManager.persist(returnedLoan1);

		Loan returnedLoan2 = new Loan();
		returnedLoan2.setBook(book);
		returnedLoan2.setReader(reader2);
		returnedLoan2.setLoanDate(now.minusDays(20));
		returnedLoan2.setDueDate(now.minusDays(6));
		returnedLoan2.setReturnDate(now.minusDays(5));
		entityManager.persist(returnedLoan2);

		entityManager.flush();

		// WHEN
		Page<Loan> activeLoansReader1 = loanRepository.findAllByReturnDateIsNullAndReaderId(reader1.getId(), Pageable.ofSize(10));
		Page<Loan> activeLoansReader2 = loanRepository.findAllByReturnDateIsNullAndReaderId(reader2.getId(), Pageable.ofSize(10));

		// THEN
		assertEquals(1, activeLoansReader1.getTotalElements());
		assertEquals(activeLoan1.getId(), activeLoansReader1.getContent().getFirst().getId());

		assertEquals(1, activeLoansReader2.getTotalElements());
		assertEquals(activeLoan2.getId(), activeLoansReader2.getContent().getFirst().getId());
	}
}
