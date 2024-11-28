package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.LoginRequestDto;
import org.bookexchange.model.User;
import org.bookexchange.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor

public class LoginService {
    private final UserRepository userRepository;

    public boolean authenticate(LoginRequestDto loginRequest) {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        return user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword());
    }
}
