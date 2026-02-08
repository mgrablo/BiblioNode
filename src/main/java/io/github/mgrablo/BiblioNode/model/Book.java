package io.github.mgrablo.BiblioNode.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, length = 20)
	private String isbn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	private Author author;

	@Column(nullable = false)
	private boolean available = true;

	public Book(Long id, String title, String isbn, Author author) {
		this.id = id;
		this.title = title;
		this.isbn = isbn;
		this.author = author;
		this.available = true;
	}
}
