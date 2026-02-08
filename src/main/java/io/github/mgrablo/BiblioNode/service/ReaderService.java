package io.github.mgrablo.BiblioNode.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;

public interface ReaderService {
	ReaderResponse createReader(ReaderRequest request);
	ReaderResponse getReaderById(Long id);
	ReaderResponse getReaderByEmail(String email);
	Page<ReaderResponse> getAll(Pageable pageable);
	ReaderResponse updateReader(Long id, ReaderRequest request);
	void deleteReader(Long id);
}
