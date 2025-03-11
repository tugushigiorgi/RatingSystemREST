package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameObjectRepository  extends JpaRepository<GameObject,Integer> {
}
