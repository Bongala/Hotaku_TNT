package net.hotaku.service;

import lombok.RequiredArgsConstructor;
import net.hotaku.dto.auth.AuthResponse;
import net.hotaku.dto.auth.SigninRequest;
import net.hotaku.dto.auth.SignupRequest;
import net.hotaku.entity.User;
import net.hotaku.repository.UserRepository;
import net.hotaku.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user and returns an authentication response with a JWT token.
     *
     * Throws a RuntimeException if the username or email is already in use.
     *
     * @param request the signup request containing username, email, and password
     * @return an AuthResponse with the generated JWT token, username, and email
     */
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String jwt = jwtUtils.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    /**
     * Authenticates a user with the provided credentials and returns an authentication response containing a JWT token.
     *
     * @param request the sign-in request containing username and password
     * @return an AuthResponse with the generated JWT token, username, and email of the authenticated user
     */
    public AuthResponse signin(SigninRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    /**
     * Logs out the current user by clearing the security context.
     *
     * Removes authentication information from the security context, effectively ending the user's session.
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }
} 