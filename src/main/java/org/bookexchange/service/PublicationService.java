package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.PublicationDto;
import org.bookexchange.dto.TransactionDto;
import org.bookexchange.model.*;
import org.bookexchange.model.enums.PublicationStatus;
import org.bookexchange.model.enums.TransactionType;
import org.bookexchange.repository.ClientRepository;
import org.bookexchange.repository.PublicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class PublicationService {
    private final ClientRepository clientRepository;
    private final PublicationRepository publicationRepository;
    private final UserService userService;
    private final TransactionService transactionService;

    @Transactional
    public void createPublication(PublicationDto publicationDto) {
        Publication publication = mapToEntity(publicationDto);
        publication.setOwner(clientRepository.findByUsername(publicationDto.getOwnerUsername()));
        publication.setStatus(PublicationStatus.AVAILABLE);
        publicationRepository.save(publication);
    }

    public List<PublicationDto> getPublications() {
        return publicationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePublication(Integer publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new IllegalArgumentException("Publication not found: " + publicationId));
        publicationRepository.delete(publication);
    }

    @Transactional
    public void updatePublication(PublicationDto publicationDto) {
        Publication publication = publicationRepository.findById(publicationDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Publication not found: " + publicationDto.getId()));
        updateEntity(publication, publicationDto);
        publicationRepository.save(publication);
    }

    public List<PublicationDto> getPublicationShop() {
        String currentUser = userService.getCurrentUsername();
        return publicationRepository.findAllExcludingCurrentUser(currentUser).stream()
                .filter(publication -> publication.getStatus() != PublicationStatus.BOUGHT)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void borrowPublication(Integer publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new NoSuchElementException("Publication not found with id: " + publicationId));

        String currentUser = userService.getCurrentUsername();
        Client borrower = clientRepository.findByUsername(currentUser);
        publication.setBorrower(borrower);
        publication.setStatus(PublicationStatus.BORROWED);
        createTransaction(publication, TransactionType.RENT);
    }

    public void buyPublication(Integer publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new NoSuchElementException("Publication not found with id: " + publicationId));
        String currentUser = userService.getCurrentUsername();
        Client recipientClient = clientRepository.findByUsername(currentUser);
        createTransaction(publication, TransactionType.BUY);
        publication.setStatus(PublicationStatus.BOUGHT);
        publication.setOwner(recipientClient);
    }

    public void returnPublication(Integer publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new NoSuchElementException("Publication not found with id: " + publicationId));
        publication.setBorrower(null);
        publication.setStatus(PublicationStatus.AVAILABLE);
        createTransaction(publication, TransactionType.RETURN);
    }

    public List<PublicationDto> getMyPublications() {
        String currentUser = userService.getCurrentUsername();
        Client owner = clientRepository.findByUsername(currentUser);
        List<Publication> publications = publicationRepository.findByOwnerId(owner.getId());
        return publications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<PublicationDto> getMyBorrowedPublications() {
        String username = userService.getCurrentUsername();
        Client borrower = clientRepository.findByUsername(username);
        List<Publication> publications = publicationRepository.findByBorrowerId(borrower.getId());
        return publications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PublicationDto getPublication(Integer publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new NoSuchElementException("Publication not found with id: " + publicationId));

        return mapToDto(publication);
    }

    private String getPublicationType(Publication publication) {
        if (publication instanceof Book) {
            return "Book";
        } else if (publication instanceof ComicBook) {
            return "Comic Book";
        } else if (publication instanceof Magazine) {
            return "Magazine";
        } else {
            return "Unknown";
        }
    }

    private PublicationDto mapToDto(Publication publication) {
        return publication.toDto();
    }

    private Publication mapToEntity(PublicationDto dto) {
        Publication publication = switch (dto.getPublicationType().toUpperCase()) {
            case "BOOK" -> new Book(dto.getGenre());
            case "COMIC BOOK" -> new ComicBook(dto.getIllustrator());
            case "MAGAZINE" -> new Magazine(dto.getIssueNumber());
            default -> throw new IllegalArgumentException("Unsupported publication type: " + dto.getPublicationType());
        };
        publication.setTitle(dto.getTitle());
        publication.setAuthor(dto.getAuthor());
        publication.setPrice(dto.getPrice());
        return publication;
    }

    private void updateEntity(Publication publication, PublicationDto dto) {
        publication.setTitle(dto.getTitle());
        publication.setAuthor(dto.getAuthor());
        publication.setPrice(dto.getPrice());
        switch (publication) {
            case Book book -> book.setGenre(dto.getGenre());
            case ComicBook comicBook -> comicBook.setIllustrator(dto.getIllustrator());
            case Magazine magazine -> magazine.setIssueNumber(dto.getIssueNumber());
            default -> {
            }
        }
    }

    private void createTransaction(Publication publication, TransactionType transactionType) {
        String username = userService.getCurrentUsername();
        Client recipientClient = clientRepository.findByUsername(username);
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setPublicationId(publication.getId());
        transactionDto.setOwnerId(publication.getOwner().getId());
        transactionDto.setRecipientId(recipientClient.getId());
        transactionDto.setTransactionType(transactionType);
        transactionService.createTransaction(transactionDto);
    }
}
