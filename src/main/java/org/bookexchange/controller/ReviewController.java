package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.ReviewDto;
import org.bookexchange.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
