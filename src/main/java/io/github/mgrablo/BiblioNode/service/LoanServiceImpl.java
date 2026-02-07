package io.github.mgrablo.BiblioNode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

import io.github.mgrablo.BiblioNode.dto.LoanRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.exception.BookNotAvailableException;
import io.github.mgrablo.BiblioNode.exception.LoanAlreadyReturnedException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.mapper.LoanMapper;
import io.github.mgrablo.BiblioNode.model.Book;
import io.github.mgrablo.BiblioNode.model.Loan;
import io.github.mgrablo.BiblioNode.model.Reader;
import io.github.mgrablo.BiblioNode.repository.BookRepository;
import io.github.mgrablo.BiblioNode.repository.LoanRepository;
import io.github.mgrablo.BiblioNode.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanServiceImpl implements LoanService {
	private final LoanRepository loanRepository;
	private final BookRepository bookRepository;
	private final ReaderRepository readerRepository;

	private final LoanMapper mapper;

	private final Clock clock;

	@Override
	public LoanResponse borrowBook(LoanRequest request) {
		Book book = bookRepository.findById(request.bookId())
				.orElseThrow(() -> new ResourceNotFoundException("Book not found for id: " + request.bookId()));

		if (!book.isAvailable()) {
			throw new BookNotAvailableException("Book is currently not available for loan");
		}

		Reader reader = readerRepository.findById(request.readerId())
				.orElseThrow(() -> new ResourceNotFoundException("Reader not found for id: " + request.readerId()));

		book.setAvailable(false);

		LocalDateTime now = LocalDateTime.now(clock);
		Loan loan = new Loan();
		loan.setLoanDate(now);
		loan.setDueDate(now.plusDays(14));
		loan.setBook(book);
		loan.setReader(reader);

		return mapper.toResponse(loanRepository.save(loan));
	}

	@Override
	public LoanResponse returnBook(Long loanId) {
		 Loan loan = loanRepository.findById(loanId)
				.orElseThrow(() -> new ResourceNotFoundException("Loan not found for id: " + loanId));

		if (loan.getReturnDate() != null) {
			throw new LoanAlreadyReturnedException("Book has already been returned");
		}
		loan.getBook().setAvailable(true);
		loan.setReturnDate(LocalDateTime.now(clock));
		return mapper.toResponse(loanRepository.save(loan));
	}
}
