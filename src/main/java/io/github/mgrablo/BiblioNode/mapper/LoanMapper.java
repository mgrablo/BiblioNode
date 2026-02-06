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
	Loan toEntity(LoanRequest request);

	@Mapping(source = "book.id", target = "bookId")
	@Mapping(source = "book.title", target = "bookTitle")
	@Mapping(source = "book.author.name", target = "bookAuthorName")
	@Mapping(source = "reader.id", target = "readerId")
	LoanResponse toResponse(Loan loan);
}
