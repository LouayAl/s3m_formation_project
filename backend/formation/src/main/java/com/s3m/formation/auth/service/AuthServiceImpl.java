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
        System.out.println("🔍 Attempting login for email: [" + email + "]");

        // ✅ Lookup user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ User not found: [" + email + "]");
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
                });

        // ✅ Check password
        System.out.println("🔑 Checking password for user: [" + email + "]");
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            System.out.println("❌ Invalid password for user: [" + email + "]");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        System.out.println("✅ Login successful for user: [" + email + "]");

        // ✅ Generate JWT
        String token = jwtUtils.generateToken(user);
        System.out.println("📝 JWT generated for user: [" + email + "]");

        // ✅ Return LoginResponse
        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole(),
                user.getPrenom(),
                user.getNom(),
                user.getEntreprise().getIdEntreprise()
        );
    }
}
