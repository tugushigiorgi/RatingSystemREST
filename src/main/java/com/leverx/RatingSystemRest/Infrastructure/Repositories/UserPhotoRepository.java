package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
}
