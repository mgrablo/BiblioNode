package io.github.mgrablo.BiblioNode.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.mgrablo.BiblioNode.dto.BookRequest;
import io.github.mgrablo.BiblioNode.dto.BookResponse;
import io.github.mgrablo.BiblioNode.model.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {
	Book toEntity(BookRequest bookRequest);

	@Mapping(source = "author.id", target = "authorId")
	@Mapping(source = "author.name", target = "authorName")
	BookResponse toResponse(Book book);
}