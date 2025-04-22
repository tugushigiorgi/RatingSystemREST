package com.leverx.RatingSystemRest;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import com.leverx.RatingSystemRest.Infrastructure.Entities.UserRoleEnum;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer which inserts admin account at the first run.
 */
@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {

    if (userRepository.count() == 0) {
      User adminUser = new User();
      adminUser.setFirstName("Admin");
      adminUser.setLastName("admin");
      adminUser.setEmail("admin@gmail.com");
      adminUser.setCreatedAt(LocalDateTime.now());
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
