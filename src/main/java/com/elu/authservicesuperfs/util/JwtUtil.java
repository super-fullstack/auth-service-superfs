package com.elu.authservicesuperfs.util;

import com.elu.authservicesuperfs.model.Users;
import com.elu.authservicesuperfs.repo.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final UserRepo userRepo;
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    public JwtUtil(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Users existingUsersEmail = userRepo.findByEmail(userDetails.getUsername())
                .orElse(null);
        if (existingUsersEmail != null) {
            claims.put("id", existingUsersEmail.getID());
            claims.put("username", existingUsersEmail.getUsername());
            claims.put("email", existingUsersEmail.getEmail());
            claims.put("role", existingUsersEmail.getRole());
        }
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()
                        + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public <T> T extractClaimByToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractClaimByToken(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaimByToken(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}

