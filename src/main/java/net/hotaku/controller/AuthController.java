package net.hotaku.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.hotaku.dto.auth.AuthResponse;
import net.hotaku.dto.auth.SigninRequest;
import net.hotaku.dto.auth.SignupRequest;
import net.hotaku.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Handles user registration requests.
     *
     * Accepts a validated signup request and returns an authentication response upon successful registration.
     *
     * @param request the signup request containing user registration details
     * @return a response entity containing the authentication response
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    /**
     * Handles user sign-in requests and returns authentication details.
     *
     * @param request the sign-in credentials provided in the request body
     * @return a response entity containing authentication information upon successful sign-in
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody SigninRequest request) {
        return ResponseEntity.ok(authService.signin(request));
    }

    /**
     * Logs out the currently authenticated user.
     *
     * Returns an HTTP 200 OK response with no content.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }
} 