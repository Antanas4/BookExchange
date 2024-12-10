package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.UserDto;
import org.bookexchange.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@AllArgsConstructor

public class UserController {
    private final UserService userService;

    @PostMapping("/admin/users")
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        try {
            userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userDto.getUserType() + " created successfully: " + userDto.getUsername());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        try {
            List<UserDto> users = userService.getUsers();
            return ResponseEntity.ok(users);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    @GetMapping("/{username}")
    public ResponseEntity<UserDto> findUserByUsername(@PathVariable String username) {
        Optional<UserDto> userDto = userService.findUserByUsername(username);
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/users/{username}")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto, @PathVariable String username) {
        try {
            userService.updateUser(username, userDto);
            return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the user: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            userService.deleteUser(username);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the user: " + e.getMessage());
        }
    }

    @GetMapping("/currentUser")
    public ResponseEntity<String> getCurrentUsername() {
        try {
            String username = userService.getCurrentUsername();
            return ResponseEntity.ok().body(username);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/currentUser/roles")
    public ResponseEntity<List<String>> getCurrentUserRole() {
        try {
            List<String> roles = userService.getCurrentUserRole();
            return ResponseEntity.ok(roles);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}
