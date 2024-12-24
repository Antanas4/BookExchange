package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bookexchange.dto.PublicationDto;
import org.bookexchange.service.PublicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RestController
@RequestMapping("/api/publications")

public class PublicationsController {
    private final PublicationService publicationService;

    @PostMapping
    public ResponseEntity<String> createPublication(@RequestBody PublicationDto publicationDto) {
        publicationService.createPublication(publicationDto);
        return new ResponseEntity<>("Publication created", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PublicationDto>> getPublications() {
        return ResponseEntity.ok(publicationService.getPublications());
    }

    @GetMapping("/{publicationId}")
    public ResponseEntity<PublicationDto> getPublication(@PathVariable Integer publicationId) {
        return ResponseEntity.ok(publicationService.getPublication(publicationId));
    }

    @DeleteMapping("/{publicationId}")
    public ResponseEntity<String> deletePublication(@PathVariable Integer publicationId) {
        publicationService.deletePublication(publicationId);
        return ResponseEntity.ok("Publication deleted successfully.");
    }

    @PutMapping()
    public ResponseEntity<String> updatePublication(@RequestBody PublicationDto publicationDto) {
        publicationService.updatePublication(publicationDto);
        return ResponseEntity.ok("Publication updated successfully.");

    }

    @GetMapping("/shop")
    public ResponseEntity<List<PublicationDto>> getPublicationsShop() {
        List<PublicationDto> publicationDtos = publicationService.getPublicationShop();
        return ResponseEntity.ok(publicationDtos);
    }

    @PutMapping("/shop/borrow/{publicationId}")
    public ResponseEntity<String> borrowPublication(@PathVariable Integer publicationId) {
        publicationService.borrowPublication(publicationId);
        return ResponseEntity.ok("Publication reserved successfully.");
    }

    @PutMapping("/shop/buy/{publicationId}")
    public ResponseEntity<String> buyPublication(@PathVariable Integer publicationId) {
        publicationService.buyPublication(publicationId);
        return ResponseEntity.ok("Publication bought successfully.");
    }

    @PutMapping("/shop/return/{publicationId}")
    public ResponseEntity<String> returnPublication(@PathVariable Integer publicationId) {
        publicationService.returnPublication(publicationId);
        return ResponseEntity.ok("Publication borrowed successfully.");
    }

    @GetMapping("/myPublications")
    public ResponseEntity<?> getMyPublications() {
        List<PublicationDto> publications = publicationService.getMyPublications();
        return ResponseEntity.ok(publications);
    }

    @GetMapping("/myPublications/borrowed")
    public ResponseEntity<?> getMyBorrowedPublications() {
        List<PublicationDto> publications = publicationService.getMyBorrowedPublications();
        return ResponseEntity.ok(publications);
    }
}
