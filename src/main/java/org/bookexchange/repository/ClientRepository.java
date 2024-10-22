package org.bookexchange.repository;

import org.bookexchange.model.Client;
import org.bookexchange.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByUsername(String username);
}
