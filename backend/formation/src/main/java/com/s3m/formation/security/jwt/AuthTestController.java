package com.s3m.formation.security.jwt;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthTestController {

    private final JwtUtils jwtUtils;

    public AuthTestController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Map<String, String> login() {

        // TEMPORARY HARDCODED USER
        String token = jwtUtils.generateToken(
                "test@s3m.com",
                1,
                "ADMIN"
        );

        return Map.of("token", token);
    }
}
