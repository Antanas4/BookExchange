package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.LoginRequestDto;
import org.bookexchange.model.User;
import org.bookexchange.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor

public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean authenticate(LoginRequestDto loginRequest) {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        return user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword());
    }
}
