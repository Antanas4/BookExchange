package org.bookexchange.repository;

import org.bookexchange.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Integer> {
}
