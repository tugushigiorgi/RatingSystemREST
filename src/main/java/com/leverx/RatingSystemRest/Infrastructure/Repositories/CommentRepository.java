package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {


    @Query(value="SELECT c FROM Comment c WHERE c.user_id = :sellerId AND c. Approved = true",nativeQuery = true)
    List<Comment> sellersAllApprovedReviews(int sellerId);

    @Query(value ="SELECT c FROM Comment c WHERE c.user_id = :sellerId AND c. Approved = false ",nativeQuery = true)
    List<Comment> sellersNotApprovedReviews(int sellerId);


    @Query(value ="SELECT c FROM Comment c WHERE   Approved = false ",nativeQuery = true)
    List<Comment> AllNotApprovedReviews();



}
