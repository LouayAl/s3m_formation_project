package com.s3m.formation.security;

import com.s3m.formation.security.jwt.JwtUtils;
import com.s3m.formation.security.util.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("🔐 JWT FILTER CALLED FOR: " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header = " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("JWT token = " + token);

        if (!jwtUtils.validateToken(token)) {
            System.out.println("❌ JWT INVALID");
            filterChain.doFilter(request, response);
            return;
        }

        Integer entrepriseId = jwtUtils.getEntrepriseId(token);
        String role = jwtUtils.getRole(token);
        String email = jwtUtils.getEmail(token);

        System.out.println("✅ JWT VALID");
        System.out.println("email=" + email + ", role=" + role + ", entrepriseId=" + entrepriseId);

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + role);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(authority)
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            SecurityUtils.setEntrepriseId(entrepriseId);
            filterChain.doFilter(request, response);
        } finally {
            SecurityUtils.clear();
        }
    }

}
