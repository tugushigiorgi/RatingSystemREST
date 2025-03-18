package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Integer anonymousId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    private boolean Approved = false;

    @Column(nullable = false)
    private int rating;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
