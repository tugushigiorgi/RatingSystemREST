package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Token {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;
    private LocalDateTime expires_at;
    @Column(unique = true)
    private String token;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


}
