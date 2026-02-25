package io.github.mgrablo.BiblioNode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import io.github.mgrablo.BiblioNode.model.RoleName;
import io.github.mgrablo.BiblioNode.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);

	boolean existsByRolesName(RoleName roleName);
}
