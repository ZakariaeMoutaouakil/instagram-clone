package com.instagram.demo.security.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
    public static final String JWT_KEY = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final String JWT_COOKIE_NAME = "jwt_token";

    private final Logger LOG = Logger.getLogger(AuthoritiesLoggingAtFilter.class.getName());


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            LOG.info("authentication: " + authentication.getName());
//            LOG.info("authentication: " + authentication.getCredentials().toString());
            LOG.info("authentication: " + authentication.getAuthorities().toString());
            SecretKey key = Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));
            String jwt = Jwts.builder()
                    .issuer("Eazy Bank")
                    .subject("JWT Token")
                    .claim("username", authentication.getName())
                    .issuedAt(new Date())
                    .expiration(new Date((new Date()).getTime() + 30000000))
                    .signWith(key)
                    .compact();

            Cookie jwtCookie = new Cookie(JWT_COOKIE_NAME, jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true); // Make it secure (HTTPS only)
            jwtCookie.setMaxAge(3600000); // Set cookie expiration time in seconds
            jwtCookie.setPath("/"); // Set cookie path
            response.setHeader("Set-Cookie", jwtCookie.getName() + "=" + jwtCookie.getValue() + "; Secure; HttpOnly; Max-Age=3600000; Path=/; SameSite=None");

            response.addCookie(jwtCookie);
            LOG.info("jwt= " + jwt);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/login");
    }
}
