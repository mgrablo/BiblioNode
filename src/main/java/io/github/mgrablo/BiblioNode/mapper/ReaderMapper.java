package io.github.mgrablo.BiblioNode.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.model.Reader;

@Mapper(componentModel = "spring", uses = {LoanMapper.class})
public interface ReaderMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "loans", ignore = true)
	Reader toEntity(ReaderRequest request);

	ReaderResponse toResponse(Reader reader);
}
