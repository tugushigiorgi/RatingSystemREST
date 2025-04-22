package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.PasswordRecoveryToken;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
 * Repository interface for performing CRUD operations on PasswordRecoveryToken entities.
 * Provides custom queries to retrieve and delete password recovery tokens.
 */
public interface PasswordRecTokenRepository extends JpaRepository<PasswordRecoveryToken, Integer> {

  /**
   * Retrieves a PasswordRecoveryToken by its token.
   *
   * @param token the token string used to find the password recovery token.
   * @return an Optional containing the PasswordRecoveryToken if found, or empty if not found.
   */
  @Query("SELECT c FROM PasswordRecoveryToken c WHERE c.token = :token")
  Optional<PasswordRecoveryToken> findByToken(String token);

  /**
   * Deletes a PasswordRecoveryToken by its ID.
   * This operation is transactional and modifies the database.
   *
   * @param id the ID of the PasswordRecoveryToken to be deleted.
   */
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM PasswordRecoveryToken c WHERE c.id = :id")
  void deleteById(int id);


}
