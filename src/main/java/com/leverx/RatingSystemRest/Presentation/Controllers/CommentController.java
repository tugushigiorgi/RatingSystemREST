package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.CommentService;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.CommentUpdateDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.addCommentDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
        CommentService commentService;
        public CommentController(CommentService commentService) {
            this.commentService = commentService;
        }



}
