package io.github.mgrablo.BiblioNode.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "authors")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Author extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "TEXT")
	@Lob
	private String biography;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<Book> books;
}
