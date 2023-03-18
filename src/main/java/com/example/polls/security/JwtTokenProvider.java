package com.example.polls.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Logger;

@Component
public class JwtTokenProvider {
     private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtTokenProvider.class);

//    @Value("{app.jwtSecret}")
    private String jwtSecret = "eyJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTY3NzM5NDU1OCwiaWF0IjoxNjc3Mzk0NTU4fQ.5e6U-4YDDpWIJ__Qq9akC9-MdpM2A1LHAatw0SGg0ObXyPe5hjBdIaFUN6Vqat8z9avHHqUO5TcfzsNdjX9wbg";

//    @Value("{app.jwtExpirationInMs}")
    private int jwtExpirationInMs = 86400000;

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










