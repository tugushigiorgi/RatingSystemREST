package com.leverx.RatingSystemRest.Infrastructure.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {

    @Id
    @GeneratedValue
    private Integer id;

    private String message;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    private boolean Approved = false;

    private int rating;



    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
