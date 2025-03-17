package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor

@AllArgsConstructor
@Data
public class GameObject {


    @Id
    @GeneratedValue
    private Integer id;

    public String title;

    public String text;

    public double price;


    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;


    @Column(insertable = false)
    private LocalDateTime updated_at;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @OneToOne(mappedBy = "gameObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private GameObjectPicture picture;


}
