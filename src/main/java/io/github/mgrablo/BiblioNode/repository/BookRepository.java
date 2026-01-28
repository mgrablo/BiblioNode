package io.github.mgrablo.BiblioNode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import io.github.mgrablo.BiblioNode.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	Optional<Book> findBookByTitle(String title);

	@Query("SELECT b FROM Book b WHERE " +
			"(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
			"(:authorName IS NULL OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :authorName, '%')))"
	)
	List<Book> searchByTitleAndAuthor(@Param("title") String title, @Param("authorName") String authorName);
}
