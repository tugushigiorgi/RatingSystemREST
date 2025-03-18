package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {


    @Query("SELECT c FROM Comment c WHERE c.user.id = :sellerId AND c. approved = true")
    List<Comment> sellersAllApprovedReviews(int sellerId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :sellerId AND c. approved = false ")
    List<Comment> sellersNotApprovedReviews(int sellerId);


    @Query("SELECT c FROM Comment c WHERE   c.approved = false ")
    List<Comment> AllNotApprovedReviews();



}
