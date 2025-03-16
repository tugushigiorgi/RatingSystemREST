package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import com.leverx.RatingSystemRest.Infrastructure.Entities.UserRoleEnum;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.CommentRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentUpdateDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserReviewsDto;
import com.leverx.RatingSystemRest.Presentation.Dto.addCommentDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CommentService {

    private CommentRepository commentRepository;

    private UserRepository userRepository;


    public ResponseEntity<String> add(addCommentDto dto) {

        var findSeller = userRepository.findById(dto.sellerId);

        if (findSeller.isEmpty()) {

            return new ResponseEntity<>("seller not found", HttpStatus.BAD_REQUEST);

        }
        var currentSeller = findSeller.get();


        boolean hasMatchingAnonymousId = currentSeller.getComments().stream()
                .anyMatch(comment -> comment.getAnonymousId().equals(dto.getAnonymousId()));


        if (hasMatchingAnonymousId) {
            return new ResponseEntity<>("anonymous with given id  already submited review", HttpStatus.BAD_REQUEST);
        }




        var newComment = Comment.builder().Approved(false).created_at(LocalDateTime.now()).user(currentSeller).rating(dto.getReview()).message(dto.getComment()).anonymousId(dto.anonymousId).build();


        currentSeller.getComments().add(newComment);
        commentRepository.save(newComment);
        userRepository.save(currentSeller);
        return new ResponseEntity<>("comment added", HttpStatus.OK);


    }
    @Transactional
    public ResponseEntity<String> delete(int anonymousId, int commentId) {

        var findComment = commentRepository.findById(commentId);
        if (findComment.isEmpty()) {
            return new ResponseEntity<>("Comment not found", HttpStatus.BAD_REQUEST);
        }
        var currentComment = findComment.get();

        if (currentComment.getAnonymousId() != anonymousId) {
            return new ResponseEntity<>("Permission denied", HttpStatus.BAD_REQUEST);

        }
        commentRepository.deleteById(commentId);
        return new ResponseEntity<>("Deleted succesfully", HttpStatus.OK);


    }

    public ResponseEntity<String> update(CommentUpdateDto dto) {

        var findComment = commentRepository.findById(dto.getCommentId());
        if (findComment.isEmpty()) {
            return new ResponseEntity<>("Comment not found", HttpStatus.BAD_REQUEST);
        }
        var currentComment = findComment.get();

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


    public List<UserReviewsDto> getApprovedReviewsBySellerId(int sellerId) throws Exception {


        var findSeller = userRepository.findById(sellerId);

        if (findSeller.isEmpty()) {

            throw new Exception("Seller  not found");

        }
        var Reviews = commentRepository.sellersAllApprovedReviews(sellerId);
        return Reviews.stream().map(UserReviewsDto::toDto).toList();


    }


    public List<UserReviewsDto> getNotApprovedReviewsBySellerId(int sellerId) throws Exception {


        var findSeller = userRepository.findById(sellerId);

        if (findSeller.isEmpty()) {

            throw new Exception("Seller  not found");

        }
        var Reviews = commentRepository.sellersNotApprovedReviews(sellerId);
        return Reviews.stream().map(UserReviewsDto::toDto).toList();


    }


    public List<UserReviewsDto> getAllNotApprovedReviews() throws Exception {


        var getdata = commentRepository.AllNotApprovedReviews();
        return getdata.stream().map(UserReviewsDto::toDto).toList();


    }


    public ResponseEntity<String> ApproveUserReview(int commentId) {


        var getcomment = commentRepository.findById(commentId);
        if (getcomment.isEmpty()) {
            return new ResponseEntity<>("Comment not found", HttpStatus.BAD_REQUEST);
        }
        var currentSeller=userRepository.findById(getcomment.get().getUser().getId()).get();

        var userTotalApprovedComments = currentSeller.getComments().stream().filter(Comment::isApproved).toList();

        var CurrentRatingCount = userTotalApprovedComments.size();
        var CurrentRatingSum = SumCurrentRating(userTotalApprovedComments);

        var NewRating= (CurrentRatingSum+getcomment.get().getRating())/(CurrentRatingCount+1);
        currentSeller.setTotalRating(NewRating);

        userRepository.save(currentSeller);
        var currentComment = getcomment.get();
        currentComment.setApproved(true);
        commentRepository.save(currentComment);
        return new ResponseEntity<>("Comment Approved", HttpStatus.OK);


    }

    @Transactional
    public ResponseEntity<String> DeclineUserReview(int commentId) {


        var getcomment = commentRepository.findById(commentId);
        if (getcomment.isEmpty()) {
            return new ResponseEntity<>("Comment not found", HttpStatus.BAD_REQUEST);
        }


        commentRepository.deleteById(commentId);
        return new ResponseEntity<>("Comment Deleted", HttpStatus.OK);


    }


    public static double SumCurrentRating(List<Comment> comment) {
        return comment.stream()
                .mapToInt(Comment::getRating)
                .sum();


    }


}
