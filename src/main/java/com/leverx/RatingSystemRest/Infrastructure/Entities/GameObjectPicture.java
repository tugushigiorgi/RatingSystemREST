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
public class GameObjectPicture {


    @Id
    @GeneratedValue
    private Integer id;

    private String photoName;

    private String Url ;

    private String Extension;

    private float size ;




    @OneToOne
    @JoinColumn(name = "gameobject_id")
    private GameObject gameObject;
}
