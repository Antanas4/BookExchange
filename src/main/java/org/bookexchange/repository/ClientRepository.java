package org.bookexchange.repository;

import org.bookexchange.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByUsername(String username);
}
