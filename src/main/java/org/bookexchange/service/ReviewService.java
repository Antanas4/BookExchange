package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.ReviewDto;
import org.bookexchange.model.Client;
import org.bookexchange.model.Review;
import org.bookexchange.repository.ClientRepository;
import org.bookexchange.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ClientRepository clientRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public void addReview(ReviewDto reviewDto) {
        Optional<Client> optionalRecipient = clientRepository.findByUsername(reviewDto.getRecipient());
        String authorUsername = userService.getCurrentUsername();
        Optional<Client> optionalAuthor = clientRepository.findByUsername(authorUsername);

        if (optionalRecipient.isPresent() && optionalAuthor.isPresent()) {
            Client recipient = optionalRecipient.get();
            Client author = optionalAuthor.get();
            LocalDateTime currentDateTime = LocalDateTime.now();
            Review review = new Review(
                    reviewDto.getTitle(),
                    reviewDto.getBody(),
                    currentDateTime,
                    author,
                    recipient
            );
            reviewRepository.save(review);
        }
    }

    public List<ReviewDto> getRecipientReviews(String username) {
        Optional<Client> Recipient = clientRepository.findByUsername(username);
        if (Recipient.isPresent()) {
            List<Review> reviews = reviewRepository.findByReviewRecipient(Recipient.get());
            return reviews.stream()
                    .map(review -> new ReviewDto(
                            review.getTitle(),
                            review.getBody(),
                            review.getTimestamp(),
                            review.getReviewAuthor().getUsername(),
                            review.getReviewRecipient().getUsername()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
