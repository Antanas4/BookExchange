package org.bookexchange.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.bookexchange.dto.LoginRequestDto;
import org.bookexchange.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpSession session) {
        loginService.loginUser(loginRequestDto, session);
        return ResponseEntity.ok(loginRequestDto.getUsername());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        loginService.logoutUser(authentication, request, response);
        return ResponseEntity.ok("Logout successful");
    }
}
