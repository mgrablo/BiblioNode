package io.github.mgrablo.BiblioNode.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.mapper.ReaderMapper;
import io.github.mgrablo.BiblioNode.model.Reader;
import io.github.mgrablo.BiblioNode.repository.ReaderRepository;

@ExtendWith(MockitoExtension.class)
public class ReaderServiceImplTest {

	@Mock
	private ReaderMapper mapper;

	@Mock
	private ReaderRepository readerRepository;

	@InjectMocks
	private ReaderServiceImpl readerService;

	@Test
	public void createReader_ShouldReturnSuccess_WhenEmailNotInUse() {
		String email = "test@email.com";
		String name = "TestName";
		ReaderRequest request = new ReaderRequest(name, email);
		Reader reader = new Reader(1L, name, email, null);
		ReaderResponse expectedResponse = new ReaderResponse(1L, name, email, null);

		when(readerRepository.existsByEmail(email)).thenReturn(false);
		when(mapper.toEntity(request)).thenReturn(reader);
		when(readerRepository.save(reader)).thenReturn(reader);
		when(mapper.toResponse(reader)).thenReturn(expectedResponse);

		ReaderResponse result = readerService.createReader(request);

		assertEquals(expectedResponse, result);
	}

	@Test
	public void createReader_ShouldThrowException_WhenEmailInUse() {
		String email = "test@email.com";
		String name = "TestName";
		ReaderRequest request = new ReaderRequest(name, email);

		when(readerRepository.existsByEmail(email)).thenReturn(true);

		assertThrows(DataIntegrityException.class, () ->
				readerService.createReader(request)
		);
	}

	@Test
	public void getReaderById_ShouldReturnReader_WhenExists() {
		Long id = 1L;
		Reader reader = new Reader(id, "TestName", "test@email.com", null);
		ReaderResponse expectedResponse = new ReaderResponse(id, "TestName", "test@email.com", null);

		when(readerRepository.findById(id)).thenReturn(Optional.of(reader));
		when(mapper.toResponse(reader)).thenReturn(expectedResponse);

		ReaderResponse result = readerService.getReaderById(id);
		assertEquals(expectedResponse, result);
	}

	@Test
	public void getReaderById_ShouldThrowException_WhenDoesNotExist() {
		Long id = 1L;
		when(readerRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () ->
				readerService.getReaderById(id)
		);
	}


	@Test
	public void getReaderByEmail_ShouldReturnReader_WhenExists() {
		String email = "test@email.com";
		Reader reader = new Reader(1L, "TestName", email, null);
		ReaderResponse expectedResponse = new ReaderResponse(1L, "TestName", email, null);

		when(readerRepository.findByEmail(email)).thenReturn(Optional.of(reader));
		when(mapper.toResponse(reader)).thenReturn(expectedResponse);

		ReaderResponse result = readerService.getReaderByEmail(email);
		assertEquals(expectedResponse, result);
	}

	@Test
	public void getReaderByEmail_ShouldReturnReader_WhenDoesNotExist() {
		String email = "test@email.com";
		when(readerRepository.findByEmail(email)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () ->
				readerService.getReaderByEmail(email)
		);
	}

	@Test
	public void getAll_ShouldReturnList_WhenReadersExist() {
		Pageable pageable = Pageable.ofSize(10);
		Reader reader1 = new Reader(1L, "Test Name1", "test1@email.com", null);
		ReaderResponse response1 = new ReaderResponse(1L, "Test Name1", "test1@email.com", null);
		Reader reader2 = new Reader(2L, "Test Name2", "test2@email.com", null);
		ReaderResponse response2 = new ReaderResponse(2L, "Test Name2", "test2@email.com", null);
		Page<Reader> readerPage = new PageImpl<>(List.of(reader1, reader2));

		when(readerRepository.findAll(pageable)).thenReturn(readerPage);
		when(mapper.toResponse(reader1)).thenReturn(response1);
		when(mapper.toResponse(reader2)).thenReturn(response2);

		Page<ReaderResponse> result = readerService.getAll(pageable);

		assertFalse(result.isEmpty());
		assertEquals(2, result.getTotalElements());
		assertEquals(response1, result.getContent().getFirst());
		assertEquals(response2, result.getContent().get(1));
		verify(readerRepository).findAll(pageable);
	}

	@Test
	public void getAll_ShouldReturnEmptyList_WhenReadersDoNotExist() {
		Pageable pageable = Pageable.ofSize(10);

		when(readerRepository.findAll(pageable)).thenReturn(Page.empty());

		Page<ReaderResponse> result = readerService.getAll(pageable);

		assertTrue(result.isEmpty());
		verify(readerRepository).findAll(pageable);
	}

	@Test
	public void updateReader_ShouldReturnUpdatedReader_WhenReaderExists_SameEmail() {
		Long id = 1L;
		ReaderRequest request = new ReaderRequest("New Name", "old@email.com");
		Reader reader = new Reader(id, "Old Name", "old@email.com", null);
		ReaderResponse expectedResponse = new ReaderResponse(id, "New Name", "old@email.com", null);

		when(readerRepository.findById(id)).thenReturn(Optional.of(reader));
		when(mapper.toResponse(reader)).thenReturn(expectedResponse);

		ReaderResponse result = readerService.updateReader(id, request);

		assertEquals(expectedResponse, result);
	}

	@Test
	public void updateReader_ShouldReturnUpdatedReader_WhenReaderExists_NewEmailNotInUse() {
		Long id = 1L;
		String newEmail = "new@email.com";
		ReaderRequest request = new ReaderRequest("New Name", newEmail);
		Reader reader = new Reader(id, "Old Name", "old@email.com", null);
		ReaderResponse expectedResponse = new ReaderResponse(id, "New Name", newEmail, null);

		when(readerRepository.findById(id)).thenReturn(Optional.of(reader));
		when(readerRepository.existsByEmail(newEmail)).thenReturn(false);
		when(mapper.toResponse(reader)).thenReturn(expectedResponse);

		ReaderResponse result = readerService.updateReader(id, request);

		assertEquals(expectedResponse, result);
	}

	@Test
	public void updateReader_ShouldThrowException_WhenReaderExists_NewEmailInUse() {
		Long id = 1L;
		String newEmail = "new@email.com";
		ReaderRequest request = new ReaderRequest("New Name", newEmail);
		Reader reader = new Reader(id, "Old Name", "old@email.com", null);

		when(readerRepository.findById(id)).thenReturn(Optional.of(reader));
		when(readerRepository.existsByEmail(newEmail)).thenReturn(true);

		assertThrows(DataIntegrityException.class, () ->
				readerService.updateReader(id, request)
		);

		// Reader not updated
		assertEquals(1L, reader.getId());
		assertEquals("Old Name", reader.getFullName());
		assertEquals("old@email.com", reader.getEmail());
	}

	@Test
	public void updateReader_ShouldThrowException_WhenReaderDoesNotExist() {
		Long id = 1L;
		String newEmail = "new@email.com";
		ReaderRequest request = new ReaderRequest("New Name", newEmail);

		when(readerRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () ->
				readerService.updateReader(id, request)
		);
	}

	@Test
	public void deleteReader_ShouldDeleteReader_WhenReaderExists() {
		Long id = 1L;

		when(readerRepository.existsById(id)).thenReturn(true);

		readerService.deleteReader(id);

		verify(readerRepository, times(1)).deleteById(id);
	}

	@Test
	public void deleteReader_ShouldThrowException_WhenReaderDoesNotExist() {
		Long id = 1L;

		when(readerRepository.existsById(id)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () ->
				readerService.deleteReader(id)
		);
		verify(readerRepository, never()).deleteById(id);
	}
}
