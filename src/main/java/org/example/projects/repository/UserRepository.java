package org.example.projects.repository;

import java.util.List;
import java.util.Optional;

import org.example.projects.entity.UserAccount;
import org.example.projects.entity.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByProfileEmail(String email);

    @EntityGraph(attributePaths = {"department", "profile"})
    List<UserAccount> findByRole(UserRole role);

    @EntityGraph(attributePaths = {"department", "profile"})
    Optional<UserAccount> findWithDepartmentAndProfileByUsername(String username);
}

