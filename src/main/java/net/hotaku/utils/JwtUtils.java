package net.hotaku.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret:defaultSecretKey12345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;

    /**
     * Generates the HMAC SHA signing key from the configured secret.
     *
     * @return the secret key used for signing and verifying JWTs
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token the JWT token from which to extract the username
     * @return the username contained in the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Retrieves the expiration date from a JWT.
     *
     * @param token the JWT from which to extract the expiration date
     * @return the expiration date of the token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from a JWT by applying the provided resolver function to the token's claims.
     *
     * @param token the JWT from which to extract the claim
     * @param claimsResolver a function that processes the token's claims and returns the desired value
     * @return the value extracted from the claims as determined by the resolver function
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT token using the signing key and returns all claims contained in its payload.
     *
     * @param token the JWT string to parse
     * @return the claims extracted from the token's payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Determines whether the JWT token has expired.
     *
     * @param token the JWT token to check
     * @return true if the token's expiration date is before the current date, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT token for the specified user without additional claims.
     *
     * @param userDetails the user for whom the token is generated
     * @return a signed JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /****
     * Generates a JWT token for the specified user, including any additional claims.
     *
     * @param userDetails the user for whom the token is generated
     * @param additionalClaims extra claims to include in the token payload
     * @return a signed JWT token as a string
     */
    public String generateToken(UserDetails userDetails, Map<String, Object> additionalClaims) {
        return createToken(additionalClaims, userDetails.getUsername());
    }

    /**
     * Creates a JWT token with the specified claims and subject, setting the issued and expiration dates, and signing it with the configured key.
     *
     * @param claims  additional claims to include in the token payload
     * @param subject the subject (typically the username) for whom the token is issued
     * @return a signed JWT token as a compact string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates a JWT by checking that the username in the token matches the provided user's username and that the token has not expired.
     *
     * @param token the JWT to validate
     * @param userDetails the user details to compare against the token's subject
     * @return true if the token is valid for the given user and not expired; false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
} 