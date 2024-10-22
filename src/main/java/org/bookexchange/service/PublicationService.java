package org.bookexchange.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bookexchange.dto.PublicationDto;
import org.bookexchange.model.*;
import org.bookexchange.repository.ClientRepository;
import org.bookexchange.repository.PublicationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor

public class PublicationService {
    private final ClientRepository clientRepository;
    private final PublicationRepository publicationRepository;

    public void createPublication(PublicationDto publicationDto) {
        System.out.println("PublicationDto: " + publicationDto);
        Client client = clientRepository.findByUsername(publicationDto.getOwnerUsername())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        Publication publication;
        switch (publicationDto.getPublicationType()) {
            case "Book" -> {
                publication = new Book();
                ((Book) publication).setGenre(publicationDto.getGenre());
            }
            case "Comic Book" -> {
                publication = new ComicBook();
                ((ComicBook) publication).setIllustrator(publicationDto.getIllustrator());
            }
            case "Magazine" -> {
                publication = new Magazine();
                ((Magazine) publication).setIssueNumber(publicationDto.getIssueNumber());
            }
            default ->
                    throw new IllegalArgumentException("Unsupported publication type: " + publicationDto.getPublicationType());
        }
        publication.setTitle(publicationDto.getTitle());
        publication.setAuthor(publicationDto.getAuthor());
        publication.setPrice(publicationDto.getPrice());
        publication.setOwner(client);
        publicationRepository.save(publication);
    }

    public List<PublicationDto> getPublications(){
        List<PublicationDto> publicationDtos= new ArrayList<>();
        List<Publication> publications = publicationRepository.findAll();
        for (Publication publication : publications) {
            PublicationDto publicationDto = new PublicationDto();
            publicationDto.setId(publication.getId());
            publicationDto.setTitle(publication.getTitle());
            publicationDto.setAuthor(publication.getAuthor());
            publicationDto.setPrice(publication.getPrice());
            publicationDto.setOwnerUsername(publication.getOwner().getUsername());
            if (publication instanceof Book) {
                Book book = (Book) publication;
                publicationDto.setPublicationType("Book");
                publicationDto.setGenre(book.getGenre());
            } else if (publication instanceof ComicBook) {
                ComicBook comicBook = (ComicBook) publication;
                publicationDto.setPublicationType("Comic Book");
                publicationDto.setIllustrator(comicBook.getIllustrator());
            } else if (publication instanceof Magazine) {
                Magazine magazine = (Magazine) publication;
                publicationDto.setPublicationType("Magazine");
                publicationDto.setIssueNumber(magazine.getIssueNumber());
            }
            publicationDtos.add(publicationDto);
        }
        return publicationDtos;
    }

    public void deletePublication(Integer publicationId) {
        Optional<Publication> publication = publicationRepository.findById(publicationId);
        if (publication.isPresent()) {
            publicationRepository.delete(publication.get());
        } else {
            throw new NoSuchElementException("Publication not found: " + publicationId);
        }
    }

    public void updatePublication(PublicationDto publicationDto) {
        Optional<Publication> publication = publicationRepository.findById(publicationDto.getId());
        if (publication.isPresent()) {
            Publication publicationToUpdate = publication.get();
            publicationToUpdate.setTitle(publicationDto.getTitle());
            publicationToUpdate.setAuthor(publicationDto.getAuthor());
            publicationToUpdate.setPrice(publicationDto.getPrice());

            switch (publicationToUpdate) {
                case Book book -> book.setGenre(publicationDto.getGenre());
                case ComicBook comicBook -> comicBook.setIllustrator(publicationDto.getIllustrator());
                case Magazine magazine -> magazine.setIssueNumber(publicationDto.getIssueNumber());
                default -> {
                    throw new IllegalArgumentException("Unsupported publication type: " + publicationDto.getPublicationType());
                }
            }
            publicationRepository.save(publicationToUpdate);
        } else {
            throw new IllegalArgumentException("Publication not found: " + publicationDto.getId());
        }
    }
}
