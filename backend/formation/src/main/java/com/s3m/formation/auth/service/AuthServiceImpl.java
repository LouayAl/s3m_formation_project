package com.s3m.formation.auth.service;

import com.s3m.formation.auth.dto.LoginRequest;
import com.s3m.formation.auth.dto.LoginResponse;
import com.s3m.formation.auth.model.User;
import com.s3m.formation.auth.repository.UserRepository;
import com.s3m.formation.security.jwt.JwtUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtils.generateToken(user);

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
