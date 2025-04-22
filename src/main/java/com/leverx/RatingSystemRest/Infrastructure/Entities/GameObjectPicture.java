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
 * Entity representing the picture associated with a GameObject.
 * Stores metadata such as name, extension, URL, size, and its relationship to a GameObject.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameObjectPicture {

  /**
   * Unique identifier for the picture.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /**
   * The original name of the uploaded photo.
   */
  @Column(nullable = false)
  private String photoName;

  /**
   * The URL where the photo is stored.
   */
  @Column(nullable = false)
  private String url;

  /**
   * The file extension of the photo (e.g., .jpg, .png).
   */
  @Column(nullable = false)
  private String extension;

  /**
   * The size of the photo in megabytes.
   */
  @Column(nullable = false)
  private double size;

  /**
   * The GameObject to which this picture belongs.
   * Ignored during JSON serialization to prevent circular references.
   */
  @JsonIgnore
  @OneToOne
  @JoinColumn(name = "gameobject_id")
  private GameObject gameObject;
}
