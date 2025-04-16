package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on UserPhoto entities.
 * Provides custom queries to retrieve user photos based on the user's ID.
 */
@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Integer> {

  /**
   * Retrieves the UserPhoto entity associated with a specific user by their user ID.
   *
   * @param userId the ID of the user whose photo is to be retrieved.
   * @return the UserPhoto entity associated with the specified user ID.
   */
  @Query("SELECT c FROM UserPhoto c WHERE c.user.id = :userId")
  UserPhoto getUserPhotoByUserId(int userId);
}
