package com.leverx.RatingSystemRest.Infrastructure.Entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPhoto {


    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private String photoName;
    @Column(nullable = false)
    private String Url ;
    @Column(nullable = false)
    private String Extension;
    @Column(nullable = false)
    private float size  ;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;
}
