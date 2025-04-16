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
 * Entity representing a password recovery token associated with a user.
 * This token allows temporary access for password reset and has a strict expiration time.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordRecoveryToken {

  /**
   * Unique identifier for the recovery token.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /**
   * Unique token value used for password recovery.
   */
  @Column(nullable = false, unique = true, updatable = false)
  private String token;

  /**
   * Timestamp of when the token was created.
   */
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the token will expire.
   */
  @Column(nullable = false)
  private LocalDateTime expiresAt;

  /**
   * The user to whom this token belongs.
   * Ignored during JSON serialization to avoid circular references.
   */
  @JsonIgnore
  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}
