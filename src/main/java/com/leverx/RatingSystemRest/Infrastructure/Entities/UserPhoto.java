package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user's profile photo.
 * Stores metadata such as file name, extension, size, and associated user.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPhoto {

  /**
   * Unique identifier for the user photo.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /**
   * Original name of the uploaded photo file.
   */
  @Column(nullable = false)
  private String photoName;

  /**
   * URL where the photo is stored.
   */
  @Column(nullable = false)
  private String url;

  /**
   * File extension of the uploaded photo (e.g., .jpg, .png).
   */
  @Column(nullable = false)
  private String extension;

  /**
   * Size of the uploaded photo in megabytes.
   */
  @Column(nullable = false)
  private double size;

  /**
   * The user associated with this photo.
   * Ignored during JSON serialization to avoid circular references.
   */
  @OneToOne
  @JsonIgnore
  @JoinColumn(name = "user_id")
  private User user;
}
