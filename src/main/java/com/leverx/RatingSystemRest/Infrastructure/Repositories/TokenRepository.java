package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Token;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for performing CRUD operations on Token entities.
 * Provides custom queries to retrieve and delete tokens based on their ID or token string.
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

  /**
   * Retrieves a Token entity by its token string.
   *
   * @param token the token string used to find the Token entity.
   * @return an Optional containing the Token entity if found, or empty if not found.
   */
  @Query("SELECT c FROM Token c WHERE c.token = :token")
  Optional<Token> findByToken(String token);

  /**
   * Deletes a Token entity by its ID.
   * This operation is transactional and modifies the database.
   *
   * @param id the ID of the Token entity to be deleted.
   */
  @Modifying
  @Transactional
  @Query("DELETE FROM Token WHERE id = :id")
  void deleteTokenById(@Param("id") int id);
}
