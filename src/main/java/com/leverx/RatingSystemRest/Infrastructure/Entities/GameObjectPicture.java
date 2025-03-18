package com.leverx.RatingSystemRest.Infrastructure.Entities;

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
public class GameObjectPicture {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(nullable = false)
    private String photoName;
    @Column(nullable = false)
    private String Url ;
    @Column(nullable = false)
    private String Extension;
    @Column(nullable = false)
    private float size ;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "gameobject_id")
    private GameObject gameObject;
}
