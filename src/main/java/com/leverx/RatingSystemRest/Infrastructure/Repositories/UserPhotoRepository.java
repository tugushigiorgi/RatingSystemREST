package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Integer> {



    @Query("SELECT c FROM UserPhoto c WHERE  c.user.id= :userId ")
     UserPhoto  getUserPhotoByUserId(int userId);




}
