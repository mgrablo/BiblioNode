package io.github.mgrablo.BiblioNode.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.mgrablo.BiblioNode.dto.LoanRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.model.Loan;

@Mapper(componentModel = "spring")
public interface LoanMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "book", ignore = true)
	@Mapping(target = "reader", ignore = true)
	@Mapping(target = "loanDate", ignore = true)
	@Mapping(target = "dueDate", ignore = true)
	@Mapping(target = "returnDate", ignore = true)
	Loan toEntity(LoanRequest request);

	@Mapping(source = "book.id", target = "bookId")
	@Mapping(source = "book.title", target = "bookTitle")
	@Mapping(source = "book.isbn", target = "bookIsbn")
	@Mapping(source = "book.author.name", target = "bookAuthorName")
	@Mapping(source = "reader.id", target = "readerId")
	LoanResponse toResponse(Loan loan);
}
