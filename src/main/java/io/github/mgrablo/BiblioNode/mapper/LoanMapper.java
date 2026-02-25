package io.github.mgrablo.BiblioNode.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.model.Loan;

@Mapper(componentModel = "spring")
public interface LoanMapper {
	@Mapping(source = "book.id", target = "bookId")
	@Mapping(source = "book.title", target = "bookTitle")
	@Mapping(source = "book.isbn", target = "bookIsbn")
	@Mapping(source = "book.author.name", target = "bookAuthorName")
	@Mapping(source = "reader.id", target = "readerId")
	LoanResponse toResponse(Loan loan);
}
