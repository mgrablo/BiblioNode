package io.github.mgrablo.BiblioNode.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;
import io.github.mgrablo.BiblioNode.model.Author;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "books", ignore = true)
	Author toEntity(AuthorRequest request);

	AuthorResponse toResponse(Author author);
}
