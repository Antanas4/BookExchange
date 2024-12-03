package org.bookexchange.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.bookexchange.dto.LoginRequestDto;
import org.bookexchange.model.User;
import org.bookexchange.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void loginUser(LoginRequestDto loginRequestDto, HttpSession session) {
        Optional<User> optUser = userRepository.findByUsername(loginRequestDto.getUsername());
        if (optUser.isPresent() && passwordEncoder.matches(loginRequestDto.getPassword(), optUser.get().getPassword())) {
            User user = optUser.get();
            authenticateUser(user, session);
        }
    }

    public void logoutUser(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
    }

    public void authenticateUser(User user, HttpSession session) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = getAuthentication(user, securityContext.getAuthentication());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }

    private static Authentication getAuthentication(User user, Authentication existingAuth) {
        if (existingAuth != null && existingAuth.isAuthenticated() && !(existingAuth instanceof AnonymousAuthenticationToken)) {
            throw new IllegalArgumentException("User is already authenticated");
        }

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole().name());

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(simpleGrantedAuthority));
    }
}
