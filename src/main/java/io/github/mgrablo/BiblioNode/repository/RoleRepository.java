package io.github.mgrablo.BiblioNode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import io.github.mgrablo.BiblioNode.model.Role;
import io.github.mgrablo.BiblioNode.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(RoleName name);
}
