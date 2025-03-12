package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity

public class Comment {

    @Id
    @GeneratedValue
    private Integer id;

    private String message;


    private Integer anonymousId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    private boolean Approved = false;

    private int rating;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
