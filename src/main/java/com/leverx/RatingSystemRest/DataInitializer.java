package com.leverx.RatingSystemRest;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import com.leverx.RatingSystemRest.Infrastructure.Entities.UserRoleEnum;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {


            User adminUser = new User();
            adminUser.setFirst_name("Admin");
            adminUser.setLast_name("admin");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setCreated_at(LocalDateTime.now());
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole(UserRoleEnum.ADMIN);
            adminUser.setHasVerifiedEmail(true);
            adminUser.setApprovedByAdmin(true);

            userRepository.save(adminUser);
            System.out.println("Admin user created successfully.");

        }
        System.out.println("-------------------------------------------");
        System.out.println("Admin login credentials:");
        System.out.println("Admin user email: " + "admin@gmail.com");
        System.out.println("Admin user password: " + "admin123");
        System.out.println("-------------------------------------------");

    }
}
