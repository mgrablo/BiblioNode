package io.github.mgrablo.BiblioNode.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Book extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(unique = true)
	private String isbn;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	private Author author;
}
