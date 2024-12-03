package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.UserDto;
import org.bookexchange.model.Admin;
import org.bookexchange.model.Client;
import org.bookexchange.model.User;
import org.bookexchange.model.enums.UserRole;
import org.bookexchange.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(UserDto userDto) {
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with username " + userDto.getUsername() + " already exists.");
        }
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user;
        if (userDto.getUserRole() == UserRole.ROLE_CLIENT) {
            user = new Client(
                    userDto.getName(),
                    userDto.getSurname(),
                    userDto.getUsername(),
                    encodedPassword,
                    userDto.getAddress(),
                    userDto.getDateOfBirth(),
                    userDto.getUserRole()
            );
        } else {
            user = new Admin(
                    userDto.getName(),
                    userDto.getSurname(),
                    userDto.getUsername(),
                    encodedPassword,
                    userDto.getAdminLevel(),
                    userDto.getUserRole()
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
            userDto.setUserRole(client.getRole());
        } else if (user instanceof Admin admin) {
            userDto.setAdminLevel(admin.getAdminLevel());
            userDto.setUserRole(admin.getRole());
        }
        return userDto;
    }

}
