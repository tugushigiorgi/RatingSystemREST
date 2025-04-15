package com.leverx.RatingSystemRest.Presentation.Controllers;


import com.leverx.RatingSystemRest.Business.Interfaces.commentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
        private final commentService commentService;
        public CommentController(commentService commentService) {
            this.commentService = commentService;
        }



}
