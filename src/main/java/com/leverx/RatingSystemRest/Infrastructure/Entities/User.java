package com.leverx.RatingSystemRest.Infrastructure.Entities;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User  implements  UserDetails {


    @Id
    @GeneratedValue
    private Integer id;

    private String first_name;
    private String last_name;
    private String email;
    private String password;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;


    @Enumerated(EnumType.STRING)
    private UserRoleEnum  role;



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<GameObject> gameObjects=new ArrayList<>();



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments=new ArrayList<>();




    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)

    private UserPhoto photo;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Token token;



    public String fullName(){
        return first_name + " " + last_name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority( role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
