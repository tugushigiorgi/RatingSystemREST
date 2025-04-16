package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Comment entities.
 * It provides custom queries for retrieving approved and unapproved comments for a specific seller.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

  /**
   * Retrieves all approved comments for a specific seller.
   *
   * @param sellerId the ID of the seller.
   * @return a list of approved comments.
   */
  @Query("SELECT c FROM Comment c WHERE c.user.id = :sellerId AND c.approved = true")
  List<Comment> sellersAllApprovedReviews(int sellerId);

  /**
   * Retrieves all unapproved comments for a specific seller.
   *
   * @param sellerId the ID of the seller.
   * @return a list of unapproved comments.
   */
  @Query("SELECT c FROM Comment c WHERE c.user.id = :sellerId AND c.approved = false")
  List<Comment> sellersNotApprovedReviews(int sellerId);

  /**
   * Retrieves all comments that are not approved.
   *
   * @return a list of unapproved comments.
   */
  @Query("SELECT c FROM Comment c WHERE c.approved = false")
  List<Comment> allNotApprovedReviews();
}
