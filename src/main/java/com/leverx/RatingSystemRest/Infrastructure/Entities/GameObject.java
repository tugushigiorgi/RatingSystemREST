package com.leverx.RatingSystemRest.Infrastructure.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameObject {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @Column(nullable = false, length = 100)
    public String title;

     @Column(nullable = false, length = 1000)
     public String text;

    @Column(nullable = false)
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
