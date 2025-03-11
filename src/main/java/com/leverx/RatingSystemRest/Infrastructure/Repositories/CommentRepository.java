package com.leverx.RatingSystemRest.Infrastructure.Repositories;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
