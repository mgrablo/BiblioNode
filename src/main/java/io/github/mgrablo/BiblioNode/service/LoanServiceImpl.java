package io.github.mgrablo.BiblioNode.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

import io.github.mgrablo.BiblioNode.config.LoanProperties;
import io.github.mgrablo.BiblioNode.dto.BorrowRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.exception.BookNotAvailableException;
import io.github.mgrablo.BiblioNode.exception.LoanAlreadyReturnedException;
import io.github.mgrablo.BiblioNode.exception.LoanLimitExceededException;
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
	private final LoanProperties loanProperties;

	private final Clock clock;

	@Override
	public LoanResponse borrowBook(BorrowRequest request, String email) {
		Book book = bookRepository.findById(request.bookId())
				.orElseThrow(() -> new ResourceNotFoundException("Book not found for id: " + request.bookId()));

		if (!book.isAvailable()) {
			throw new BookNotAvailableException("Book is currently not available for loan");
		}

		Reader reader = readerRepository.findByUserEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Reader not found"));

		Long activeLoansCount = loanRepository.countByReaderIdAndReturnDateIsNull(reader.getId());
		if (activeLoansCount >= loanProperties.getMaxActiveLoans()) {
			throw new LoanLimitExceededException("Reader has exceeded the maximum number of active loans (" + loanProperties.getMaxActiveLoans() + ")");
		}

		book.setAvailable(false);

		LocalDateTime now = LocalDateTime.now(clock);
		Loan loan = new Loan();
		loan.setLoanDate(now);
		loan.setDueDate(now.plusDays(loanProperties.getDefaultLoanDays()));
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

	@Override
	@Transactional(readOnly = true)
	public Page<LoanResponse> getAllLoans(Pageable pageable) {
		return loanRepository.findAll(pageable).map(mapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LoanResponse> getLoansByReaderId(Long readerId, Pageable pageable) {
		return loanRepository.findByReaderId(readerId, pageable).map(mapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LoanResponse> getLoansByBookId(Long bookId, Pageable pageable) {
		return loanRepository.findByBookId(bookId, pageable).map(mapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LoanResponse> getActiveLoans(Pageable pageable) {
		return loanRepository
				.findAllByReturnDateIsNull(pageable)
				.map(mapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LoanResponse> getActiveLoansByReaderId(Long readerId, Pageable pageable) {
		return loanRepository
				.findAllByReturnDateIsNullAndReaderId(readerId, pageable)
				.map(mapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LoanResponse> getOverdueLoans(Pageable pageable) {
		return loanRepository
				.findAllByReturnDateIsNullAndDueDateBefore(LocalDateTime.now(clock), pageable)
				.map(mapper::toResponse);
	}
}
