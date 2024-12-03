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
@RequestMapping("/api/client/publications")

public class PublicationsController {
    private final PublicationService publicationService;

    @PostMapping
    public ResponseEntity<String> createPublication (@RequestBody PublicationDto publicationDto){
        try{
            publicationService.createPublication(publicationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(publicationDto.toString());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PublicationDto>> getPublications(){
        try{
            List<PublicationDto> publicationDtos = publicationService.getPublications();
            return ResponseEntity.ok(publicationDtos);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{publicationId}")
    public ResponseEntity<String> deletePublication (@PathVariable Integer publicationId){
        try {
            publicationService.deletePublication(publicationId);
            return ResponseEntity.ok("Publication deleted successfully.");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication not found.");
        }
    }

    @PutMapping("/{publicationId}")
    public ResponseEntity<String> updatePublication(@RequestBody PublicationDto publicationDto) {
        try {
            publicationService.updatePublication(publicationDto);
            return ResponseEntity.ok("Publication updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    } //nereikia cia ID greiciausiai, perziureti!!
}
