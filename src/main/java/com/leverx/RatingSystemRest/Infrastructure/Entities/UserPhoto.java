package com.leverx.RatingSystemRest.Infrastructure.Entities;


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

    private String photoName;

    private String Url ;

    private String Extension;

    private float size  ;




    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
