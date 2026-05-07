package com.s3m.formation.auth.service;

import com.s3m.formation.auth.dto.LoginRequest;
import com.s3m.formation.auth.dto.LoginResponse;
import com.s3m.formation.auth.model.User;
import com.s3m.formation.auth.repository.UserRepository;
import com.s3m.formation.security.jwt.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // ✅ Normalize email
        String email = request.email().trim().toLowerCase();

        // ✅ Lookup user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
                });

        // ✅ Check password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }


        // ✅ Generate JWT
        String token = jwtUtils.generateToken(user);

        // ✅ Return LoginResponse
        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole(),
                user.getPrenom(),
                user.getNom(),
                user.getEntreprise() != null ? user.getEntreprise().getIdEntreprise() : null
        );
    }
}
