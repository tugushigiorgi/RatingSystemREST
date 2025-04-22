package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * Entity representing an authentication or session token associated with a user.
 * Stores metadata like creation time, expiration time, and the token string.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Token {

  /**
   * Unique identifier of the token entity.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /**
   * Timestamp indicating when the token was created.
   * This field is not updatable.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp indicating when the token will expire.
   */
  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  /**
   * Unique token string used for session or authentication.
   */
  @Column(unique = true)
  private String token;

  /**
   * The user associated with this token.
   * Ignored during JSON serialization to prevent circular reference issues.
   */
  @JsonIgnore
  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}
