package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


    @Query("SELECT u FROM User  u WHERE u.role='SELLER' and u.isApprovedByAdmin =false  and u.HasVerifiedEmail=true")
    List<User> notApprovedSellersList();


    @Query("SELECT u FROM User  u WHERE u.role='SELLER' and u.isApprovedByAdmin =true")
    List<User> ApprovedSellersList();

    @Query("SELECT u FROM User u WHERE u.role='SELLER' and u.isApprovedByAdmin =true and u.first_name like  %:username%")
    List<User> GetRegisteredSellerByUsername(String username);

    @Query("""
            SELECT u 
            FROM User u 
            WHERE u.role = 'SELLER' 
            AND u.isApprovedByAdmin = true
            AND u.TotalRating>0 
            ORDER BY u.TotalRating DESC
            LIMIT 5
            """)
    List<User> findTop5RatedSellers();


    Optional<User> findByEmail(String email);

}
