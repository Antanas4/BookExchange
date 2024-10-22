package org.bookexchange.service;

import lombok.AllArgsConstructor;
import org.bookexchange.dto.UserDto;
import org.bookexchange.model.Admin;
import org.bookexchange.model.Client;
import org.bookexchange.model.User;
import org.bookexchange.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public void createUser(UserDto userDto) {
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with username " + userDto.getUsername() + " already exists.");
        }

        User user;
        if (userDto.getUserType().equals("Client")) {
            user = new Client(
                    userDto.getName(),
                    userDto.getSurname(),
                    userDto.getUsername(),
                    userDto.getPassword(),
                    userDto.getAddress(),
                    userDto.getDateOfBirth()
            );
        } else {
            user = new Admin(
                    userDto.getName(),
                    userDto.getSurname(),
                    userDto.getUsername(),
                    userDto.getPassword(),
                    userDto.getAdminLevel()
            );
        }
        userRepository.save(user);
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new NoSuchElementException("No users found.");
        }
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
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
            userDtos.add(userDto);
        }
        return userDtos;
    }

    public Optional<UserDto> findUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
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
            return Optional.of(userDto);
        }
        return Optional.empty();
    }


    public void updateUser(String username, UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDto.getName());
            user.setSurname(userDto.getSurname());
            user.setUsername(userDto.getUsername());
            user.setPassword(userDto.getPassword());

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

}
