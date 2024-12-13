package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.CommentDto;
import org.bookexchange.model.Client;
import org.bookexchange.model.Comment;
import org.bookexchange.model.Publication;
import org.bookexchange.repository.ClientRepository;
import org.bookexchange.repository.CommentRepository;
import org.bookexchange.repository.PublicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CommentService {
    private final UserService userService;
    private final PublicationRepository publicationRepository;
    private final ClientRepository clientRepository;
    private final CommentRepository commentRepository;

    public void addComment(CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        String authorUsername = userService.getCurrentUsername();
        Client author = clientRepository.findByUsername(authorUsername);
        Publication publication = publicationRepository.findById(commentDto.getPublicationId())
                .orElseThrow(() -> new IllegalArgumentException("Publication not found"));

        Comment comment = new Comment();
        comment.setTitle(commentDto.getTitle());
        comment.setBody(commentDto.getBody());
        comment.setTimestamp(now);
        comment.setAuthor(author);
        comment.setPublication(publication);

        if (commentDto.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(commentDto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }
        commentRepository.save(comment);
    }

    public List<CommentDto> getComments(String publicationId) {
        Publication publication = publicationRepository.findById(Integer.valueOf(publicationId))
                .orElseThrow(() -> new IllegalArgumentException("Publication not found with ID: " + publicationId));

        List<Comment> comments = commentRepository.findByPublication(publication);

        return comments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CommentDto mapToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getTitle(),
                comment.getBody(),
                comment.getTimestamp(),
                comment.getReplies() != null
                        ? comment.getReplies().stream().map(this::mapToDto).collect(Collectors.toList())
                        : null,
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getAuthor().getUsername(),
                comment.getPublication().getId()
        );

    }
}
