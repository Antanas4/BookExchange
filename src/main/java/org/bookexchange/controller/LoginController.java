package org.bookexchange.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.bookexchange.dto.LoginRequestDto;
import org.bookexchange.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@SessionAttributes("username")

public class LoginController {
    private final LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        boolean success = loginService.authenticate(loginRequestDto);
        if (success) {
            String username = loginRequestDto.getUsername();
            return ResponseEntity.ok("User logged in successfully: " + username);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session, SessionStatus status) {
        loginService.logout(session, status);
        return ResponseEntity.ok("Logout successful");
    }
}
