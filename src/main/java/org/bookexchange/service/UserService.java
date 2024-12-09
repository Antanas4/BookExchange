package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.config.SecurityConfig;
import org.bookexchange.dto.UserDto;
import org.bookexchange.model.Admin;
import org.bookexchange.model.Client;
import org.bookexchange.model.User;
import org.bookexchange.model.enums.UserRole;
import org.bookexchange.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.*;

@Service
@AllArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final SecurityConfig securityConfig;

    public void createUser(UserDto userDto) {
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        logger.error(userDto.getUserType());
        if (existingUser.isPresent()) {
            logger.error(String.valueOf(existingUser.get().getUsername()));
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with username " + userDto.getUsername() + " already exists.");
        }
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user;
        if (Objects.equals(userDto.getUserType(), "Client")) {
            user = new Client(
                    userDto.getName(),
                    userDto.getSurname(),
                    userDto.getUsername(),
                    encodedPassword,
                    userDto.getAddress(),
                    userDto.getDateOfBirth(),
                    UserRole.ROLE_CLIENT
            );
        } else {
            user = new Admin(
                    userDto.getName(),
                    userDto.getSurname(),
                    userDto.getUsername(),
                    encodedPassword,
                    userDto.getAdminLevel(),
                    UserRole.ROLE_ADMIN
            );
        }
        userRepository.save(user);
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = createUserDto(user);
            userDtos.add(userDto);
        }
        return userDtos;
    }

    public Optional<UserDto> findUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserDto userDto = createUserDto(user);
            return Optional.of(userDto);
        }
        return Optional.empty();
    }


    public void updateUser(String username, UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            User user = optionalUser.get();
            user.setName(userDto.getName());
            user.setSurname(userDto.getSurname());
            user.setUsername(userDto.getUsername());
            user.setPassword(encodedPassword);

            if (user instanceof Client client) {
                client.setAddress(userDto.getAddress());
                client.setDateOfBirth(userDto.getDateOfBirth());
            } else if (user instanceof Admin admin) {
                admin.setAdminLevel(userDto.getAdminLevel());
            }
            userRepository.save(user);
        } else {
            throw new NoSuchElementException("User with username " + username + " does not exist.");
        }
    }


    public void deleteUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
        } else {
            throw new NoSuchElementException("User with username " + username + " does not exist.");
        }
    }

    private UserDto createUserDto(User user) {
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

    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}
