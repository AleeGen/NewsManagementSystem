package ru.clevertec.users.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.clevertec.users.service.JwtService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of the {@link JwtService} interface for working with JWT tokens.
 * Uses the io library.jsonwebtoken for generating and verifying tokens.
 * Contains the SECRET_KEY and validPeriod fields, which define the secret key and the token lifetime, respectively.
 */
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${spring.security.token.secret}")
    private String SECRET_KEY;

    @Value("${spring.security.token.valid-period}")
    private long validPeriod; //milliseconds

    /**
     * Method for extracting the username from the JWT token.
     *
     * @param token JWT token.
     * @return the username specified in the token.
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Method for extracting data from a token by a given key.
     *
     * @param token          JWT token.
     * @param claimsResolver function for extracting data from Claims.
     * @param <T>            type of extracted data.
     * @return data extracted from the token by the specified key.
     */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Method for generating a JWT token based on user data and additional data.
     *
     * @param userDetails user data, based on which the token will be generated.
     * @return JWT token.
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validPeriod))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Method for checking the validity of the JWT token.
     *
     * @param token       JWT token.
     * @param userDetails user data with which the token will be compared.
     * @return true if the token is valid and corresponds to the user's data, false otherwise.
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token);
        } catch (MalformedJwtException | ExpiredJwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}