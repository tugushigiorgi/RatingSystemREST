package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
}
