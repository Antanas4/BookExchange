package org.bookexchange.controller;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.LoginRequestDto;
import org.bookexchange.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor

public class LoginController {
    private final LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        boolean success = loginService.authenticate(loginRequestDto);
        return success ? ResponseEntity.ok("Login successful") : ResponseEntity.status(401).body("Invalid credentials");
    }
}
