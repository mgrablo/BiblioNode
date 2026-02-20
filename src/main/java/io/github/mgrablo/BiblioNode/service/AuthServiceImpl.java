package io.github.mgrablo.BiblioNode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.dto.RegisterRequest;
import io.github.mgrablo.BiblioNode.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
	private final UserService userService;
	private final ReaderService readerService;

	@Override
	public ReaderResponse register(RegisterRequest request) {
		User user = userService.createAccount(request.email(), request.password());
		ReaderRequest readerRequest = new ReaderRequest(request.fullName());

		return readerService.createProfile(readerRequest, user);
	}
}
