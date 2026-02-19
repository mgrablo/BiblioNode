package io.github.mgrablo.BiblioNode.service;

import io.github.mgrablo.BiblioNode.model.User;

public interface UserService {
	User createAccount(String email, String password);
	void updateEmail(Long userId, String newEmail);
}
