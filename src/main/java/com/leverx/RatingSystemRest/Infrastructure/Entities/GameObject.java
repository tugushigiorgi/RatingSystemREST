package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Entity representing a GameObject listed by a seller.
 * Includes title, description, price, timestamps, and relationships to user and picture.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameObject {

  /**
   * Unique identifier for the GameObject.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /**
   * The title of the GameObject.
   */
  @Column(nullable = false, length = 100)
  private String title;

  /**
   * The description text for the GameObject.
   */
  @Column(nullable = false, length = 1000)
  private String text;

  /**
   * The price of the GameObject.
   */
  @Column(nullable = false)
  private double price;

  /**
   * Timestamp indicating when the GameObject was created.
   * This field is not updatable after insertion.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp indicating the last time the GameObject was updated.
   * This field is set automatically and not insertable by the user.
   */
  @Column(name = "updated_at", insertable = false)
  private LocalDateTime updatedAt;

  /**
   * The user (seller) who owns the GameObject.
   * Ignored during JSON serialization.
   */
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  /**
   * The associated picture for this GameObject.
   * This is a one-to-one relationship with cascading and orphan removal.
   */
  @OneToOne(mappedBy = "gameObject", cascade = CascadeType.ALL, orphanRemoval = true)
  private GameObjectPicture picture;
}
