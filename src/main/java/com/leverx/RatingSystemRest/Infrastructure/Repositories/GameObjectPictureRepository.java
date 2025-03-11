package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObjectPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameObjectPictureRepository extends JpaRepository<GameObjectPicture, Integer> {
}
