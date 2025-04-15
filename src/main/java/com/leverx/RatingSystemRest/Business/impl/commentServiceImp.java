package com.leverx.RatingSystemRest.Business.impl;


import com.leverx.RatingSystemRest.Business.Interfaces.commentService;
import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.CommentRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.CommentUpdateDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.addCommentDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
//TODO LOGER ANNOTATION
//TODO USE VAR WHENEVER POSSIBLE
public class commentServiceImp implements commentService {

    private CommentRepository commentRepository;

    private UserRepository userRepository;


    public ResponseEntity<String> add(addCommentDto dto) {

        var currentSeller = userRepository.findById(dto.sellerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
        boolean hasMatchingAnonymousId = currentSeller.getComments().stream().anyMatch(comment -> comment.getAnonymousId().equals(dto.getAnonymousId()));
        if (hasMatchingAnonymousId) {
            return new ResponseEntity<>("anonymous with given id  already submited review", HttpStatus.BAD_REQUEST);
        }
        var newComment = Comment.builder().approved(false).created_at(LocalDateTime.now()).user(currentSeller).rating(dto.getReview()).message(dto.getComment()).anonymousId(dto.anonymousId).build();
        currentSeller.getComments().add(newComment);
        commentRepository.save(newComment);
        userRepository.save(currentSeller);
        return new ResponseEntity<>("comment added", HttpStatus.OK);


    }

    @Transactional
    public ResponseEntity<String> delete(int anonymousId, int commentId) {

        var currentComment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));


        if (currentComment.getAnonymousId() != anonymousId) {
            return new ResponseEntity<>("Permission denied", HttpStatus.BAD_REQUEST);

        }
        commentRepository.deleteById(commentId);
        return new ResponseEntity<>("Deleted succesfully", HttpStatus.OK);


    }

    public ResponseEntity<String> update(CommentUpdateDto dto) {

        var currentComment = commentRepository.findById(dto.getCommentId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (currentComment.getAnonymousId() != dto.getAnonymousId()) {
            return new ResponseEntity<>("Permission denied", HttpStatus.BAD_REQUEST);

        }

        if (dto.getComment() != null && !dto.getComment().equals(currentComment.getMessage())) {

            currentComment.setMessage(dto.getComment());
        }
        if (dto.getReview() != currentComment.getRating()) {
            currentComment.setRating(dto.getReview());
        }

        commentRepository.save(currentComment);


        return new ResponseEntity<>("Updated succesfully", HttpStatus.OK);
    }


    public ResponseEntity<List<UserReviewsDto>> getApprovedReviewsBySellerId(int sellerId) {
        var seller = userRepository.findById(sellerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        var reviews = commentRepository.sellersAllApprovedReviews(sellerId);

        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var reviewDtos = reviews.stream().map(UserReviewsDto::toDto).toList();
        return ResponseEntity.ok(reviewDtos);
    }


    public List<UserReviewsDto> getNotApprovedReviewsBySellerId(int sellerId) throws Exception {
        var seller = userRepository.findById(sellerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        var Reviews = commentRepository.sellersNotApprovedReviews(sellerId);
        return Reviews.stream().map(UserReviewsDto::toDto).toList();


    }


    public List<UserReviewsDto> getAllNotApprovedReviews() {
        List<Comment> notApprovedReviews = commentRepository.AllNotApprovedReviews();
        if (notApprovedReviews == null || notApprovedReviews.isEmpty()) {
            return Collections.emptyList();
        }
        return notApprovedReviews.stream().map(UserReviewsDto::toDto).collect(Collectors.toList());
    }

    public ResponseEntity<String> approveUserReview(int commentId) {


        var getcomment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        var currentSeller = userRepository.findById(getcomment.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller not found"));


        var userTotalApprovedComments = currentSeller.getComments().stream().filter(Comment::isApproved).toList();

        var CurrentRatingCount = userTotalApprovedComments.size();
        var CurrentRatingSum = sumCurrentRating(userTotalApprovedComments);

        var NewRating = (CurrentRatingSum + getcomment.getRating()) / (CurrentRatingCount + 1);
        currentSeller.setTotalRating(NewRating);

        userRepository.save(currentSeller);

        getcomment.setApproved(true);
        commentRepository.save(getcomment);
        return new ResponseEntity<>("Comment Approved", HttpStatus.OK);


    }

    @Transactional
    public ResponseEntity<String> declineUserReview(int commentId) {
        var getcomment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment not found"));
        commentRepository.deleteById(commentId);
        return new ResponseEntity<>("Comment Deleted", HttpStatus.OK);
    }


    public static double sumCurrentRating(List<Comment> comment) {
        return comment.stream().mapToInt(Comment::getRating).sum();
    }


}
