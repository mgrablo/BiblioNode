package io.github.mgrablo.BiblioNode.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

import io.github.mgrablo.BiblioNode.dto.*;
import io.github.mgrablo.BiblioNode.model.Loan;
import io.github.mgrablo.BiblioNode.model.User;
import io.github.mgrablo.BiblioNode.repository.BookRepository;
import io.github.mgrablo.BiblioNode.repository.LoanRepository;
import io.github.mgrablo.BiblioNode.repository.ReaderRepository;
import io.github.mgrablo.BiblioNode.repository.UserRepository;
import io.github.mgrablo.BiblioNode.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Profile("dev")
@Order(2)
@Slf4j
public class DevMockDataInitializer implements CommandLineRunner {
	private final UserService userService;
	private final AuthorService authorService;
	private final BookService bookService;
	private final ReaderService readerService;
	private final LoanService loanService;

	private final UserRepository userRepository;
	private final LoanRepository loanRepository;
	private final BookRepository bookRepository;
	private final ReaderRepository readerRepository;
	private final Clock clock;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		if (userRepository.existsByEmail("jan.kowalski@email.com")) {
			log.info("Development data already exists. Skipping seeding.");
			return;
		}

		log.info("Starting database seeding for development profile...");

		LocalDateTime now = LocalDateTime.now(clock);

		// Authors
		AuthorResponse sanderson = authorService.saveAuthor(
				new AuthorRequest("Brandon Sanderson", "Famous epic fantasy author known for the Cosmere universe.")
		);
		AuthorResponse wight = authorService.saveAuthor(
				new AuthorRequest("Will Wight", "Author of the popular 'Cradle' series, known for his unique magic systems.")
		);

		// Books
		log.info("Adding sample books to the database...");
		BookResponse wayOfKings = bookService.addBook(
				new BookRequest("The Way of Kings", "978-0765365279", sanderson.id())
		);
		BookResponse wordsOfRadiance = bookService.addBook(
				new BookRequest("Words of Radiance", "978-0765326362", sanderson.id())
		);
		BookResponse unsouled = bookService.addBook(
				new BookRequest("Unsouled", "978-0989671767", wight.id())
		);

		//Readers
		User user1 = createOrGetUser("jan.kowalski@email.com", "password123");
		ReaderResponse reader1 = readerService.createProfile(
				new ReaderRequest("Jan Kowalski"),
				user1
		);
		User user2 = createOrGetUser("anna.nowak@email.com", "password321");
		ReaderResponse reader2 = readerService.createProfile(
				new ReaderRequest("Anna Nowak"),
				user2
		);


		LoanResponse returnedLoan = loanService.borrowBook(new BorrowRequest(wayOfKings.id()), user1.getEmail());
		loanService.returnBook(returnedLoan.bookId());

		loanService.borrowBook(new BorrowRequest(wordsOfRadiance.id()), user2.getEmail());
		createOverdueLoan(reader2.id(), unsouled.id(), now);

		log.info("Database seeding completed successfully.");
	}

	private User createOrGetUser(String email, String password) {
		return userRepository.findByEmail(email)
				.orElseGet(() -> userService.createAccount(email, password));
	}

	private void createOverdueLoan(Long readerId, Long bookId, LocalDateTime now) {
		var reader = readerRepository.findById(readerId).orElseThrow(() -> new RuntimeException("Reader not found"));
		var book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

		var loan = new Loan();
		loan.setReader(reader);
		loan.setBook(book);
		loan.setLoanDate(now.minusDays(30));
		loan.setDueDate(now.minusDays(16));
		loan.setReturnDate(null);

		loanRepository.save(loan);

		var loanBook = loan.getBook();
		loanBook.setAvailable(false);
		bookRepository.save(loanBook);
	}
}
