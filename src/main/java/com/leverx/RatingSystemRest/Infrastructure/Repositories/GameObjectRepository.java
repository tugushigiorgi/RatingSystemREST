package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for performing CRUD operations on GameObject entities.
 * Provides custom queries to filter and retrieve GameObject data based on seller and rating.
 */
@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Integer> {

  /**
   * Retrieves all game objects for a specific seller.
   *
   * @param sellerId the ID of the seller.
   * @return a list of game objects belonging to the specified seller.
   */
  @Query("SELECT c FROM GameObject c WHERE c.user.id = :sellerId")
  List<GameObject> getGameObjectsBySellerId(int sellerId);

  /**
   * Filters game objects by title and seller's rating.
   * Returns game objects where the title contains the specified keyword and the seller's rating is above or equal to the given threshold.
   *
   * @param title        the keyword to filter game objects by title.
   * @param sellerRating the minimum rating of the seller.
   * @return a list of game objects that match the title and seller rating criteria.
   */
  @Query("SELECT c FROM GameObject c WHERE c.title LIKE %:title% AND c.user.totalRating >= :sellerRating")
  List<GameObject> filterByTitleAndRating(@Param("title") String title, @Param("sellerRating") int sellerRating);

  /**
   * Filters game objects by the seller's rating.
   * Returns game objects where the seller's rating is above or equal to the specified threshold.
   *
   * @param sellerRating the minimum rating of the seller.
   * @return a list of game objects where the seller's rating is greater than or equal to the specified rating.
   */
  @Query("SELECT c FROM GameObject c WHERE c.user.totalRating >= :sellerRating")
  List<GameObject> filterBySellerRating(@Param("sellerRating") int sellerRating);
}
