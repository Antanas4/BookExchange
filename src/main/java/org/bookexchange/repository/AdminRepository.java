package org.bookexchange.repository;

import org.bookexchange.model.Admin;
import org.bookexchange.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByUsername(String username);
}
