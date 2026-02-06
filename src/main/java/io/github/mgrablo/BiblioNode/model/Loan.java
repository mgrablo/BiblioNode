package io.github.mgrablo.BiblioNode.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loans")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Loan extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reader_id", nullable = false)
	private Reader reader;

	@Column(nullable = false)
	private LocalDateTime loanDate;

	@Column(nullable = false)
	private LocalDateTime dueDate;

	@Column
	private LocalDateTime returnDate;
}
