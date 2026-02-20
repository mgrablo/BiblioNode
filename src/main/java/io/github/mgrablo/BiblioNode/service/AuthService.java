package io.github.mgrablo.BiblioNode.service;

import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.dto.RegisterRequest;

public interface AuthService {
	ReaderResponse register(RegisterRequest request);
}
