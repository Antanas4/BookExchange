package org.bookexchange.repository;

import org.bookexchange.model.Client;
import org.bookexchange.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByReviewRecipient(Client client);
}
