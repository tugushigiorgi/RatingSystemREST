package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on User entities.
 * Provides custom queries to retrieve users based on role, approval status, rating, and other criteria.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  /**
   * Retrieves a list of sellers who are not approved by an admin and have verified their email.
   *
   * @return a list of users who are unapproved sellers with verified emails.
   */
  @Query("SELECT u FROM User u WHERE u.role = 'SELLER' and u.isApprovedByAdmin = false and u.HasVerifiedEmail = true")
  List<User> notApprovedSellersList();

  /**
   * Retrieves a list of approved sellers.
   *
   * @return a list of users who are approved sellers.
   */
  @Query("SELECT u FROM User u WHERE u.role = 'SELLER' and u.isApprovedByAdmin = true")
  List<User> approvedSellersList();

  /**
   * Retrieves a list of approved sellers filtered by a username search.
   *
   * @param username the username (or part of it) used to filter sellers.
   * @return a list of approved sellers whose first name matches the search criteria.
   */
  @Query("SELECT u FROM User u WHERE u.role = 'SELLER' and u.isApprovedByAdmin = true and u.first_name like %:username%")
  List<User> getRegisteredSellerByUsername(String username);

  /**
   * Retrieves the top 5 rated sellers.
   *
   * @return a list of the top 5 sellers with the highest ratings, ordered by rating in descending order.
   */
  @Query("""
      SELECT u
      FROM User u 
      WHERE u.role = 'SELLER' 
      AND u.isApprovedByAdmin = true
      AND u.TotalRating > 0 
      ORDER BY u.TotalRating DESC
      LIMIT 5
      """)
  List<User> findTop5RatedSellers();

  /**
   * Retrieves a user by their email.
   *
   * @param email the email address of the user to retrieve.
   * @return an Optional containing the User if found, or empty if no user with that email exists.
   */
  Optional<User> findByEmail(String email);
}
