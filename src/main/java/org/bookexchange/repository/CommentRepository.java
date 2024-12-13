package org.bookexchange.repository;

import org.bookexchange.model.Comment;
import org.bookexchange.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPublication(Publication publication);
}
