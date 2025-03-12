package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameObjectRepository  extends JpaRepository<GameObject,Integer> {




    @Query("SELECT c FROM GameObject c WHERE c.user.id = :sellerId")
    List<GameObject> getGameObjectsBySellerId(int sellerId);


    @Query( "SELECT c FROM GameObject c WHERE c.title like  %:title%" )
    List<GameObject> getGameObjectsByTitle(String title);



}
