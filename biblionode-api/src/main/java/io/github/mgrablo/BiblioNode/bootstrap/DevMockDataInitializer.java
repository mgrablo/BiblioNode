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
				new BookRequest("The Way of Kings", "978-0765365279", sanderson.id(), "https://placehold.co/300x400/3F5D4E/FFF?text=The+Way+of+Kings", """
						Roshar is a world of stone and storms. Uncanny tempests of incredible power sweep across the rocky terrain so frequently that they have shaped ecology and civilization alike. Animals hide in shells, trees pull in branches, and grass retracts into the soilless ground. Cities are built only where the topography offers shelter.
						
						It has been centuries since the fall of the ten consecrated orders known as the Knights Radiant, but their Shardblades and Shardplate remain: mystical swords and suits of armor that transform ordinary men into near-invincible warriors. Men trade kingdoms for Shardblades. Wars were fought for them, and won by them.
						
						One such war rages on a ruined landscape called the Shattered Plains. There, Kaladin, who traded his medical apprenticeship for a spear to protect his little brother, has been reduced to slavery. In a war that makes no sense, where ten armies fight separately against a single foe, he struggles to save his men and to fathom the leaders who consider them expendable.
						
						Brightlord Dalinar Kholin commands one of those other armies. Like his brother, the late king, he is fascinated by an ancient text called The Way of Kings. Troubled by over-powering visions of ancient times and the Knights Radiant, he has begun to doubt his own sanity.
						
						Across the ocean, an untried young woman named Shallan seeks to train under an eminent scholar and notorious heretic, Dalinar's niece, Jasnah. Though she genuinely loves learning, Shallan's motives are less than pure. As she plans a daring theft, her research for Jasnah hints at secrets of the Knights Radiant and the true cause of the war.
						
						The result of over ten years of planning, writing, and world-building, The Way of Kings is but the opening movement of the Stormlight Archive, a bold masterpiece in the making.
						
						Speak again the ancient oaths:
						
						Life before death.
						Strength before weakness.
						Journey before Destination.
						
						and return to men the Shards they once bore.
						
						The Knights Radiant must stand again.
						""")
		);
		BookResponse wordsOfRadiance = bookService.addBook(
				new BookRequest("Words of Radiance", "978-0765326362", sanderson.id(), null, null)
		);
		BookResponse unsouled = bookService.addBook(
				new BookRequest("Unsouled", "978-0989671767", wight.id(), null, null)
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
