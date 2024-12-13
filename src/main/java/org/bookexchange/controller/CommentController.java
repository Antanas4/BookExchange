package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.CommentDto;
import org.bookexchange.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/comment")

public class CommentController {
    private final CommentService commentService;

    @PostMapping("/addComment")
    public ResponseEntity<String> addComment (@RequestBody CommentDto commentDto){
        try{
            commentService.addComment(commentDto);
            return ResponseEntity.status(HttpStatus.OK).body("Comment added");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getPublicationComments/{publicationId}")
    public ResponseEntity<?> getComments(@PathVariable String publicationId){
        try{
            List<CommentDto> comments = commentService.getComments(publicationId);
            return ResponseEntity.ok(comments);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
