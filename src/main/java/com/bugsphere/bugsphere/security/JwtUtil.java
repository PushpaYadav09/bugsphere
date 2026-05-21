package com.bugsphere.bugsphere.security;

import io.jsonwebtoken.Claims;           // Claims = the data stored inside a JWT token
import io.jsonwebtoken.Jwts;             // main class for building and parsing JWT tokens
import io.jsonwebtoken.SignatureAlgorithm; // the algorithm used to sign tokens (we use HS256)
import io.jsonwebtoken.security.Keys;    // helper to create a secure signing key from our secret string
import org.springframework.beans.factory.annotation.Value; // reads values from application.properties
import org.springframework.security.core.userdetails.UserDetails; // Spring's user object
import org.springframework.stereotype.Component; // marks this as a Spring-managed bean

import java.security.Key;       // Java's generic Key interface
import java.util.Date;          // used for token expiry timestamps
import java.util.HashMap;       // for adding extra data into the token
import java.util.Map;
import java.util.function.Function; // for the generic claim extractor

@Component // Spring will create one instance of this class and share it everywhere it's needed
public class JwtUtil {

    // Reads jwt.secret from application.properties and injects it here
    @Value("${jwt.secret}")
    private String secret;

    // Reads jwt.expiration from application.properties (86400000 = 24 hours in ms)
    @Value("${jwt.expiration}")
    private long expiration;

    // ── Key helper ─────────────────────────────────────────────────────────────

    // Converts our plain-text secret string into a cryptographic Key object.
    // HMAC-SHA requires the key to be at least 256 bits — Keys.hmacShaKeyFor() handles that.
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── Token generation ───────────────────────────────────────────────────────

    // Creates a JWT token for a logged-in user.
    // The token contains: username, issue time, expiry time — all signed with our secret.
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // We could add extra data here, e.g. claims.put("role", userDetails.getAuthorities())
        // For now we keep it simple — just the username as the subject
        return buildToken(claims, userDetails.getUsername());
    }

    // Internal method that actually builds the JWT string.
    // A JWT looks like:  header.payload.signature
    private String buildToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .setClaims(extraClaims)               // any extra data we want in the token
                .setSubject(username)                  // who this token belongs to
                .setIssuedAt(new Date())               // when was this token created
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // when it expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // sign with our secret key
                .compact(); // build it into the final "xxxxx.yyyyy.zzzzz" string
    }

    // ── Token reading ──────────────────────────────────────────────────────────

    // Generic method to extract any piece of data from a token.
    // claimsResolver is a function — you tell it WHAT to extract.
    // Example: extractClaim(token, Claims::getSubject) → gets the username
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parses the token and returns ALL data stored inside it.
    // This also verifies the signature — if someone tampered with the token, this throws an exception.
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // use the same key we used to sign
                .build()
                .parseClaimsJws(token)          // parse and verify
                .getBody();                     // return the payload (the claims)
    }

    // Extracts just the username (stored as the "subject") from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extracts the expiry date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ── Token validation ───────────────────────────────────────────────────────

    // Returns true if the token is valid for this user AND has not expired.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Check 1: token username matches the logged-in user
        // Check 2: token hasn't expired yet
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Returns true if the current time is past the token's expiry time
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}