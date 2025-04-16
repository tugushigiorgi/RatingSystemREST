package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a comment left by an anonymous user.
 * Includes message, rating, approval status, and relation to the user.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Comment {

  /**
   * Unique identifier of the comment.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /**
   * The text message of the comment.
   */
  @Column(nullable = false)
  private String message;

  /**
   * Identifier of the anonymous user who submitted the comment.
   */
  @Column(nullable = false)
  private Integer anonymousId;

  /**
   * The timestamp when the comment was created.
   * This field is not updatable.
   */
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Indicates whether the comment has been approved.
   */
  private boolean approved = false;

  /**
   * Rating value associated with the comment.
   */
  @Column(nullable = false)
  private int rating;

  /**
   * The user (seller) to whom the comment belongs.
   * This relationship is ignored during JSON serialization.
   */
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
