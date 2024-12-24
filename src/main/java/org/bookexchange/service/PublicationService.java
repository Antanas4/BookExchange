package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.PublicationDto;
import org.bookexchange.dto.TransactionDto;
import org.bookexchange.model.*;
import org.bookexchange.model.enums.PublicationStatus;
import org.bookexchange.model.enums.TransactionType;
import org.bookexchange.repository.ClientRepository;
import org.bookexchange.repository.PublicationRepository;
import org.bookexchange.repository.TransactionRepository;
import org.bookexchange.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class PublicationService {
    private final ClientRepository clientRepository;
    private final PublicationRepository publicationRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    public void createPublication(PublicationDto publicationDto) {
        Publication publication = mapToEntity(publicationDto);
        publication.setOwner(clientRepository.findByUsername(publicationDto.getOwnerUsername()));
        publicationRepository.save(publication);
    }

    public List<PublicationDto> getPublications(){
        return publicationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void deletePublication(Integer publicationId) {
        publicationRepository.deleteById(publicationId);
    }

    public void updatePublication(PublicationDto publicationDto) {
        Publication publication = publicationRepository.findById(publicationDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Publication not found: " + publicationDto.getId()));
        updateEntity(publication, publicationDto);
        publicationRepository.save(publication);
    }

    public List<PublicationDto> getPublicationShop() {
        String currentUser = userService.getCurrentUsername();
        return publicationRepository.findAllExcludingCurrentUser(currentUser).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

//    fix borrowing publications logic and exclude transactions from publication class
    public void borrowPublication(Integer publicationId) {
        Optional<Publication> publication = publicationRepository.findById(publicationId);
        if (publication.isPresent()) {
            Publication publicationReserved = publication.get();
            TransactionDto transactionDto = new TransactionDto();
            String currentUser = userService.getCurrentUsername();
            Client recipientClient = clientRepository.findByUsername(currentUser);
            publicationReserved.setStatus(PublicationStatus.RESERVED);
            transactionDto.setPublicationId(publicationReserved.getId());
            transactionDto.setOwnerId(publicationReserved.getOwner().getId());
            transactionDto.setRecipientId(recipientClient.getId());
            transactionDto.setTransactionType(TransactionType.RENT);
            transactionService.createTransaction(transactionDto);
        }
    }

    public void buyPublication(Integer publicationId) {
        Optional<Publication> publication = publicationRepository.findById(publicationId);
        if (publication.isPresent()) {
            Publication publicationBought = publication.get();
            TransactionDto transactionDto = new TransactionDto();
            String username = userService.getCurrentUsername();
            Client recipientClient = clientRepository.findByUsername(username);
            publicationBought.setStatus(PublicationStatus.SOLD);
            transactionDto.setPublicationId(publicationBought.getId());
            transactionDto.setOwnerId(publicationBought.getOwner().getId());
            transactionDto.setRecipientId(recipientClient.getId());
            transactionDto.setTransactionType(TransactionType.BUY);
            transactionService.createTransaction(transactionDto);
            recipientClient.getOwnedPublications().add(publicationBought);
        }
    }

    public List<PublicationDto> getMyPublications() {
        String username = userService.getCurrentUsername();
        Client client = clientRepository.findByUsername(username);

        return client.getOwnedPublications().stream()
                .filter(publication ->
                        !(publication.getStatus() == PublicationStatus.RESERVED
                                && publication.getOwner().getId() != client.getId()))
                .map(publication -> {
                    PublicationDto dto = new PublicationDto();
                    dto.setId(publication.getId());
                    dto.setAuthor(publication.getAuthor());
                    dto.setTitle(publication.getTitle());
                    dto.setPrice(publication.getPrice());
                    dto.setOwnerUsername(publication.getOwner().getUsername());

                    if (publication.getOwner().getId() != client.getId()) {
                        dto.setStatus(PublicationStatus.BOUGHT);
                    } else {
                        dto.setStatus(publication.getStatus());
                    }

                    if (publication instanceof Book) {
                        dto.setPublicationType("Book");
                        dto.setGenre(((Book) publication).getGenre());
                    } else if (publication instanceof ComicBook) {
                        dto.setPublicationType("Comic Book");
                        dto.setIllustrator(((ComicBook) publication).getIllustrator());
                    } else if (publication instanceof Magazine) {
                        dto.setPublicationType("Magazine");
                        dto.setIssueNumber(((Magazine) publication).getIssueNumber());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


    private String getPublicationType(Publication publication) {
        if (publication instanceof Book) {
            return "Book";
        } else if (publication instanceof ComicBook) {
            return "ComicBook";
        } else if (publication instanceof Magazine) {
            return "Magazine";
        } else {
            return "Unknown";
        }
    }

    public List<PublicationDto> getMyBorrowedPublications() {
        String username = userService.getCurrentUsername();
        Client client = clientRepository.findByUsername(username);
        List<Transaction> transactions = transactionRepository.findByRecipientIdAndType(client.getId(), TransactionType.RENT);

        return transactions.stream()
                .map(transaction -> {
                    Publication publication = transaction.getPublication();

                    if (publication.getStatus() != PublicationStatus.RESERVED) {
                        return null;
                    }
                    return new PublicationDto(
                            publication.getId(),
                            publication.getAuthor(),
                            publication.getTitle(),
                            publication.getPrice(),
                            getPublicationType(publication),
                            publication.getOwner().getUsername(),
                            PublicationStatus.BORROWED
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void returnPublication(Integer publicationId) {
        Optional<Publication> publication = publicationRepository.findById(publicationId);
        if (publication.isPresent()) {
            Publication publicationReturned = publication.get();
            TransactionDto transactionDto = new TransactionDto();
            String currentUser = userService.getCurrentUsername();
            Client recipientClient = clientRepository.findByUsername(currentUser);
            publicationReturned.setStatus(PublicationStatus.AVAILABLE);
            transactionDto.setPublicationId(publicationReturned.getId());
            transactionDto.setOwnerId(publicationReturned.getOwner().getId());
            transactionDto.setRecipientId(recipientClient.getId());
            transactionDto.setTransactionType(TransactionType.RETURN);
            transactionService.createTransaction(transactionDto);
            recipientClient.getOwnedPublications().remove(publicationReturned);
        }
    }

    public PublicationDto getPublication(Integer publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new NoSuchElementException("Publication not found with id: " + publicationId));

        return mapToDto(publication);
    }

    private PublicationDto mapToDto(Publication publication) {
        PublicationDto dto = new PublicationDto();
        dto.setId(publication.getId());
        dto.setTitle(publication.getTitle());
        dto.setAuthor(publication.getAuthor());
        dto.setPrice(publication.getPrice());
        dto.setOwnerUsername(publication.getOwner().getUsername());
        dto.setStatus(publication.getStatus());
        dto.setPublicationType(getPublicationType(publication));

        return dto;
    }

    private Publication mapToEntity(PublicationDto dto) {
        Publication publication;
        switch (dto.getPublicationType().toUpperCase()) {
            case "BOOK":
                publication = new Book(dto.getGenre());
                break;
            case "COMIC_BOOK":
                publication = new ComicBook(dto.getIllustrator());
                break;
            case "MAGAZINE":
                publication = new Magazine(dto.getIssueNumber());
                break;
            default:
                throw new IllegalArgumentException("Unsupported publication type: " + dto.getPublicationType());
        }
        publication.setTitle(dto.getTitle());
        publication.setAuthor(dto.getAuthor());
        publication.setPrice(dto.getPrice());
        return publication;

    }
    private void updateEntity(Publication publication, PublicationDto dto) {
        publication.setTitle(dto.getTitle());
        publication.setAuthor(dto.getAuthor());
        publication.setPrice(dto.getPrice());
        if (publication instanceof Book book) {
            book.setGenre(dto.getGenre());
        } else if (publication instanceof ComicBook comicBook) {
            comicBook.setIllustrator(dto.getIllustrator());
        } else if (publication instanceof Magazine magazine) {
            magazine.setIssueNumber(dto.getIssueNumber());
        }
    }
}
