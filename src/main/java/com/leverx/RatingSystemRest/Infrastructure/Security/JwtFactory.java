package com.leverx.RatingSystemRest.Infrastructure.Security;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling JSON Web Token (JWT) operations such as
 * creation, parsing, validation, and claim extraction.
 */
@Service
@SuppressWarnings("checkstyle:Indentation")
public class JwtFactory {

  @Value("${jwt.signinkey}")
  private String secretKey;

  /**
   * Extracts the username (subject) from the JWT token.
   *
   * @param token the JWT token
   * @return the username (email) embedded in the token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts a specific claim from the token using a resolver function.
   *
   * @param token          the JWT token
   * @param claimsResolver function to extract the specific claim
   * @param <T>            type of the claim
   * @return extracted claim
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Generates a JWT token using the default expiration and no additional claims.
   *
   * @param userDetails the user for whom the token is generated
   * @return the generated token
   */
  public String generateToken(User userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * Generates a JWT token with additional claims.
   *
   * @param extraClaims additional claims to be added to the token
   * @param userDetails user details for whom the token is generated
   * @return the generated JWT token
   */
  public String generateToken(
      Map<String, Object> extraClaims,
      User userDetails
  ) {
    long jwtExpiration = 48000000; // 13.3 hours
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  /**
   * Generates a refresh token with a longer expiration period.
   *
   * @param userDetails the user for whom the refresh token is generated
   * @return the refresh token
   */
  public String generateRefreshToken(
      User userDetails
  ) {
    long refreshExpiration = 48000000; // 13.3 hours
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  /**
   * Builds a JWT token with given claims, user information, and expiration.
   *
   * @param extraClaims additional claims to include
   * @param userDetails the user for whom the token is built
   * @param expiration  expiration time in milliseconds
   * @return the signed JWT token
   */
  private String buildToken(
      Map<String, Object> extraClaims,
      User userDetails,
      long expiration
  ) {
    return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getEmail())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Validates the token by checking username match and expiration.
   *
   * @param token       the JWT token
   * @param userDetails the user details to compare
   * @return true if token is valid, false otherwise
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  /**
   * Checks if the token is expired.
   *
   * @param token the JWT token
   * @return true if expired, false otherwise
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Extracts the expiration date from the token.
   *
   * @param token the JWT token
   * @return expiration date
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extracts all claims from the JWT token.
   *
   * @param token the JWT token
   * @return all claims contained in the token
   */
  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Decodes the secret key and returns it as a {@link java.security.Key}.
   *
   * @return the signing key used to verify JWT signatures
   */
  private Key getSignInKey() {
    String key = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    byte[] keyBytes = Decoders.BASE64.decode(key);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
