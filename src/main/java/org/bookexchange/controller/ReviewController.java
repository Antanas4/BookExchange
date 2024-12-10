package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.ReviewDto;
import org.bookexchange.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")

public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/addReview")
    public ResponseEntity<String> addReview (@RequestBody ReviewDto reviewDto){
        try{
            reviewService.addReview(reviewDto);
            return ResponseEntity.ok().body("Successfully added review");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/reviews/{username}")
    public ResponseEntity<?> getRecipientReviews(@PathVariable String username){
        try{
            List<ReviewDto> reviews = reviewService.getRecipientReviews(username);
            return ResponseEntity.ok(reviews);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
