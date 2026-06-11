package com.s3m.formation.security;

import com.s3m.formation.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

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


        String token = null;

        // ✅ Check cookies
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            log.warn("⚠️ No cookies received from browser!");
        } else {

            for (Cookie cookie : cookies) {

                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        // ✅ No JWT cookie
        if (token == null) {
            log.warn("⚠️ No JWT token found in cookies → user not authenticated");
            filterChain.doFilter(request, response);
            return;
        }
        else {
            Claims claims = jwtUtils.parseClaims(token);
        }


        try {
            Claims claims = jwtUtils.parseClaims(token);

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            Integer entrepriseId = claims.containsKey("entrepriseId")
                    ? claims.get("entrepriseId", Integer.class)
                    : null;


            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                    );

            authentication.setDetails(entrepriseId);

            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (JwtException ex) {

            log.error("❌ JWT invalid: {}", ex.getMessage());

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid JWT"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.equals("/api/auth/login")
                || path.equals("/api/auth/logout")
                || path.startsWith("/api/public/");
    }
}
