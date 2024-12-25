package org.bookexchange.repository;

import org.bookexchange.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    @Query("SELECT p FROM Publication p WHERE p.owner.username != :username")
    List<Publication> findAllExcludingCurrentUser(@Param("username") String username);
    List<Publication> findByOwnerId(int id);
    List<Publication> findByBorrowerId(int id);
}
