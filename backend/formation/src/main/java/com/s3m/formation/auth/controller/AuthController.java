package com.s3m.formation.auth.controller;

import com.s3m.formation.auth.dto.LoginRequest;
import com.s3m.formation.auth.dto.LoginResponse;
import com.s3m.formation.auth.model.User;
import com.s3m.formation.auth.service.AuthService;
import com.s3m.formation.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {

        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", response.token())
                .httpOnly(true)   // ✅ frontend cannot access
                .secure(false)    // ⚠️ true in production HTTPS
                .path("/")
                .maxAge(24 * 60 * 60) // 1 day
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response); // we can still return user info
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // true in prod
                .path("/")
                .maxAge(0)     // immediately expire
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        String email = (String) auth.getPrincipal();
        Integer entrepriseId = (Integer) auth.getDetails();
        String role = auth.getAuthorities().stream()
                .map(Object::toString)
                .findFirst()
                .orElse("USER");


        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(new LoginResponse(
                null, // token is no longer returned
                user.getEmail(),
                role,
                user.getPrenom(),
                user.getNom(),
                entrepriseId
        ));
    }


}
