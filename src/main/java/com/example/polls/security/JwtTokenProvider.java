package com.example.polls.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Logger;

@Component

public class JwtTokenProvider {
     private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("{app.jwtSecret}")
    private String jwtSecret;

    @Value("{app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now=new Date();
        Date expiryDate=new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(authToken);
        }catch (SignatureException ex) {
            logger.info("Invalid JWT signature");
        }catch (MalformedJwtException ex) {
            logger.info("Invalid JWT token");
        }catch (ExpiredJwtException ex) {
            logger.info("Expired JWT token");
        }catch (UnsupportedJwtException ex) {
            logger.info("Unsupported JWT token");
        }catch (IllegalArgumentException ex){
            logger.info("JWT claims string is empty");
        }

        return false;
    }
}










