package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Integer anonymousId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    private boolean approved = false;

    @Column(nullable = false)
    private int rating;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
