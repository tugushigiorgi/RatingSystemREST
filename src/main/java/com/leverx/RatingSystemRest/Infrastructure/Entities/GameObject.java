package com.leverx.RatingSystemRest.Infrastructure.Entities;

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
public class GameObject {




    @Id
    @GeneratedValue
    private Integer id;

    public String title;

    public String text;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate

    @Column(insertable = false)
    private LocalDateTime updated_at;

    //relationships

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "gameObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private GameObjectPicture picture;



}
