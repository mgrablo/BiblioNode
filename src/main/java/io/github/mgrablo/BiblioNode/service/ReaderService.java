package io.github.mgrablo.BiblioNode.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.dto.UserProfileResponse;
import io.github.mgrablo.BiblioNode.model.User;

public interface ReaderService {
	ReaderResponse createProfile(ReaderRequest request, User user);
	ReaderResponse getReaderById(Long id);
	ReaderResponse getReaderByEmail(String email);
	UserProfileResponse getUserProfileByEmail(String email);
	Page<ReaderResponse> getAll(Pageable pageable);
	ReaderResponse updateReader(Long id, ReaderRequest request);
	void deleteReader(Long id);
}
