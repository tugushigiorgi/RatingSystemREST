package com.leverx.RatingSystemRest.Infrastructure.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * Entity class representing a User.
 * Implements {@link UserDetails} for Spring Security authentication and authorization.
 * Contains information about the user such as name, email, password, role, associated game objects,
 * comments, ratings, and related tokens.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements UserDetails {

  /**
   * The unique identifier for the user.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /**
   * The first name of the user.
   * Cannot be null.
   */
  @Column(nullable = false)
  private String first_name;

  /**
   * The last name of the user.
   * Cannot be null.
   */
  @Column(nullable = false)
  private String last_name;

  /**
   * The email address of the user.
   * Must be unique and cannot be null.
   */
  @Column(unique = true, nullable = false)
  private String email;

  /**
   * The password of the user.
   * Cannot be null.
   */
  @Column(nullable = false)
  private String password;

  /**
   * The date and time when the user was created.
   * Cannot be null or updatable.
   */
  @Column(nullable = false, updatable = false)
  private LocalDateTime created_at;

  /**
   * Indicates whether the user has verified their email.
   * Default is false.
   */
  private boolean hasVerifiedEmail;

  /**
   * The role of the user (e.g., ADMIN, SELLER).
   * Cannot be null.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserRoleEnum role;

  /**
   * The list of game objects owned by the user.
   * Cascade all operations.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<GameObject> gameObjects = new ArrayList<>();

  /**
   * The list of comments made by the user.
   * Cascade all operations.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Comment> comments = new ArrayList<>();

  /**
   * The total rating of the user based on their game objects or reviews.
   */
  private double totalRating;

  /**
   * The user's profile photo.
   * One-to-one relationship with the {@link UserPhoto} entity.
   */
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private UserPhoto photo;

  /**
   * The token associated with the user for authentication.
   * One-to-one relationship with the {@link Token} entity.
   */
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Token token;

  /**
   * The password recovery token for the user.
   * One-to-one relationship with the {@link PasswordRecoveryToken} entity.
   */
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private PasswordRecoveryToken passwordRecoveryToken;

  /**
   * Indicates whether the user has been approved by the admin.
   */
  public boolean isApprovedByAdmin;

  /**
   * Returns the full name of the user by combining first and last name.
   *
   * @return the full name of the user.
   */
  public String fullName() {
    return first_name + " " + last_name;
  }

  /**
   * Retrieves the authorities granted to the user.
   * This method is used by Spring Security to determine the roles and permissions of the user.
   *
   * @return a collection of granted authorities.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  /**
   * Retrieves the password of the user.
   *
   * @return the user's password.
   */
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * Retrieves the username of the user, which is the user's email.
   *
   * @return the user's email as the username.
   */
  @Override
  public String getUsername() {
    return email;
  }

  /**
   * Indicates whether the user is enabled (i.e., whether the user has verified their email).
   *
   * @return true if the user has verified their email, false otherwise.
   */
  @Override
  public boolean isEnabled() {
    return hasVerifiedEmail;
  }
}
