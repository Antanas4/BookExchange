package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.UserDto;
import org.bookexchange.model.Admin;
import org.bookexchange.model.Client;
import org.bookexchange.model.User;
import org.bookexchange.model.enums.UserRole;
import org.bookexchange.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(UserDto userDto) {
        userRepository.findByUsername(userDto.getUsername())
                .ifPresent(user -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists.");
                });

        User user = mapToUser(userDto);
        userRepository.save(user);
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToUserDto)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));
    }

    public void updateUser(String username, UserDto userDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        updateUserData(user, userDto);
        userRepository.save(user);
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        userRepository.delete(user);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    public List<String> getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("User is not authenticated");
        }
        return auth.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setSurname(user.getSurname());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());

        if (user instanceof Client client) {
            userDto.setAddress(client.getAddress());
            userDto.setDateOfBirth(client.getDateOfBirth());
            userDto.setUserType("Client");
        } else if (user instanceof Admin admin) {
            userDto.setAdminLevel(admin.getAdminLevel());
            userDto.setUserType("Admin");
        }
        return userDto;
    }

    private User mapToUser(UserDto userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        if ("Client".equalsIgnoreCase(userDto.getUserType())) {
            return new Client(userDto.getName(), userDto.getSurname(), userDto.getUsername(),
                    encodedPassword, userDto.getAddress(), userDto.getDateOfBirth(), UserRole.ROLE_CLIENT);
        } else {
            return new Admin(userDto.getName(), userDto.getSurname(), userDto.getUsername(),
                    encodedPassword, userDto.getAdminLevel(), UserRole.ROLE_ADMIN);
        }
    }

    private void updateUserData(User user, UserDto userDto) {
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (user instanceof Client client) {
            client.setAddress(userDto.getAddress());
            client.setDateOfBirth(userDto.getDateOfBirth());
        } else if (user instanceof Admin admin) {
            admin.setAdminLevel(userDto.getAdminLevel());
        }
    }
}
