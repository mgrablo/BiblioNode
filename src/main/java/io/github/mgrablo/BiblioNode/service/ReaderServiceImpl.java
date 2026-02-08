package io.github.mgrablo.BiblioNode.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.mapper.ReaderMapper;
import io.github.mgrablo.BiblioNode.model.Reader;
import io.github.mgrablo.BiblioNode.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReaderServiceImpl implements ReaderService {

	private final ReaderRepository readerRepository;
	private final ReaderMapper mapper;

	@Override
	public ReaderResponse createReader(ReaderRequest request) {
		if (readerRepository.existsByEmail(request.email())) {
			throw new DataIntegrityException("Email already in use");
		}

		Reader reader = mapper.toEntity(request);
		return mapper.toResponse(readerRepository.save(reader));
	}

	@Override
	@Transactional(readOnly = true)
	public ReaderResponse getReaderById(Long id) {
		return readerRepository.findById(id)
				.map(mapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Reader not found for id: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public ReaderResponse getReaderByEmail(String email) {
		return readerRepository.findByEmail(email)
				.map(mapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Reader not found for email: " + email));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ReaderResponse> getAll(Pageable pageable) {
		return readerRepository.findAll(pageable)
				.map(mapper::toResponse);
	}

	@Override
	public ReaderResponse updateReader(Long id, ReaderRequest request) {
		Reader reader = readerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reader not found for id: " + id));

		if (!Objects.equals(reader.getEmail(), request.email()) && readerRepository.existsByEmail(request.email())) {
			throw new DataIntegrityException("New email already in use");
		}

		reader.setEmail(request.email());
		reader.setFullName(request.fullName());

		return mapper.toResponse(reader);
	}

	@Override
	public void deleteReader(Long id) {
		if (!readerRepository.existsById(id)) {
			throw new ResourceNotFoundException("Reader not found for id: " + id);
		}

		readerRepository.deleteById(id);
	}
}
