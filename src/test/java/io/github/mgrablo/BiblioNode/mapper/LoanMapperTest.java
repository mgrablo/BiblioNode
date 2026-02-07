package io.github.mgrablo.BiblioNode.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.model.Book;
import io.github.mgrablo.BiblioNode.model.Loan;
import io.github.mgrablo.BiblioNode.model.Reader;

public class LoanMapperTest {
	private final LoanMapper mapper = Mappers.getMapper(LoanMapper.class);

	@Test
	public void shouldMapLoanToLoanResponse() {
		LocalDateTime testDate = LocalDateTime.of(2024, 1, 1, 12, 0);
		Author author = new Author(1L, "Test Author", "Bio", null);
		Book book = new Book(1L, "Test Book", "111", author, true);
		Reader reader = new Reader(1L, "Test Reader", "test@email.com", null);
		Loan loan = new Loan(1L, book, reader, testDate, null, null);

		var response = mapper.toResponse(loan);

		assertEquals(loan.getId(), response.id());
		assertEquals(loan.getBook().getId(), response.bookId());
		assertEquals(loan.getBook().getTitle(), response.bookTitle());
		assertEquals(loan.getBook().getIsbn(), response.bookIsbn());
		assertEquals(loan.getBook().getAuthor().getName(), response.bookAuthorName());
		assertEquals(loan.getReader().getId(), response.readerId());
		assertEquals(loan.getLoanDate(), response.loanDate());
	}
}
