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
        Client client = clientRepository.findByUsername(publicationDto.getOwnerUsername());
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
        publication.setStatus(PublicationStatus.AVAILABLE);
        publicationRepository.save(publication);
    }

    public List<PublicationDto> getPublications(){
        List<PublicationDto> publicationDtos= new ArrayList<>();
        List<Publication> publications = publicationRepository.findAll();
        for (Publication publication : publications) {
            PublicationDto publicationDto = createPublicationDto(publication);
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

    public List<PublicationDto> getPublicationShop() {
        String currentUser = userService.getCurrentUsername();
        List<Publication> publications = publicationRepository.findAllExcludingCurrentUser(currentUser);
        List<PublicationDto> publicationDtos = new ArrayList<>();
        for (Publication publication : publications) {
            PublicationDto publicationDto = createPublicationDto(publication);
            publicationDtos.add(publicationDto);
        }
        return publicationDtos;
    }

    private PublicationDto createPublicationDto(Publication publication){
        PublicationDto dto = new PublicationDto();
        dto.setId(publication.getId());
        dto.setTitle(publication.getTitle());
        dto.setAuthor(publication.getAuthor());
        dto.setPrice(publication.getPrice());
        dto.setOwnerUsername(publication.getOwner().getUsername());
        dto.setStatus(publication.getStatus());

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
    }

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
        dto.setPublicationType(getPublicationType(publication));

        return dto;
    }
}
