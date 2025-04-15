package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.PasswordRecoveryToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface passwordRecoveryTokenRepository extends JpaRepository<PasswordRecoveryToken, Integer> {

    @Query("SELECT c FROM PasswordRecoveryToken c WHERE  c.token= :token ")
    Optional<PasswordRecoveryToken> findByToken(String token);


    @Modifying
    @Transactional
    @Query(value = "Delete From PasswordRecoveryToken c  where  c.id=:id" )
    void DeleteByID(int id);

}
