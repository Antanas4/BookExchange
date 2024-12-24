package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.UserDto;
import org.bookexchange.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor

public class UserController {
    private final UserService userService;

    @PostMapping("/admin/users")
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDto.getUserType() + " created successfully: " + userDto.getUsername());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> findUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.findUserByUsername(username);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/admin/users/{username}")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto, @PathVariable String username) {
        userService.updateUser(username, userDto);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/admin/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/currentUser")
    public ResponseEntity<String> getCurrentUsername() {
        return ResponseEntity.ok(userService.getCurrentUsername());
    }

    @GetMapping("/currentUser/roles")
    public ResponseEntity<List<String>> getCurrentUserRole() {
        return ResponseEntity.ok(userService.getCurrentUserRole());
    }
}
